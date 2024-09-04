package fr.insee.bpm.metadata.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.UcqVariable;
import fr.insee.bpm.metadata.model.VariableType;
import fr.insee.bpm.metadata.reader.lunatic.ReaderUtils;

import java.util.List;

import static fr.insee.bpm.metadata.Constants.LABEL;
import static fr.insee.bpm.metadata.Constants.VALUE;

public class RadioCheckboxProcessor implements ComponentProcessor{

    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        String variableName = ReaderUtils.getVariableName(primaryComponent);
        UcqVariable ucqVarOne = new UcqVariable(variableName, group, VariableType.STRING);
        JsonNode modalitiesOne = primaryComponent.get("options");
        for (JsonNode modality : modalitiesOne){
            if (isLunaticV2) {
                ucqVarOne.addModality(modality.get(VALUE).asText(), modality.get(LABEL).get(VALUE).asText());
                continue;
            }
            ucqVarOne.addModality(modality.get(VALUE).asText(), modality.get(LABEL).asText());
        }
        metadataModel.getVariables().putVariable(ucqVarOne);
        variables.remove(variableName);
    }
}
