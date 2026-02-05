package fr.insee.bpm.metadata.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VariableTest {

    Variable variable;

    @BeforeEach
    void setUp() {
        variable = new Variable();
    }

    @Test
    void getGroupNameTest(){
        //GIVEN
        String groupName = "testGroup";
        variable.group = new Group(groupName);

        //WHEN + THEN
        Assertions.assertThat(variable.getGroupName()).isEqualTo(groupName);
    }

    @Test
    void getExpectedLengthTest(){
        //GIVEN
        variable.sasFormat = "3.test";

        //WHEN + THEN
        Assertions.assertThat(variable.getExpectedLength()).isEqualTo(3);
    }

    @Test
    void getExpectedLengthTest_noDot(){
        //GIVEN
        variable.sasFormat = "2";

        //WHEN + THEN
        Assertions.assertThat(variable.getExpectedLength()).isEqualTo(2);
    }
    @Test
    void getExpectedLengthTest_empty(){
        Assertions.assertThat(variable.getExpectedLength()).isEqualTo(1);
    }
}