package fr.insee.bpm.metadata.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.bpm.TestConstants;
import fr.insee.bpm.metadata.model.MetadataModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NoOpProcessorITest {

    private NoOpProcessor processor;
    private MetadataModel metadataModel;
    private JsonNode sequenceComponent;
    private JsonNode primaryComponent;
    private List<String> variables;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() throws IOException {
        // GIVEN
        metadataModel = new MetadataModel();

        // Initialization of variables remaining to add to the metadata model
        variables = new ArrayList<>(List.of("QSIMPLENUM","QSIMPLENUM2","VAR2"));
        // Initialization of a processor with an Input Number type component
        processor = new NoOpProcessor();
        File file = new File(TestConstants.UNIT_TESTS_DIRECTORY + "/lunatic/components/sequence.json");
        sequenceComponent = objectMapper.readTree(file);
    }

    @Test
    void testProcessDoesNothing() {
        // Clone parameters to test them after execution
        List<String> originalVariables = new ArrayList<>(variables);
        primaryComponent = sequenceComponent.get(0);

        // Call method and verify there's no side effects
        assertDoesNotThrow(() -> processor.process(primaryComponent, metadataModel.getRootGroup(), variables, metadataModel, true));

        // Check that variables list is unchanged
        assertEquals(originalVariables, variables, "The variables list should remain unchanged.");
    }
}
