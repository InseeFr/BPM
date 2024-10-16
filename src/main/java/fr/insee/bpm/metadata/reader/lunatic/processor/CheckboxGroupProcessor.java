package fr.insee.bpm.metadata.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.McqVariable;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.VariableType;
import fr.insee.bpm.metadata.reader.lunatic.LunaticUtils;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.bpm.metadata.Constants.LABEL;
import static fr.insee.bpm.metadata.Constants.VALUE;

public class CheckboxGroupProcessor implements ComponentProcessor {

    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        processCheckboxGroup(primaryComponent, group, variables, metadataModel, isLunaticV2);
    }

    /**
     * Process a checkbox group to create a boolean variable for each response
     * @param checkboxComponent : component representing a checkbox group
     * @param group : group to which the variables belong
     * @param variables : list of variables to be completed
     * @param metadataModel : metadata model of the questionnaire to be completed
     * @param isLunaticV2 : true if the Lunatic version is 2.3 or higher
     */
    private static void processCheckboxGroup(JsonNode checkboxComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        String variableName;
        JsonNode responses = checkboxComponent.get("responses");
        List<String> responsesName= new ArrayList<>();
        for (JsonNode response : responses){
            responsesName.add(LunaticUtils.getVariableName(response));
        }
        String questionName = LunaticUtils.findLongestCommonPrefix(responsesName);
        for (JsonNode response : responses){
            variableName = LunaticUtils.getVariableName(response);
            McqVariable mcqVariable = new McqVariable(variableName, group, VariableType.BOOLEAN);
            if (isLunaticV2) mcqVariable.setText(response.get(LABEL).get(VALUE).asText());
            if (!isLunaticV2) mcqVariable.setText(response.get(LABEL).asText());
            mcqVariable.setInQuestionGrid(true);
            mcqVariable.setQuestionName(questionName);
            metadataModel.getVariables().putVariable(mcqVariable);
            variables.remove(variableName);
        }
    }
}
