package fr.insee.bpm.metadata.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.Variable;
import fr.insee.bpm.metadata.model.VariableType;
import fr.insee.bpm.metadata.reader.lunatic.ReaderUtils;

import java.util.List;

public class InputNumberProcessor implements ComponentProcessor {

    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        String variableName = ReaderUtils.getVariableName(primaryComponent);
        if (primaryComponent.get("decimals").asInt()==0){
            metadataModel.getVariables().putVariable(new Variable(variableName, group, VariableType.INTEGER));
            variables.remove(variableName);
            return;
        }
        metadataModel.getVariables().putVariable(new Variable(variableName, group, VariableType.NUMBER));
        variables.remove(variableName);
    }
}
