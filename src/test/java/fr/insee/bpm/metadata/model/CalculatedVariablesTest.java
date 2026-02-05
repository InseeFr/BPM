package fr.insee.bpm.metadata.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class CalculatedVariablesTest {

    private CalculatedVariables calculatedVariables;

    public static final String VARIABLE_NAME = "test";


    @BeforeEach
    void setUp() {
        calculatedVariables = new CalculatedVariables();
    }

    @Test
    void putVariable() {
        //GIVEN
        CalculatedVariables.CalculatedVariable calculatedVariable = new CalculatedVariables.CalculatedVariable(VARIABLE_NAME, "");

        //WHEN
        calculatedVariables.putVariable(calculatedVariable);

        //THEN
        Assertions.assertThat(calculatedVariables).containsEntry(VARIABLE_NAME, calculatedVariable);
    }

    @Test
    void getVtlExpression() {
        //GIVEN
        String expectedVtlExpression = "testVtl";
        CalculatedVariables.CalculatedVariable calculatedVariable = new CalculatedVariables.CalculatedVariable(VARIABLE_NAME, expectedVtlExpression);
        calculatedVariables.put(VARIABLE_NAME, calculatedVariable);

        //WHEN
        String vtlExpression = calculatedVariables.getVtlExpression(VARIABLE_NAME);

        //THEN
        Assertions.assertThat(vtlExpression).isEqualTo(expectedVtlExpression);
    }

    @Test
    void getDependantVariables() {
        //GIVEN
        String dependantVariableName = "testDependant";
        CalculatedVariables.CalculatedVariable calculatedVariable = new CalculatedVariables.CalculatedVariable(VARIABLE_NAME, "");
        calculatedVariable.dependantVariables.add(dependantVariableName);
        calculatedVariables.put(VARIABLE_NAME, calculatedVariable);

        //WHEN
        List<String> actualDependantVariables = calculatedVariables.getDependantVariables(VARIABLE_NAME);

        //THEN
        Assertions.assertThat(actualDependantVariables).containsExactly(dependantVariableName);
    }
}