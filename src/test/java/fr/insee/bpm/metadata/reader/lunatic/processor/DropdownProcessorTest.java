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

class DropdownProcessorTest {
    private DropdownProcessor processor;
    private MetadataModel metadataModel;
    private JsonNode dropdownComponents;
    private JsonNode primaryComponent;
    private List<String> variables;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() throws IOException {
        // GIVEN
        metadataModel = new MetadataModel();

        // Initialization of variables remaining to add to the metadata model
        variables = new ArrayList<>(List.of("LISTEDROUL","VAR2"));
        // Initialization of a processor with an Input Number type component
        processor = new DropdownProcessor();
        File file = new File(TestConstants.UNIT_TESTS_DIRECTORY + "/lunatic/components/dropdown.json");
        dropdownComponents = objectMapper.readTree(file);
    }

    @Test
    void testProcess_AddsCorrectModalities() throws Exception {
        //WHEN
        primaryComponent = dropdownComponents.get(0);
        processor.process(primaryComponent, metadataModel.getRootGroup(), variables, metadataModel, true);

        // Check if the variables is correctly added to the metadata model
        assertTrue(metadataModel.getVariables().getVariables().containsKey("LISTEDROUL"));

        // Retrieve UcqVariable and check nullity
        UcqVariable ucqVariable = (UcqVariable) metadataModel.getVariables().getVariables().get("LISTEDROUL");
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
        assertFalse(variables.contains("LISTEDROUL"));
    }

    @Test
    void testProcess_UnknownVariable() {
        //GIVEN
        primaryComponent = dropdownComponents.get(1);

        // WHEN
        processor.process(primaryComponent, metadataModel.getRootGroup(), variables, metadataModel, true);

        //THEN
        // We add the variable in the metadata model anyway
        assertNotNull(metadataModel.getVariables().getVariables().get("UNKNOWN_UCQVAR"));
        // But the list remained unchanged
        assertEquals(2, variables.size());
    }




}
