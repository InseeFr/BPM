package fr.insee.bpm.metadata.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UcqVariableTest {

    UcqVariable ucqVariable;

    @BeforeEach
    void setUp() {
        ucqVariable = new UcqVariable();
    }

    @Test
    void addModalityTest_ucqModality(){
        //GIVEN
        UcqModality ucqModality = new UcqModality();

        //WHEN
        ucqVariable.addModality(ucqModality);

        //THEN
        Assertions.assertThat(ucqVariable.modalities).containsExactly(ucqModality);
    }

    @Test
    void addModalityTest_valueText(){
        //GIVEN
        String expectedText = "test";
        String expectedValue = "testValue";

        //WHEN
        ucqVariable.addModality(expectedValue, expectedText);

        //THEN
        Assertions.assertThat(ucqVariable.modalities).hasSize(1);
        Assertions.assertThat(ucqVariable.modalities.getFirst().text).isEqualTo(expectedText);
        Assertions.assertThat(ucqVariable.modalities.getFirst().value).isEqualTo(expectedValue);
    }

    @Test
    void addModalityTest_valueTextVariableName(){
        //GIVEN
        String expectedText = "test";
        String expectedValue = "testValue";
        String expectedVariableName = "testVariable";

        //WHEN
        ucqVariable.addModality(expectedValue, expectedText, expectedVariableName);

        //THEN
        Assertions.assertThat(ucqVariable.modalities).hasSize(1);
        Assertions.assertThat(ucqVariable.modalities.getFirst().text).isEqualTo(expectedText);
        Assertions.assertThat(ucqVariable.modalities.getFirst().value).isEqualTo(expectedValue);
        Assertions.assertThat(ucqVariable.modalities.getFirst().variableName).isEqualTo(expectedVariableName);
    }

    @Test
    void getValuesTest(){
        //GIVEN
        String expectedValue = "testValue";
        ucqVariable.modalities.add(new UcqModality(expectedValue, "test"));

        //WHEN + THEN
        Assertions.assertThat(ucqVariable.getValues()).containsExactly(expectedValue);

    }

    @Test
    void getModalityNamesTest(){
        //GIVEN
        String expectedName = "testName";
        ucqVariable.modalities.add(new UcqModality("testValue", "testText", expectedName));

        //WHEN + THEN
        Assertions.assertThat(ucqVariable.getModalityNames()).containsExactly(expectedName);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void getModalityFromValueTest(boolean isMultiple){
        //GIVEN
        String value = "testValue";
        ucqVariable.modalities.add(new UcqModality(value, "testText"));
        if(isMultiple){
            ucqVariable.modalities.add(new UcqModality(value, "testText2"));
        }

        //WHEN
        UcqModality ucqModality = ucqVariable.getModalityFromValue(value);

        //THEN
        Assertions.assertThat(ucqModality.value).isEqualTo(value);
    }

    @Test
    void getModalityFromValueTest_empty(){
        Assertions.assertThat(ucqVariable.getModalityFromValue("test")).isNull();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void getModalityFromNameTest(boolean isMultiple){
        //GIVEN
        String variableName = "testVariable";
        ucqVariable.modalities.add(new UcqModality("testValue", "testText", variableName));
        if(isMultiple){
            ucqVariable.modalities.add(new UcqModality("testValue2", "testText2", variableName));
        }

        //WHEN
        UcqModality ucqModality = ucqVariable.getModalityFromName(variableName);

        //THEN
        Assertions.assertThat(ucqModality.variableName).isEqualTo(variableName);
    }

    @Test
    void getModalityFromNameTest_empty(){
        Assertions.assertThat(ucqVariable.getModalityFromName("test")).isNull();
    }
}