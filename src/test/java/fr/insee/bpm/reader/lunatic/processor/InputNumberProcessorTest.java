package fr.insee.bpm.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.bpm.TestConstants;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.VariableType;
import fr.insee.bpm.metadata.reader.lunatic.ComponentLunatic;
import fr.insee.bpm.metadata.reader.lunatic.processor.InputNumberProcessor;
import fr.insee.bpm.metadata.reader.lunatic.processor.SimpleVariableProcessor;
import fr.insee.bpm.reader.lunatic.stubs.MetadataModelStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InputNumberProcessorTest {

    private InputNumberProcessor processor;
    private MetadataModel metadataModel;
    private JsonNode InputNumberComponents;
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
        processor = new InputNumberProcessor();
        /*This file contains three components :
        - the first one an input number with decimals
        - the second one an input number without decimals
        - the third one a component with an unknown variable name
        */
        File file = new File(TestConstants.UNIT_TESTS_DIRECTORY + "/lunatic/processor/input_number_component.json");
        InputNumberComponents = objectMapper.readTree(file);
    }

    @Test
    void testProcess_NumberType() {
        //GIVEN
        primaryComponent = InputNumberComponents.get(0);

        // WHEN
        processor.process(primaryComponent, metadataModel.getRootGroup(), variables, metadataModel, true);

        // THEN
        assertNotNull(metadataModel.getVariables().getVariables().get("QSIMPLENUM"));
        assertTrue(metadataModel.getVariables().getVariables().get("QSIMPLENUM").getType() == VariableType.NUMBER);
        assertFalse(variables.contains("QSIMPLENUM"), "The variable should be removed from the list");
    }

    @Test
    void testProcess_IntegerType() {
        //GIVEN
        primaryComponent = InputNumberComponents.get(1);

        // WHEN
        processor.process(primaryComponent, metadataModel.getRootGroup(), variables, metadataModel, true);

        // THEN
        assertNotNull(metadataModel.getVariables().getVariables().get("QSIMPLENUM2"));
        assertTrue(metadataModel.getVariables().getVariables().get("QSIMPLENUM2").getType() == VariableType.INTEGER);
        assertFalse(variables.contains("QSIMPLENUM2"), "The variable should be removed from the list");
    }

    @Test
    void testProcess_UnknownVariable() {
        //GIVEN
        primaryComponent = InputNumberComponents.get(2);

        // WHEN
        processor.process(primaryComponent, metadataModel.getRootGroup(), variables, metadataModel, true);

        //THEN
        // We add the variable in the metadata model anyway
        assertNotNull(metadataModel.getVariables().getVariables().get("UNKNOWN_NUM"));
        // But the list remained unchanged
        assertEquals(3, variables.size());
    }







}
