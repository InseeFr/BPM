package fr.insee.bpm.metadata.reader;

import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.VariablesMap;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ReaderUtilsTest {

    @Test
    void getMetadataFromDDIAndLunatic_should_add_filter_variables_for_web() throws Exception {
        // GIVEN
        InputStream ddiStream = new FileInputStream(
                Path.of("src/test/resources/unit_tests/ddi/ddi-log-2021-x21-web.xml").toFile()
        );
        InputStream lunaticStream = new FileInputStream(
                Path.of("src/test/resources/unit_tests/lunatic/log2021x21_web.json").toFile());

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


        // Assert que FILTER_RESULT_CADR n'a pas été envoyé dans RACINE
        assertEquals(cadrGroup, filterGroup, "FILTER_RESULT_CADR should be at the same group as CADR");
    }


    @Test
    void getMetadataFromDDIAndLunatic_should_add_missing_variables_for_tel() throws Exception {
        InputStream ddiStream = new FileInputStream(
                Path.of("src/test/resources/unit_tests/ddi/ddi-log-2021-x21-web.xml").toFile()
        );
        InputStream lunaticStream  = new FileInputStream(
                Path.of("src/test/resources/unit_tests/lunatic/log2021x21_tel.json").toFile());

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
                Path.of("src/test/resources/unit_tests/ddi/ddi-log-2021-x21-web.xml").toFile()
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



}