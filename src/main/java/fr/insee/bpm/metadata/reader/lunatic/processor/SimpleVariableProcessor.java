package fr.insee.bpm.metadata.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.Variable;
import fr.insee.bpm.metadata.reader.lunatic.ComponentLunatic;
import fr.insee.bpm.metadata.reader.lunatic.LunaticUtils;

import java.util.List;

public class SimpleVariableProcessor implements ComponentProcessor {

    private final ComponentLunatic componentType;

    public SimpleVariableProcessor(ComponentLunatic componentType) {
        this.componentType = componentType;
    }

    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        String variableName = LunaticUtils.getVariableName(primaryComponent);
        metadataModel.getVariables().putVariable(new Variable(variableName, group, componentType.getType()));
        variables.remove(variableName);
    }
}
