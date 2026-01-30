package fr.insee.bpm.metadata.reader.lunatic;

import fr.insee.bpm.TestConstants;
import fr.insee.bpm.metadata.Constants;
import fr.insee.bpm.metadata.model.CalculatedVariables;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.VariablesMap;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticReaderITest {

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

    @Test
    void getMetadataFromLunatic_should_add_missing_variables_for_tel() throws FileNotFoundException {
        // GIVEN
        MetadataModel metadataModel = LunaticReader.getMetadataFromLunatic(
                new FileInputStream(
                        lunaticSamplesPath.resolve("log2021x21_tel.json").toFile())
        );

        // THEN
        assertNotNull(metadataModel);

        VariablesMap vars = metadataModel.getVariables();

        assertTrue(vars.hasVariable("CADR_MISSING"));
    }

    @Test
    void getMetadataFromLunatic_should_add_filter_variables_for_web() throws FileNotFoundException {
        // GIVEN
        MetadataModel metadataModel = LunaticReader.getMetadataFromLunatic(
                new FileInputStream(
                        lunaticSamplesPath.resolve("log2021x21_web.json").toFile())
        );

        // THEN
        assertNotNull(metadataModel);

        VariablesMap vars = metadataModel.getVariables();
        assertTrue(vars.hasVariable("FILTER_RESULT_CADR"));
    }


    @Test
    void getMetadataFromLunatic_should_add_link_variables() throws FileNotFoundException {
        // GIVEN
        MetadataModel metadataModel = LunaticReader.getMetadataFromLunatic(
                new FileInputStream(
                        lunaticSamplesPath.resolve("lunaticSAMPLETEST-DATAONLY-v1.json").toFile()
                )
        );

        // THEN
        assertNotNull(metadataModel);

        VariablesMap vars = metadataModel.getVariables();

        // Precondition: LIENS variable must exist in Lunatic file
        assertTrue(vars.hasVariable(Constants.LIENS));

        // Expected behavior: link variables (LIENx) must be added
        for (int i = 1; i < Constants.MAX_LINKS_ALLOWED; i++) {
            assertTrue(
                    vars.hasVariable(Constants.LIEN + i),
                    "Missing variable: " + Constants.LIEN + i
            );
        }
    }


}
