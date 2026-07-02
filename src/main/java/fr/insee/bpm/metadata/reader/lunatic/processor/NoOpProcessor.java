package fr.insee.bpm.metadata.reader.lunatic.processor;

import tools.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;

import java.util.List;

public class NoOpProcessor implements ComponentProcessor{

    @Override
    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        //Do nothing intentionally. Use when the component is recognized (for example, a sequence) but no treatment is needed
    }
}
