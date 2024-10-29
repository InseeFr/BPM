package fr.insee.bpm.metadata.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.UcqVariable;
import fr.insee.bpm.metadata.model.VariableType;
import fr.insee.bpm.metadata.reader.lunatic.LunaticUtils;

import java.util.List;

import static fr.insee.bpm.metadata.Constants.LABEL;
import static fr.insee.bpm.metadata.Constants.VALUE;

public class RadioCheckboxProcessor implements ComponentProcessor{

    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        String variableName = LunaticUtils.getVariableName(primaryComponent);
        UcqVariable ucqVarOne = new UcqVariable(variableName, group, VariableType.STRING);
        JsonNode modalitiesOne = primaryComponent.get("options");
        for (JsonNode modality : modalitiesOne){
            String label ="";
            if (isLunaticV2) {
                label = modality.get(LABEL).get(VALUE).asText().replace("\"","");
                ucqVarOne.addModality(modality.get(VALUE).asText(), label);
                continue;
            }
            label = modality.get(LABEL).asText().replace("\"","");
            ucqVarOne.addModality(modality.get(VALUE).asText(), label);
        }
        metadataModel.getVariables().putVariable(ucqVarOne);
        variables.remove(variableName);
    }
}
