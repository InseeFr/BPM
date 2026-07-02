package fr.insee.bpm.metadata.reader.lunatic.processor;

import tools.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.UcqVariable;
import fr.insee.bpm.metadata.model.VariableType;
import fr.insee.bpm.metadata.reader.lunatic.LunaticUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static fr.insee.bpm.metadata.Constants.LABEL;
import static fr.insee.bpm.metadata.Constants.VALUE;

@Slf4j
public class DropdownProcessor implements ComponentProcessor {

    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        String variableName = LunaticUtils.getVariableName(primaryComponent);
        UcqVariable ucqVar = new UcqVariable(variableName, group, VariableType.STRING);
        JsonNode modalities = primaryComponent.get("options");
        boolean isModalityRead = false;
        for (JsonNode modality : modalities){
            String value = modality.get(VALUE).asText();
            // For the label we have to check the Json structure because it has changed over time
            // But we support only the last versions(v2+), otherwise the modalities are empty strings and we log a warning
            String label ="";
            if (modality.get(LABEL).has(VALUE)) {
                label = modality.get(LABEL).get(VALUE).asText();
                isModalityRead =true;
            }
            // Remove unnecessary double quotes
            label = label.replace("\"", "");
            ucqVar.addModality(value, label);
        }
        if (!isModalityRead){
            log.warn("Can't read the modalities for Ucq variable {}. Check Json Lunatic structure.", variableName);
        }
        metadataModel.getVariables().putVariable(ucqVar);
        variables.remove(variableName);
    }
}
