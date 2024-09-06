package fr.insee.bpm.metadata.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.reader.lunatic.LunaticReader;

import java.util.List;

public class QuestionProcessor implements ComponentProcessor {

    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        JsonNode components = primaryComponent.get("components");
        for (JsonNode component : components) {
            LunaticReader.addResponsesAndMissing(component, group, variables, metadataModel);
        }
    }
}
