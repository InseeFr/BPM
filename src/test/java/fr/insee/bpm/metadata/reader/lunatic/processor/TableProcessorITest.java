package fr.insee.bpm.metadata.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.bpm.TestConstants;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.SpecType;
import fr.insee.bpm.metadata.model.Variable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TableProcessorITest {

    private TableProcessor processor;
    private MetadataModel metadataModel;
    private JsonNode tableComponents;
    private List<String> variables;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        // GIVEN
        metadataModel = new MetadataModel();
        Map<SpecType,String> version = new HashMap<>();
        version.put(SpecType.LUNATIC,"3.0.0");
        metadataModel.setSpecVersions(version);

        // Initialization of variables remaining to add to the metadata model
        variables = new ArrayList<>(List.of("TABCODELIST1D11","TABCODELIST1D21","TABCODELIST1D31","VAR2"));
        // Initialization of a processor with an Input Number type component
        processor = new TableProcessor();
        File file = new File(TestConstants.UNIT_TESTS_DIRECTORY + "/lunatic/components/table.json");
        tableComponents = objectMapper.readTree(file);
    }

    @Test
    void testProcess_Table1DAddsVariableToMetadataModel() {
        //GIVEN
        JsonNode primaryComponent = tableComponents.get(1);

        // WHEN
        processor.process(primaryComponent, metadataModel.getRootGroup(), variables, metadataModel, true);

        // THEN
        // Check if the variable is added to the metadata model
        assertNotNull(metadataModel.getVariables().getVariables().get("TABCODELIST1D11"));
        assertNotNull(metadataModel.getVariables().getVariables().get("TABCODELIST1D21"));
        assertNotNull(metadataModel.getVariables().getVariables().get("TABCODELIST1D31"));

        // Retrieve UcqVariable and check nullity
        Variable variable = metadataModel.getVariables().getVariables().get("TABCODELIST1D21");
        assertNotNull(variable);

        // Check if the variable is removed from variables remaining to add to metadata model
        assertFalse(variables.contains("TABCODELIST1D11")||variables.contains("TABCODELIST1D21")||variables.contains("TABCODELIST1D31"));
    }

}
