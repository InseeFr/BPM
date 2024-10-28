package fr.insee.bpm.metadata.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.bpm.TestConstants;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.UcqModality;
import fr.insee.bpm.metadata.model.UcqVariable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RadioCheckboxProcessorTest {

    private RadioCheckboxProcessor processor;
    private MetadataModel metadataModel;
    private JsonNode radioCheckboxComponents;
    private List<String> variables;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        // GIVEN
        metadataModel = new MetadataModel();

        // Initialization of variables remaining to add to the metadata model
        variables = new ArrayList<>(List.of("BOUTONRADI","CASECOCHER","VAR2"));
        // Initialization of a processor with an Input Number type component
        processor = new RadioCheckboxProcessor();
        File file = new File(TestConstants.UNIT_TESTS_DIRECTORY + "/lunatic/processor/radio_checkbox_components.json");
        radioCheckboxComponents = objectMapper.readTree(file);
    }

    @Test
    void testProcess_RadioAddsVariableToMetadataModel() {
        //GIVEN
        JsonNode primaryComponent = radioCheckboxComponents.get(1);

        // WHEN
        processor.process(primaryComponent, metadataModel.getRootGroup(), variables, metadataModel, true);

        // THEN
        // Check if the variable is added to the metadata model
        assertNotNull(metadataModel.getVariables().getVariables().get("CASECOCHER"));

        // Retrieve UcqVariable and check nullity
        UcqVariable ucqVariable = (UcqVariable) metadataModel.getVariables().getVariables().get("CASECOCHER");
        assertNotNull(ucqVariable);

        // Check modalities size
        List<UcqModality> modalities = ucqVariable.getModalities();
        assertEquals(3, modalities.size());

        // Check modalities' values and label
        assertEquals("1", modalities.get(0).getValue());
        assertEquals("Modalité 1", modalities.get(0).getText());

        assertEquals("2", modalities.get(1).getValue());
        assertEquals("Modalité 2", modalities.get(1).getText());

        assertEquals("3", modalities.get(2).getValue());
        assertEquals("Modalité 3", modalities.get(2).getText());
        // Check if the variable is removed from variables remaining to add to metadata model
        assertFalse(variables.contains("CASECOCHER"));
    }

    @Test
    void testProcess_CheckboxOneAddsVariableToMetadataModel() {
        //GIVEN
        JsonNode primaryComponent = radioCheckboxComponents.get(0);

        // WHEN
        processor.process(primaryComponent, metadataModel.getRootGroup(), variables, metadataModel, true);

        // THEN
        // Check if the variable is added to the metadata model
        assertNotNull(metadataModel.getVariables().getVariables().get("BOUTONRADI"));

        // Retrieve UcqVariable and check nullity
        UcqVariable ucqVariable = (UcqVariable) metadataModel.getVariables().getVariables().get("BOUTONRADI");
        assertNotNull(ucqVariable);

        // Check modalities size
        List<UcqModality> modalities = ucqVariable.getModalities();
        assertEquals(3, modalities.size());

        // Check modalities' values and label
        assertEquals("1", modalities.get(0).getValue());
        assertEquals("Modalité 1", modalities.get(0).getText());

        assertEquals("2", modalities.get(1).getValue());
        assertEquals("Modalité 2", modalities.get(1).getText());

        assertEquals("3", modalities.get(2).getValue());
        assertEquals("Modalité 3", modalities.get(2).getText());
        // Check if the variable is removed from variables remaining to add to metadata model
        assertFalse(variables.contains("BOUTONRADI"));
    }

    @Test
    void testProcess_UnknownVariable() {
        //GIVEN
        JsonNode primaryComponent = radioCheckboxComponents.get(2);

        // WHEN
        processor.process(primaryComponent, metadataModel.getRootGroup(), variables, metadataModel, true);

        //THEN
        // We add the variable in the metadata model anyway
        assertNotNull(metadataModel.getVariables().getVariables().get("UNKNOWN_UCQVAR"));
        // But the list remained unchanged
        assertEquals(3, variables.size());
    }

}
