package fr.insee.bpm.metadata.reader.lunatic.processor;

import tools.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.reader.lunatic.LunaticReader;

import java.util.List;

public class QuestionProcessor implements ComponentProcessor {

    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        JsonNode components = primaryComponent.get("components");
        for (JsonNode component : components) {
            addResponsesAndMissing(component, group, variables, metadataModel);
        }
    }

    void addResponsesAndMissing(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel){
        LunaticReader.addResponsesAndMissing(primaryComponent, group, variables, metadataModel);
    }
}
