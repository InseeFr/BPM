package fr.insee.bpm.metadata.reader;

import fr.insee.bpm.TestConstants;
import fr.insee.bpm.metadata.Constants;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.VariablesMap;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ReaderUtilsTest {
    static final Path lunaticSamplesPath = Path.of(TestConstants.UNIT_TESTS_DIRECTORY, "lunatic");
    static final Path ddiSamplesPath = Path.of(TestConstants.UNIT_TESTS_DIRECTORY, "ddi");


    @Test
    void getMetadataFromDDIAndLunatic_should_add_filter_variables_for_web() throws Exception {
        // GIVEN
        InputStream ddiStream = new FileInputStream(
                ddiSamplesPath.resolve("ddi-log-2021-x21-web.xml").toFile()
        );
        InputStream lunaticStream = new FileInputStream(
                lunaticSamplesPath.resolve("log2021x21_web.json").toFile());

        // WHEN
        MetadataModel metadataModel = ReaderUtils.getMetadataFromDDIAndLunatic(
                "file://ddi-log-2021-x21-web.xml",
                ddiStream,
                lunaticStream
        );

        // THEN
        VariablesMap vars = metadataModel.getVariables();

        assertTrue(vars.hasVariable("CADR"));
        assertTrue(vars.hasVariable("FILTER_RESULT_CADR"));

        Group cadrGroup = vars.getVariable("CADR").getGroup();
        Group filterGroup = vars.getVariable("FILTER_RESULT_CADR").getGroup();


        // Assert that FILTER_RESULT_CADR n'a pas été envoyé dans RACINE
        assertEquals(cadrGroup, filterGroup, "FILTER_RESULT_CADR should be at the same group as CADR");
    }


    @Test
    void getMetadataFromDDIAndLunatic_should_add_missing_variables_for_tel() throws Exception {
        InputStream ddiStream = new FileInputStream(
                ddiSamplesPath.resolve("ddi-log-2021-x21-web.xml").toFile()
        );
        InputStream lunaticStream  = new FileInputStream(
                lunaticSamplesPath.resolve("log2021x21_tel.json").toFile());

        MetadataModel metadataModel = ReaderUtils.getMetadataFromDDIAndLunatic(
                "file://ddi-log-2021-x21-web.xml",
                ddiStream,
                lunaticStream
        );

        VariablesMap vars = metadataModel.getVariables();
        assertTrue(vars.hasVariable("CADR"));
        assertTrue(vars.hasVariable("CADR_MISSING"));
    }

    @Test
    void getMetadataFromDDIAndLunatic_noLunaticFile_should_returnDDIVariablesOnly() throws Exception {
        // GIVEN
        InputStream ddiStream = new FileInputStream(
                ddiSamplesPath.resolve("ddi-log-2021-x21-web.xml").toFile()
        );

        // WHEN
        MetadataModel metadataModel = ReaderUtils.getMetadataFromDDIAndLunatic(
                "file://ddi-log-2021-x21-web.xml",
                ddiStream,
                null
        );

        // THEN
        VariablesMap vars = metadataModel.getVariables();

        assertTrue(vars.hasVariable("CADR"));
        assertFalse(vars.hasVariable("CADR_MISSING"));
        assertFalse(vars.hasVariable("FILTER_RESULT_CADR"));
    }

    @Test
    void getMetadataFromDDIAndLunatic_should_add_link_variables() throws Exception {
        // GIVEN
        InputStream ddiStream = new FileInputStream(
                ddiSamplesPath.resolve("ddi-SAMPLETEST-DATAONLY-v1.xml").toFile()
        );
        InputStream lunaticStream = new FileInputStream(
                lunaticSamplesPath.resolve("lunaticSAMPLETEST-DATAONLY-v1.json").toFile()
        );

        // WHEN
        MetadataModel metadataModel = ReaderUtils.getMetadataFromDDIAndLunatic(
                "file://ddi-SAMPLETEST-DATAONLY-v1.xml",
                ddiStream,
                lunaticStream
        );

        // THEN
        VariablesMap vars = metadataModel.getVariables();

        // Precondition: LIENS variable must exist
        assertTrue(vars.hasVariable(Constants.LIENS));

        // Expected behavior: link variables (LIENx) must be added
        for (int i = 1; i < Constants.MAX_LINKS_ALLOWED; i++) {
            assertTrue(
                    vars.hasVariable(Constants.LIEN + i),
                    "Missing variable: " + Constants.LIEN + i
            );
        }

    }


    @Test
    void getMetadataFromDDIAndLunatic_noLunaticFile_should_not_add_link_variables() throws Exception {
        InputStream ddiStream = new FileInputStream(
                ddiSamplesPath.resolve("ddi-log-2021-x21-web.xml").toFile()
        );

        MetadataModel metadataModel = ReaderUtils.getMetadataFromDDIAndLunatic(
                "file://ddi-log-2021-x21-web.xml",
                ddiStream,
                null
        );

        VariablesMap vars = metadataModel.getVariables();

        assertFalse(vars.hasVariable(Constants.LIENS));
        assertFalse(vars.hasVariable(Constants.LIEN + "1"));
    }



}