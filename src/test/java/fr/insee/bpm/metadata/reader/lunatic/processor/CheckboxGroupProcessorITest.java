package fr.insee.bpm.metadata.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.bpm.TestConstants;
import fr.insee.bpm.metadata.model.McqVariable;
import fr.insee.bpm.metadata.model.MetadataModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CheckboxGroupProcessorITest {

    private CheckboxGroupProcessor processor;
    private MetadataModel metadataModel;
    private JsonNode checkboxGroupComponents;
    private List<String> variables;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        // GIVEN
        metadataModel = new MetadataModel();

        // Initialization of variables remaining to add to the metadata model
        variables = new ArrayList<>(List.of("QUESTIONCH1","QUESTIONCH2","QUESTIONCH3","VAR2"));
        // Initialization of a processor with an Input Number type component
        processor = new CheckboxGroupProcessor();
        File file = new File(TestConstants.UNIT_TESTS_DIRECTORY + "/lunatic/components/checkbox_group.json");
        checkboxGroupComponents = objectMapper.readTree(file);
    }

    @Test
    void testProcess_CheckboxGroupAddsVariableToMetadataModel() {
        //GIVEN
        JsonNode primaryComponent = checkboxGroupComponents.get(0);

        // WHEN
        processor.process(primaryComponent, metadataModel.getRootGroup(), variables, metadataModel, true);

        // THEN
        // Check if the variable is added to the metadata model
        assertNotNull(metadataModel.getVariables().getVariables().get("QUESTIONCH1"));
        assertNotNull(metadataModel.getVariables().getVariables().get("QUESTIONCH2"));
        assertNotNull(metadataModel.getVariables().getVariables().get("QUESTIONCH3"));

        // Retrieve UcqVariable and check nullity
        McqVariable mcqVariable = (McqVariable) metadataModel.getVariables().getVariables().get("QUESTIONCH2");
        assertNotNull(mcqVariable);

        // Check modalities size
        assertEquals("Modalité 2", mcqVariable.getText());

        // Check if the variable is removed from variables remaining to add to metadata model
        assertFalse(variables.contains("QUESTIONCH1")||variables.contains("QUESTIONCH2")||variables.contains("QUESTIONCH3"));
    }


}
