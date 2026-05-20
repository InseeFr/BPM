package fr.insee.bpm.metadata.reader.lunatic.processor;

import tools.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.reader.lunatic.LunaticReader;

import java.util.List;

import static fr.insee.bpm.metadata.Constants.COMPONENTS;

public class PairwiseLinksProcessor implements ComponentProcessor{

    public void process(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel, boolean isLunaticV2) {
        //Iterate on the components in the body of a pairwise links to find the responses
        JsonNode components = primaryComponent.get(COMPONENTS);
        if (components.isArray()){
            for (JsonNode component : components) {
                LunaticReader.addResponsesAndMissing(component, group, variables, metadataModel);
            }
        }
    }
}
