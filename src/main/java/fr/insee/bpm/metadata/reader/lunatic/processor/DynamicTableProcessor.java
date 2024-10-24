package fr.insee.bpm.metadata.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.reader.lunatic.ComponentLunatic;
import fr.insee.bpm.metadata.reader.lunatic.LunaticUtils;

import java.util.List;

import static fr.insee.bpm.metadata.Constants.COMPONENTS;

public class DynamicTableProcessor implements ComponentProcessor {

    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        JsonNode components = primaryComponent.get(COMPONENTS);
        // We have to create a group specific to the dynamic table
        // The name is the name of the first response collected
        String groupName = LunaticUtils.getFirstResponseName(components);
        Group dynTabGroup = LunaticUtils.getNewGroup(metadataModel,groupName,group);
        for (JsonNode component : components) {
            ComponentLunatic componentType = ComponentLunatic.fromJsonName(component.get("componentType").asText());
            ComponentProcessor processor = ComponentProcessorFactory.getProcessor(componentType);
            processor.process(component, dynTabGroup, variables, metadataModel, isLunaticV2);
        }
    }

}
