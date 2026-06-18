package fr.insee.bpm.metadata.reader.lunatic.processor;

import tools.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.Constants;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.Variable;
import fr.insee.bpm.metadata.reader.lunatic.ComponentLunatic;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoundaboutProcessor implements ComponentProcessor{

    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        //First we determine in which group the variables will be added
        //We rely on "iterations" attribute
        //We parse the VTL formula to extract the variable on which the roundabout is based
        String vtlFormula = primaryComponent.get("iterations").get("value").asText();
        String variableName = extractVariableName(vtlFormula);
        Variable loopVariable = metadataModel.getVariables().getVariable(variableName);
        Group groupRoundAbout = loopVariable.getGroup();
        //We will put all the variables of the roundabout in the same group that loopVariable
        JsonNode components = primaryComponent.get(Constants.COMPONENTS);
        for (JsonNode component : components) {
            ComponentLunatic componentType = ComponentLunatic.fromJsonName(component.get("componentType").asText());
            ComponentProcessor processor = ComponentProcessorFactory.getProcessor(componentType);
            processor.process(component, groupRoundAbout, variables, metadataModel, isLunaticV2);
        }
    }

    static String extractVariableName(String input) {
        // Regex to capture what's between parenthesis
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            // Get the content between the parenthesis
            return matcher.group(1);
        }
        return null;
    }
}
