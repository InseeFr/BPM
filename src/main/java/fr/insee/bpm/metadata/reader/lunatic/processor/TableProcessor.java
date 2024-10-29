package fr.insee.bpm.metadata.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.reader.lunatic.LunaticReader;

import java.util.List;

import static fr.insee.bpm.metadata.Constants.COMPONENT_TYPE;

public class TableProcessor implements ComponentProcessor{

    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        iterateOnTableBody(primaryComponent, group, variables, metadataModel, isLunaticV2);
    }

    /**
     * Iterate on the components in the body of a table to find the responses
     * @param tableComponent : component representing a table
     * @param group : group to which the variables belong
     * @param variables : list of variables to be completed
     * @param metadataModel : metadata model of the questionnaire to be completed
     * @param isLunaticV2 : true if the Lunatic version is 2.3 or higher
     */
    private static void iterateOnTableBody(JsonNode tableComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        // In the case of a table component we have to iterate on the body components to find the responses
        // The body is a nested array of arrays
        // In Lunatic 2.2 and lower the body is called cells
        JsonNode body = isLunaticV2 ? tableComponent.get("body") : tableComponent.get("cells");
        for(JsonNode arr : body){
            if (arr.isArray()){
                for (JsonNode cell : arr){
                    if (cell.has(COMPONENT_TYPE)) {
                        LunaticReader.addResponsesAndMissing(cell, group, variables, metadataModel);
                    }
                }
            }
        }
    }
}
