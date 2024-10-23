package fr.insee.bpm.reader.lunatic.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.reader.lunatic.ComponentLunatic;
import fr.insee.bpm.metadata.reader.lunatic.processor.SimpleVariableProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleVariableProcessorTest {

    private SimpleVariableProcessor processor;
    private MetadataModel metadataModel;
    private JsonNode primaryComponent;
    private List<String> variables;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        // GIVEN
        metadataModel = new MetadataModel();

        // Initialization of variables remaining to add to the metadata model
        variables = new ArrayList<>(List.of("QSIMPLETXT","VAR2"));
        // Initialization of a processor with an Input type component
        processor = new SimpleVariableProcessor(ComponentLunatic.INPUT);
    }

    @Test
    void testProcess_AddsVariableToMetadataModel() throws JsonProcessingException {
        //GIVEN
        String json = "{\"id\": \"lz9wh5gy\",\"page\": \"2\",\"response\": {\"name\": \"QSIMPLETXT\"},\"mandatory\": false,\"maxLength\": 249,\"componentType\": \"Input\"}";
        primaryComponent = objectMapper.readTree(json);

        // WHEN
        processor.process(primaryComponent, metadataModel.getRootGroup(), variables, metadataModel, true);

        // Check if the variable is added to the metadata model
        assertNotNull(metadataModel.getVariables().getVariables().get("QSIMPLETXT"));
        // Check if the variable is removed from variables remaining to add to metadata model
        assertFalse(variables.contains("QSIMPLETXT"));
    }

    @Test
    void testProcess_IfVariableNameNotInList() throws JsonProcessingException {
        //GIVEN
        // The primary component contains a variable not identified in the list of variables to add in the metadata model
        String json = "{\"id\": \"lz9wh5gy\",\"page\": \"2\",\"response\": {\"name\": \"UnknownVariable\"},\"mandatory\": false,\"maxLength\": 249,\"componentType\": \"Input\"}";
        primaryComponent = objectMapper.readTree(json);

        // WHEN
        processor.process(primaryComponent, metadataModel.getRootGroup(), variables, metadataModel, true);

        // We add the variable in the metadata model anyway
        assertNotNull(metadataModel.getVariables().getVariables().get("UnknownVariable"));
        // But the list remained unchanged
        assertEquals(2, variables.size());
    }

    @Test
    void testProcessor_InstantiationWithInvalidComponentLunatic() {
        assertThrows(IllegalArgumentException.class,()->new SimpleVariableProcessor(ComponentLunatic.ROUNDABOUT));
    }

}
