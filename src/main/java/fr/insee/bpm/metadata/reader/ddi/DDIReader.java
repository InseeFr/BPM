package fr.insee.bpm.metadata.reader.ddi;

import fr.insee.bpm.exceptions.MetadataParserException;
import fr.insee.bpm.metadata.Constants;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.McqVariable;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.Sequence;
import fr.insee.bpm.metadata.model.UcqVariable;
import fr.insee.bpm.metadata.model.Variable;
import fr.insee.bpm.metadata.model.VariableType;
import fr.insee.bpm.metadata.model.VariablesMap;
import fr.insee.bpm.utils.xsl.SaxonTransformer;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Log4j2
public class DDIReader {

	private DDIReader() {
		//Utility class
	}

	// DDI should stay in KW and use DOM (not Jaxb)

	/**
	 * This method apply the XSLT_STRUCTURED_VARIABLES transformation to the DDI,
	 * then reads the output xml to return the variables. The XML file generated is
	 * written in the system temporary folder with the name 'variables.xml'. This file is
	 * deleted after being used or when the virtual machine terminates.
	 *
	 * @param ddiUrlString : Path to the DDI file.
	 *
	 * @return The variables found in the DDI.
	 * @throws MetadataParserException
	 */
	public static MetadataModel getMetadataFromDDI(String ddiUrlString, InputStream ddiInputStream) throws MetadataParserException {

		try {
			// Path of the output 'variables.xml' temp file
			File variablesFile = File.createTempFile("variables", ".xml");
			variablesFile.deleteOnExit();
			Path variablesTempFilePath = variablesFile.toPath();

			transformDDI(ddiUrlString, ddiInputStream, variablesTempFilePath);

			MetadataModel metadataModel = readVariables(variablesTempFilePath);
			//Files.delete(variablesFile.toPath());
			return metadataModel;
		}

		catch (MalformedURLException e) {
			log.error(String.format("Error when converting file path '%s' to an URL.", ddiUrlString), e);
			return null;
		} catch (IOException e) {
			log.error("Unable to write temp file.", e);
			return null;
		} catch (SAXException | ParserConfigurationException e) {
			log.error("Unable to read Variables in DDI file.", e);
			return null;
		}
	}

	/**
	 * Apply the XSLT_STRUCTURED_VARIABLES transformation.
	 *
	 * @param ddiUrlString            : URL of the DDI file.
	 * @param variablesFilePath : Path of the 'variables.xml' file to be generated.
	 */
	private static void transformDDI(String ddiUrlString, InputStream ddiInputStream, Path variablesFilePath) throws IOException {
		SaxonTransformer saxonTransformer = new SaxonTransformer();
		saxonTransformer.xslTransform(ddiUrlString, ddiInputStream, Constants.XSLT_STRUCTURED_VARIABLES, variablesFilePath);
	}

	/**
	 * Parse the transformed xml file (using XmlFileReader), and read the
	 * transformed xml to return a VariablesMap.
	 *
	 * @param variablesFilePath Path to the transformed xml file.
	 * @return The variables described in the file.
	 * @throws MetadataParserException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	private static MetadataModel readVariables(Path variablesFilePath)
			throws MetadataParserException, SAXException, IOException, ParserConfigurationException {
		MetadataModel metadataModel = new MetadataModel();

		// Parse
		Element root = readXmlFile(variablesFilePath);

		// Get XML groups
		NodeList groupElements = root.getChildNodes();

		// temporary save the root group name to normalize it
		String rootGroupName = null;

		for (int i = 0; i < groupElements.getLength(); i++) {
			try {
				Node groupNode = groupElements.item(i);
				if (nodeIsElementWithName(groupNode, "Group")) {
					// Get the group name
					Element groupElement = (Element) groupNode;

					String groupName = groupElement.getAttribute("name");
					String parentGroupName = groupElement.getAttribute("parent");
					// To be normalized later : we don't want to create a group of variables for pairwise links.
					// Pairwise links scope are the individuals
					if (groupName.equals(Constants.PAIRWISE_GROUP_NAME)){
						// Instead we assign temporarily the variables inside pairwise links group to the root group
						getVariablesInGroup(metadataModel.getVariables(), groupNode, metadataModel.getRootGroup(), metadataModel.getSequences());
						continue;
					}

					// Store the group
					Group group;
					if (StringUtils.isEmpty(parentGroupName)) {
						rootGroupName = groupName;
						group = metadataModel.getRootGroup();
					} else {
						group = new Group(groupName, parentGroupName);
						metadataModel.putGroup(group);
					}

					// Variables in the group
					getVariablesInGroup(metadataModel.getVariables(), groupNode, group, metadataModel.getSequences());

				}
			} catch (NullPointerException e) {
				log.error(String.format("Missing field in mandatory information for variable %s",
						((Element) groupElements.item(i)).getAttribute("name")));
			}

			for (String groupName : metadataModel.getSubGroupNames()) {
				Group group = metadataModel.getGroup(groupName);
				if (group.getParentName().equals(rootGroupName)) {
					group.setParentName(Constants.ROOT_GROUP_NAME);
				}
			}

		}
		// Normalize the root group name
		if (rootGroupName == null) {
			log.debug("Failed to identify the root group while reading variables files: {}", variablesFilePath);
		}
		return metadataModel;
	}



	private static void getVariablesInGroup(VariablesMap variablesMap, Node groupNode, Group group, List<Sequence> sequences) {
		NodeList variableNodes = groupNode.getChildNodes();
		for (int j = 0; j < variableNodes.getLength(); j++) {
			Node variableNode = variableNodes.item(j);
			if (nodeIsElementWithName(variableNode, "Variable")) {
				addVariableToVariablesMap(variablesMap, group, variableNode, sequences);
			}
		}
	}

	private static void addVariableToVariablesMap(VariablesMap variablesMap, Group group, Node variableNode, List<Sequence> sequences) {
		Element variableElement = (Element) variableNode;

		// Variable name, type and size
		String variableName = getFirstChildValue(variableElement, "Name");
		VariableType variableType = VariableType.valueOf(getFirstChildValue(variableElement, "Format"));
		String variableLength = getFirstChildValue(variableElement, "Size");
		String sequenceName= getFirstChildAttribute(variableElement, "Sequence","name");

		Node questionName = getFirstChildNode(variableElement, "QuestionName");
		Node valuesElement = getFirstChildNode(variableElement, "Values");
		Node questionType = getFirstChildNode(variableElement, "QuestionType");

		if (sequenceName != null){
			Sequence sequence = new Sequence(sequenceName);
			if (sequences.isEmpty() || !sequences.contains(sequence)){
				sequences.add(sequence);
			}
		}

		if (valuesElement != null) {
			UcqVariable variable = new UcqVariable(variableName, group, variableType, variableLength);
			if (questionName != null) {
				variable.setQuestionName(questionName.getTextContent());
			} /*else if (mcqElement != null) {
				variable.setQuestionName(mcqElement.getTextContent());
				variable.setInQuestionGrid(true);
			}*/
			NodeList valueElements = valuesElement.getChildNodes();
			addValues(variable, valueElements);
			variablesMap.putVariable(variable);
		} else if (questionType != null && questionType.getTextContent().equals("MCQ")) {
			McqVariable variable = new McqVariable(variableName, group, variableType, variableLength);
			variable.setQuestionName(questionName.getTextContent());
			variable.setInQuestionGrid(true);
			variable.setText(getFirstChildValue(variableElement, "Label"));
			variablesMap.putVariable(variable);
		} else {
			Variable variable = new Variable(variableName, group, variableType, variableLength);
			if (questionName != null) {
				variable.setQuestionName(questionName.getTextContent());
			} else {
				variable.setQuestionName(variableName);
			}
			variablesMap.putVariable(variable);
		}
	}

	private static void addValues(UcqVariable variable, NodeList valueElements) {
		for (int k = 0; k < valueElements.getLength(); k++) {
			Node valueElement = valueElements.item(k);
			if (nodeIsElementWithName(valueElement, "Value")) {
				variable.addModality(valueElement.getTextContent(),
						((Element) valueElement).getAttribute("label"));

			}
		}
	}

	private static boolean nodeIsElementWithName(Node groupNode, String name) {
		return name.equals(groupNode.getNodeName()) && groupNode.getNodeType() == Node.ELEMENT_NODE;
	}
	private static Element readXmlFile(Path variablesFilePath)
			throws ParserConfigurationException, SAXException, IOException, MetadataParserException {
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
				"com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		DocumentBuilder builder = factory.newDocumentBuilder();
		try(InputStream inputStream = Files.newInputStream(variablesFilePath)){
			Document document = builder.parse(inputStream);
			if (document == null)
				throw new MetadataParserException("Can't read DDI - DDI is null");
			return document.getDocumentElement();
		}
	}

	private static String getFirstChildValue(Element variableElement, String childTagName) {
		Node child = getFirstChildNode(variableElement, childTagName);
		if (child == null){
			return null;
		}
		return child.getTextContent();
	}

	private static String getFirstChildAttribute(Element variableElement, String childTagName, String attribute){
		Node child = getFirstChildNode(variableElement, childTagName);
		if (child == null) {
			return null;
		}
		return child.hasAttributes() ? child.getAttributes().getNamedItem(attribute).getTextContent() : null;
	}

	private static Node getFirstChildNode(Element variableElement, String childTagName) {
		NodeList children = variableElement.getElementsByTagName(childTagName);
		if (children == null){
			return null;
		}
		return children.item(0);
	}

}
