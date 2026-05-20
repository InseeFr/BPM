package fr.insee.bpm.metadata.reader.lunatic.processor;

import tools.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.Variable;
import fr.insee.bpm.metadata.reader.lunatic.ComponentLunatic;
import fr.insee.bpm.metadata.reader.lunatic.LunaticUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SimpleVariableProcessor implements ComponentProcessor {

    private final ComponentLunatic componentType;

    public SimpleVariableProcessor(ComponentLunatic componentType) {
        // Check if the component type is authorized
        if(!isValidComponentType(componentType)){
            throw new IllegalArgumentException("Invalid component type for SimpleVariableProcessor: " + componentType);
        }
        this.componentType = componentType;
    }

    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        String variableName = LunaticUtils.getVariableName(primaryComponent);
        if (!variables.contains(variableName)){
            log.warn("The variable {} was not found in variables list of the json lunatic specification",variableName);
        }
        metadataModel.getVariables().putVariable(new Variable(variableName, group, componentType.getType()));
        variables.remove(variableName);
    }

    private boolean isValidComponentType(ComponentLunatic componentType) {
        // Only these types of components are valid for this processor
        return componentType == ComponentLunatic.INPUT ||
                componentType == ComponentLunatic.DATE_PICKER ||
                componentType == ComponentLunatic.TEXT_AREA ||
                componentType == ComponentLunatic.DURATION ||
                componentType == ComponentLunatic.CHECKBOX_BOOLEAN ||
                componentType == ComponentLunatic.SUGGESTER;
    }

}
