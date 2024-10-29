package fr.insee.bpm.metadata.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import static fr.insee.bpm.metadata.Constants.COMPONENT_TYPE;

@Log4j2
public class UnknownComponentProcessor implements ComponentProcessor{

    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        log.warn(String.format("%s component type not recognized", primaryComponent.get(COMPONENT_TYPE).asText()));
    }

}
