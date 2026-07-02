package fr.insee.bpm.metadata.reader.lunatic.processor;

import tools.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;

import java.util.List;

public interface ComponentProcessor {

    void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2);

}
