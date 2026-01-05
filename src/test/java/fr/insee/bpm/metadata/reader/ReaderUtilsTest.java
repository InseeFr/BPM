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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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

        assertThat(vars.hasVariable("CADR")).isTrue();
        assertThat(vars.hasVariable("FILTER_RESULT_CADR")).isTrue();

        Group cadrGroup = vars.getVariable("CADR").getGroup();
        Group filterGroup = vars.getVariable("FILTER_RESULT_CADR").getGroup();


        // Assert that FILTER_RESULT_CADR n'a pas été envoyé dans RACINE
        assertThat(filterGroup)
                .as("FILTER_RESULT_CADR should be at the same group as CADR")
                .isEqualTo(cadrGroup);
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
        assertThat(vars.hasVariable("CADR")).isTrue();
        assertThat(vars.hasVariable("CADR_MISSING")).isTrue();
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

        assertThat(vars.hasVariable("CADR")).isTrue();
        assertThat(vars.hasVariable("CADR_MISSING")).isFalse();
        assertThat(vars.hasVariable("FILTER_RESULT_CADR")).isFalse();
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
        assertThat(vars.hasVariable(Constants.LIENS)).isTrue();

        // Expected behavior: link variables (LIENx) must be added
        for (int i = 1; i < Constants.MAX_LINKS_ALLOWED; i++) {
            assertThat(vars.hasVariable(Constants.LIEN + i))
                    .as("Missing variable: %s", Constants.LIEN + i)
                    .isTrue();
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

        assertThat(vars.hasVariable(Constants.LIENS)).isFalse();
        assertThat(vars.hasVariable(Constants.LIEN + "1")).isFalse();
    }



}