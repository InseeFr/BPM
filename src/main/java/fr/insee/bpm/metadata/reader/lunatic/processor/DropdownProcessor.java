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

public class DropdownProcessor implements ComponentProcessor {

    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        String variableName = LunaticUtils.getVariableName(primaryComponent);
        UcqVariable ucqVar = new UcqVariable(variableName, group, VariableType.STRING);
        JsonNode modalities = primaryComponent.get("options");
        for (JsonNode modality : modalities){
            String value = modality.get(VALUE).asText();
            String label = modality.get(LABEL).get(VALUE).asText();
            // Remove unnecessary double quotes
            label = label.replace("\"", "");
            ucqVar.addModality(value, label);
        }
        metadataModel.getVariables().putVariable(ucqVar);
        variables.remove(variableName);
    }
}
