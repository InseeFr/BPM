package fr.insee.bpm.metadata.reader.lunatic;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.Constants;
import fr.insee.bpm.metadata.model.*;
import fr.insee.bpm.metadata.reader.lunatic.processor.ComponentProcessor;
import fr.insee.bpm.metadata.reader.lunatic.processor.ComponentProcessorFactory;
import fr.insee.bpm.utils.json.JsonReader;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fr.insee.bpm.metadata.Constants.*;

@Log4j2
public class LunaticReader {

	private static final String BINDING_DEPENDENCIES = "bindingDependencies";
	private static final String VARIABLES = "variables";
	private static final String MISSING_RESPONSE = "missingResponse";
	private static final String LUNATIC_MODEL_VERSION= "lunaticModelVersion";
	private static final String EXCEPTION_MESSAGE = "Unable to read Lunatic questionnaire file: {}";

	private LunaticReader() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Read the lunatic questionnaire given to get VTL expression of calculated
	 * variables.
	 *
	 * @param lunaticFile Path to a lunatic questionnaire file.
	 * @return A CalculatedVariables map.
	 */
	public static CalculatedVariables getCalculatedFromLunatic(InputStream lunaticFile) {
		try {
			JsonNode rootNode = JsonReader.read(lunaticFile);
			String lunaticModelVersion = rootNode.get(LUNATIC_MODEL_VERSION).asText();
			boolean isLunaticV2 = LunaticUtils.compareVersions(lunaticModelVersion, "2.3.0") > 0;

			CalculatedVariables calculatedVariables = new CalculatedVariables();

			JsonNode variablesNode = rootNode.get(VARIABLES);
			variablesNode.forEach(variableNode -> {
				if (variableNode.get("variableType").asText().equals("CALCULATED")) {
					String formula = isLunaticV2 ? variableNode.get("expression").get(VALUE).asText().replace("\r\n","")
							: variableNode.get("expression").asText().replace("\r\n","");
					CalculatedVariables.CalculatedVariable calculatedVariable = new CalculatedVariables.CalculatedVariable(variableNode.get("name").asText(),
							formula);
					JsonNode dependantVariablesNode = variableNode.get(BINDING_DEPENDENCIES);
					if (dependantVariablesNode != null) {
						dependantVariablesNode.forEach(name -> calculatedVariable.addDependantVariable(name.asText()));
					}
					calculatedVariables.putVariable(calculatedVariable);
				}
			});

			return calculatedVariables;

		} catch (IOException e) {
			log.error(EXCEPTION_MESSAGE, lunaticFile);
			return new CalculatedVariables();
		}
	}

	/**
	 * Read the lunatic file to get the names of the _MISSING variables and the
	 * collected variables added by Eno which are not present in the DDI
	 *
	 * @param lunaticFile Path to a lunatic questionnaire file.
	 * @return A List of String.
	 */
	public static List<String> getMissingVariablesFromLunatic(InputStream lunaticFile) {
		try {
			JsonNode rootNode = JsonReader.read(lunaticFile);
			List<String> variables = new ArrayList<>();
			List<String> varsEno = Arrays.asList(Constants.getEnoVariables());

			JsonNode variablesNode = rootNode.get(VARIABLES);
			variablesNode.forEach(variableNode -> variables.add(variableNode.get("name").asText()));
			return variables.stream()
					.filter(varToRead -> varToRead.endsWith(MISSING_SUFFIX) || varsEno.contains(varToRead)).toList();

		} catch (IOException e) {
			log.error(EXCEPTION_MESSAGE, lunaticFile);
			return Collections.emptyList();
		}
	}

	/**
	 * Read the lunatic file to get the names of the FILTER_RESULT variables which
	 * are not present in the DDI
	 *
	 * @param lunaticFile Path to a lunatic questionnaire file.
	 * @return A List of String.
	 */
	public static List<String> getFilterResultFromLunatic(InputStream lunaticFile) {
		try {
			JsonNode rootNode = JsonReader.read(lunaticFile);
			List<String> variables = new ArrayList<>();

			JsonNode variablesNode = rootNode.get(VARIABLES);
			variablesNode.forEach(variableNode -> variables.add(variableNode.get("name").asText()));
			return variables.stream().filter(variable -> variable.startsWith(FILTER_RESULT_PREFIX)).toList();

		} catch (IOException e) {
			log.error(EXCEPTION_MESSAGE, lunaticFile);
			return Collections.emptyList();
		}
	}

	public static String getLunaticModelVersion(InputStream lunaticFile){
		try {
			JsonNode rootNode = JsonReader.read(lunaticFile);
			return rootNode.get(LUNATIC_MODEL_VERSION).toString();

		} catch (IOException e) {
			log.error(EXCEPTION_MESSAGE, lunaticFile);
			return "";
		}
	}

	/**
	 * This method extracts return the variables of a questionnaire without reading
	 * a DDI file. It should be used only when the DDI is not available.
	 *
	 * @param lunaticFile : Path to a Lunatic specification file.
	 * @return The variables found in the Lunatic specification.
	 */
	public static MetadataModel getMetadataFromLunatic(InputStream lunaticFile) {
		JsonNode rootNode;
		try {
			rootNode = JsonReader.read(lunaticFile);
			List<String> variables = new ArrayList<>();
			JsonNode variablesNode = rootNode.get(VARIABLES);
			variablesNode.forEach(newVar -> variables.add(newVar.get("name").asText()));
			// Root group is created in VariablesMap constructor
			MetadataModel metadataModel = new MetadataModel();
			metadataModel.putSpecVersions(SpecType.LUNATIC,rootNode.get(LUNATIC_MODEL_VERSION).asText());
			Group rootGroup = metadataModel.getRootGroup();
			iterateOnComponents(rootNode, variables, metadataModel, rootGroup);

			// We iterate on root components to identify variables belonging to root group
			JsonNode rootComponents = rootNode.get(COMPONENTS);
			for (JsonNode comp : rootComponents) {
				addResponsesAndMissing(comp, rootGroup, variables, metadataModel);
			}
			// We need to add the filter results
			List<String> varToRemove = new ArrayList<>();
			for (String variable : variables){
				if (variable.startsWith(FILTER_RESULT_PREFIX)){
					LunaticUtils.addLunaticVariable(metadataModel, variable, Constants.FILTER_RESULT_PREFIX, VariableType.BOOLEAN);
					varToRemove.add(variable);
				}
			}
			varToRemove.forEach(variables::remove);
			// We add the remaining (not identified in any loops nor root) variables to the root group
			variables.forEach(
					varName -> metadataModel.getVariables().putVariable(new Variable(varName, rootGroup, VariableType.STRING)));
			return metadataModel;
		} catch (IOException e) {
			log.error(EXCEPTION_MESSAGE, lunaticFile);
			return null;
		}
	}

	/**
	 * This method iterates on an array of components to extract the variables present in loops and get their group.
	 * @param rootNode : node containing the components we want to iterate on
	 * @param variables : variables list to be completed
	 * @param metadataModel : metadata model of the questionnaire to be completed
	 * @param parentGroup : group of rootNode
	 */
	private static void iterateOnComponents(JsonNode rootNode, List<String> variables, MetadataModel metadataModel, Group parentGroup) {
		JsonNode componentsNode = rootNode.get(COMPONENTS);
		if (componentsNode.isArray()) {
			for (JsonNode component : componentsNode) {
				if (component.get(COMPONENT_TYPE).asText().equals("Loop")) {
					if (component.has("lines")) {
						processPrimaryLoop(variables, metadataModel, parentGroup, component);
					}
					if (component.has("iterations")) {
						Group group = processDependingLoop(variables, metadataModel, parentGroup, component);
						iterateOnComponents(component,variables, metadataModel,group);
					}
				}
			}
		}
	}

	/**
	 * This method processes the primary loop and creates a group with the name of the first response
	 * @param variables : variables list to be completed
	 * @param metadataModel : metadata model of the questionnaire to be completed
	 * @param parentGroup : parent group of the loop
	 * @param component : loop component
	 */
	private static void processPrimaryLoop(List<String> variables, MetadataModel metadataModel, Group parentGroup, JsonNode component) {
		JsonNode primaryComponents = component.get(COMPONENTS);
		//We create a group only with the name of the first response
		//Then we add all the variables found in response to the newly created group
		String groupName = LunaticUtils.getFirstResponseName(primaryComponents);
		Group group = LunaticUtils.getNewGroup(metadataModel, groupName, parentGroup);
		for (JsonNode primaryComponent : primaryComponents) {
			addResponsesAndMissing(primaryComponent, group, variables, metadataModel);
		}
	}

	/**
	 * This method processes the loop depending on variables and creates a group with the name of loop dependencies
	 * @param variables : variables list to be completed
	 * @param metadataModel : metadata model of the questionnaire to be completed
	 * @param parentGroup : parent group of the loop
	 * @param component : loop component
	 * @return the group corresponding to the loop
	 */
	private static Group processDependingLoop(List<String> variables, MetadataModel metadataModel, Group parentGroup, JsonNode component) {
		JsonNode loopDependencies = component.get("loopDependencies");
		String groupName;
		if (!loopDependencies.isEmpty()) {
			StringBuilder groupNameBuilder = new StringBuilder(loopDependencies.get(0).asText());
			for (int i = 1; i < loopDependencies.size(); i++) {
				groupNameBuilder.append("_").append(loopDependencies.get(i).asText());
			}
			groupName = groupNameBuilder.toString();
		} else {
			int i = 1;
			groupName = "UNNAMED_" + i;
			List<String> groups = metadataModel.getGroupNames();
			while (groups.contains(groupName)) {
				i++;
				groupName = "UNNAMED_" + i;
			}
		}
		Group group = LunaticUtils.getNewGroup(metadataModel, groupName, parentGroup);
		iterateOnComponentsToFindResponses(component, variables, metadataModel, group);
		return group;
	}

	/**
	 * Adds variables to the metadata model (it infers type of variables from the component type)
	 * Checks Lunatic version to adapt to the different ways of writing the JSON
	 *
	 * @param primaryComponent : a component of the questionnaire
	 * @param group : the group to which the variables belong
	 * @param variables : list of variables to be completed
	 * @param metadataModel : metadata model of the questionnaire to be completed
	 */
	public static void addResponsesAndMissing(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel) {
		//We read the name of the collected variables in response(s)
		//And we deduce the variable type by looking at the component that encapsulate the variable
		ComponentLunatic componentType = ComponentLunatic.fromJsonName(primaryComponent.get(COMPONENT_TYPE).asText());
		boolean isLunaticV2 = LunaticUtils.compareVersions(metadataModel.getSpecVersions().get(SpecType.LUNATIC), "2.3.0") > 0;
		ComponentProcessor processor = ComponentProcessorFactory.getProcessor(componentType);
		processor.process(primaryComponent, group, variables, metadataModel, isLunaticV2);

		//We also had the missing variable if it exists (only one missing variable even if multiple responses)
		addMissingVariable(primaryComponent, group, variables, metadataModel);
	}



	/**
	 * Add the missing variable defined in the component if present
	 * @param component : a questionnaire component
	 * @param group : group to which the variables belong
	 * @param variables : list of variables to be completed
	 * @param metadataModel : metadata model of the questionnaire to be completed
	 */
	private static void addMissingVariable(JsonNode component, Group group, List<String> variables, MetadataModel metadataModel) {
		if (component.has(MISSING_RESPONSE)){
			String missingVariable = component.get(MISSING_RESPONSE).get("name").asText();
			metadataModel.getVariables().putVariable(new Variable(missingVariable, group, VariableType.STRING));
			variables.remove(missingVariable);
		}
	}

	private static void iterateOnComponentsToFindResponses(JsonNode node, List<String> variables, MetadataModel metadataModel, Group group) {
		JsonNode components = node.get(COMPONENTS);
		if (components.isArray()){
			for (JsonNode component : components) {
				addResponsesAndMissing(component, group, variables, metadataModel);
			}
		}
	}

	/**
	 * Read the lunatic file and returns a String containing the questionnaire model
	 * id
	 *
	 * @param lunaticFile : Path to a Lunatic specification file.
	 * @return the questionnaire model id
	 */
	public static String getQuestionnaireModelId(InputStream lunaticFile) {
		JsonNode rootNode;
		try {
			rootNode = JsonReader.read(lunaticFile);
			return rootNode.get("id").asText();
		} catch (IOException e) {
			log.error(EXCEPTION_MESSAGE, lunaticFile);
			return null;
		}
	}

}
