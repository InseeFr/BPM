package fr.insee.bpm.metadata.reader.lunatic;

import fr.insee.bpm.TestConstants;
import fr.insee.bpm.metadata.model.CalculatedVariables;
import fr.insee.bpm.metadata.model.MetadataModel;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticReaderTest {

    static final Path lunaticSamplesPath = Path.of(TestConstants.UNIT_TESTS_DIRECTORY, "lunatic");

    @Test
    void readLogX21TelLunaticFile() throws FileNotFoundException {
        //
        CalculatedVariables calculatedVariables = LunaticReader.getCalculatedFromLunatic(
                new FileInputStream(lunaticSamplesPath.resolve("log2021x21_tel.json").toFile()));

        //
        assertNotNull(calculatedVariables);
        //
        assertTrue(calculatedVariables.containsKey("AGE"));
        assertEquals(
                "if (FUTURANNIVERSAIRE) then cast((cast(AGEMILLESIME,integer) - 1),integer) else cast(AGEMILLESIME,integer)",
                calculatedVariables.getVtlExpression("AGE"));
        assertEquals(
                List.of("FUTURANNIVERSAIRE", "AGEMILLESIME", "DATENAIS"),
                calculatedVariables.getDependantVariables("AGE"));
        //
        assertTrue(calculatedVariables.containsKey("ANNEENQ"));
        assertEquals(
                "cast(current_date(),string,\"YYYY\")",
                calculatedVariables.getVtlExpression("ANNEENQ"));
        assertTrue(calculatedVariables.getDependantVariables("ANNEENQ").isEmpty());

    }
    
    @Test
    void readLogX22WebLunaticFile() throws FileNotFoundException {
        //
        CalculatedVariables calculatedVariables = LunaticReader.getCalculatedFromLunatic(
                new FileInputStream(lunaticSamplesPath.resolve("log2021x22_web.json").toFile()));

        //
        assertNotNull(calculatedVariables);
        assertTrue(calculatedVariables.containsKey("S2_MAA1AT"));
       
    }


    @Test
    //Same test  with DDI [2 groups, 463 variables]
    //Update test with newer version of Lunatic (log2021x21_web.json uses an old version)
    void readVariablesFromLogX21WebLunaticFile() throws FileNotFoundException {
        //
        MetadataModel variables = LunaticReader.getMetadataFromLunatic(
                new FileInputStream(lunaticSamplesPath.resolve("log2021x21_web.json").toFile()));

        //
        assertNotNull(variables);
        assertEquals(2,variables.getGroupsCount());
        assertEquals(683, variables.getVariables().getVariables().size());
       
    }

}
