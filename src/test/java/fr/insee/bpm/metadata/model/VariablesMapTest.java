package fr.insee.bpm.metadata.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VariablesMapTest {

    VariablesMap variablesMap;

    @BeforeEach
    void setUp() {
        variablesMap = new VariablesMap();
    }

    @Test
    void putVariableTest(){
        //GIVEN
        String variableName = "testVariable";
        Variable variable = new Variable(variableName, new Group(), VariableType.STRING);

        //WHEN
        variablesMap.putVariable(variable);

        //THEN
        Assertions.assertThat(variablesMap.variables).containsEntry(variableName, variable);
    }

    @Test
    void putVariableTest_empty(){
        //GIVEN
        Variable variable = new Variable();

        //WHEN
        variablesMap.putVariable(variable);

        //THEN
        Assertions.assertThat(variablesMap.variables).isEmpty();
    }

    @Test
    void removeVariableTest(){
        //GIVEN
        String variableName = "testVariable";
        Variable variable = new Variable(variableName, new Group(), VariableType.STRING);
        variablesMap.getVariables().put(variableName, variable);

        //WHEN
        variablesMap.removeVariable(variableName);

        //THEN
        Assertions.assertThat(variablesMap.variables).doesNotContainKey(variableName);
    }

    @Test
    void getVariableTest(){
        //GIVEN
        String variableName = "testVariable";
        Variable variable = new Variable(variableName, new Group(), VariableType.STRING);
        variablesMap.getVariables().put(variableName, variable);

        //WHEN + THEN
        Assertions.assertThat(variablesMap.getVariable(variableName)).isEqualTo(variable);
    }

    @Test
    void getVariableTest_null(){
        //WHEN + THEN
        Assertions.assertThat(variablesMap.getVariable("test")).isNull();
    }

    @Test
    void getVariableNamesTest(){
        //GIVEN
        String variableName = "testVariable";
        Variable variable = new Variable(variableName, new Group(), VariableType.STRING);
        variablesMap.getVariables().put(variableName, variable);
        String variableName2 = "testVariable2";
        variable = new Variable(variableName, new Group(), VariableType.STRING);
        variablesMap.getVariables().put(variableName2, variable);

        //WHEN + THEN
        Assertions.assertThat(variablesMap.getVariableNames()).containsExactlyInAnyOrder(
                variableName, variableName2
        );
    }

    @Test
    void getVariableNamesTest_null(){
        //GIVEN
        variablesMap.variables = null;

        //WHEN + THEN
        Assertions.assertThat(variablesMap.getVariableNames()).isNotNull().isEmpty();
    }

    @Test
    void getGroupVariableNamesTest(){
        //GIVEN
        //With group
        String variableName = "testVariable";
        String groupName = "testGroup";
        Variable variable = new Variable(variableName, new Group(groupName), VariableType.STRING);
        variablesMap.getVariables().put(variableName, variable);
        //No group
        variable = new Variable("testVariable2", new Group("testGroup2"), VariableType.STRING);
        variablesMap.getVariables().put("testVariable2", variable);

        //WHEN + THEN
        Assertions.assertThat(variablesMap.getGroupVariableNames(groupName)).containsExactly(variableName);
    }

    @Test
    void getGroupVariableNamesAsListTest(){
        //GIVEN
        //With group
        String variableName = "testVariable";
        String groupName = "testGroup";
        Variable variable = new Variable(variableName, new Group(groupName), VariableType.STRING);
        variablesMap.getVariables().put(variableName, variable);
        //No group
        variable = new Variable("testVariable2", new Group("testGroup2"), VariableType.STRING);
        variablesMap.getVariables().put("testVariable2", variable);

        //WHEN + THEN
        Assertions.assertThat(variablesMap.getGroupVariableNamesAsList(groupName)).containsExactly(variableName);
    }

    @Test
    void hasVariableTest(){
        //GIVEN
        String variableName = "testVariable";
        Variable variable = new Variable(variableName, new Group(), VariableType.STRING);
        variablesMap.getVariables().put(variableName, variable);

        //WHEN + THEN
        Assertions.assertThat(variablesMap.hasVariable(variableName)).isTrue();
        Assertions.assertThat(variablesMap.hasVariable("absentVariable")).isFalse();
    }

    @Test
    void hasMcqTest(){
        //GIVEN
        String questionName = "question";
        String variableName = "testVariable";
        McqVariable mcqVariable = new McqVariable(variableName, new Group("testGroup"), VariableType.BOOLEAN);
        mcqVariable.questionName = questionName;
        variablesMap.getVariables().put(variableName, mcqVariable);
        String questionName2 = "question2";
        String variableName2 = "testVariable2";
        UcqVariable ucqVariable = new UcqVariable(variableName2, new Group("testGroup2"), VariableType.STRING);
        ucqVariable.questionName = questionName2;
        variablesMap.getVariables().put(variableName2, mcqVariable);

        //WHEN + THEN
        Assertions.assertThat(variablesMap.hasMcq(questionName)).isTrue();
        Assertions.assertThat(variablesMap.hasMcq(questionName2)).isFalse();
        Assertions.assertThat(variablesMap.hasMcq("absentQuestion")).isFalse();
    }

    @Test
    void getMcqGroupTest(){
        //GIVEN
        String questionName = "question";
        String variableName = "testVariable";
        String groupName = "testGroup";
        Group group = new Group(groupName);
        McqVariable mcqVariable = new McqVariable(variableName, group, VariableType.BOOLEAN);
        mcqVariable.questionName = questionName;
        variablesMap.getVariables().put(variableName, mcqVariable);

        //WHEN + THEN
        Assertions.assertThat(variablesMap.getMcqGroup(questionName)).isEqualTo(group);
        Assertions.assertThat(variablesMap.getMcqGroup("absentQuestion")).isNull();
    }

    @Test
    void isInQuestionGridTest(){
        //GIVEN
        String questionName = "question";
        String variableName = "testVariable";
        Variable variable = new Variable(variableName, new Group("testGroup"), VariableType.STRING);
        variable.questionName = questionName;
        variable.isInQuestionGrid = true;
        variablesMap.getVariables().put(variableName, variable);

        //WHEN + THEN
        Assertions.assertThat(variablesMap.isInQuestionGrid(questionName)).isTrue();
        Assertions.assertThat(variablesMap.isInQuestionGrid("absentQuestion")).isFalse();
    }

    @Test
    void getQuestionGridGroup(){
        //GIVEN
        String questionName = "question";
        String variableName = "testVariable";
        String groupName = "testGroup";
        Group group = new Group(groupName);
        Variable variable = new Variable(variableName, group, VariableType.STRING);
        variable.questionName = questionName;
        variable.isInQuestionGrid = true;
        variablesMap.getVariables().put(variableName, variable);

        //WHEN + THEN
        Assertions.assertThat(variablesMap.getQuestionGridGroup(questionName)).isEqualTo(group);
        Assertions.assertThat(variablesMap.getQuestionGridGroup("absentQuestion")).isNull();
    }

    @Test
    void hasUcqTest(){
        //GIVEN
        String variableName = "testVariable";
        McqVariable mcqVariable = new McqVariable(variableName, new Group("testGroup"), VariableType.BOOLEAN);
        variablesMap.getVariables().put(variableName, mcqVariable);
        String variableName2 = "testVariable2";
        UcqVariable ucqVariable = new UcqVariable(variableName2, new Group("testGroup2"), VariableType.STRING);
        variablesMap.getVariables().put(variableName2, ucqVariable);


        //WHEN + THEN
        Assertions.assertThat(variablesMap.hasUcq(variableName)).isFalse();
        Assertions.assertThat(variablesMap.hasUcq(variableName2)).isTrue();
        Assertions.assertThat(variablesMap.hasUcq("absentVariable")).isFalse();
    }

    @Test
    void hasUcqMcqTest(){
        //GIVEN
        String variableName = "testVariable";
        UcqVariable ucqVariable = new UcqVariable(variableName, new Group("testGroup2"), VariableType.STRING);
        ucqVariable.questionName = "question";
        variablesMap.getVariables().put(variableName, ucqVariable);
        String variableName2 = "testVariable2";
        ucqVariable = new UcqVariable(variableName2, new Group("testGroup2"), VariableType.STRING);
        ucqVariable.questionName = "";
        variablesMap.getVariables().put(variableName2, ucqVariable);

        //WHEN + THEN
        Assertions.assertThat(variablesMap.hasUcqMcq(variableName)).isTrue();
        Assertions.assertThat(variablesMap.hasUcqMcq(variableName2)).isFalse();
        Assertions.assertThat(variablesMap.hasUcqMcq("absentVariable")).isFalse();
    }

    @Test
    void getUcqVariablesTest(){
        //GIVEN
        String variableName = "testVariable";
        McqVariable mcqVariable = new McqVariable(variableName, new Group("testGroup"), VariableType.BOOLEAN);
        variablesMap.getVariables().put(variableName, mcqVariable);
        String variableName2 = "testVariable2";
        UcqVariable ucqVariable = new UcqVariable(variableName2, new Group("testGroup2"), VariableType.STRING);
        variablesMap.getVariables().put(variableName2, ucqVariable);


        //WHEN + THEN
        Assertions.assertThat(variablesMap.getUcqVariables()).containsExactly(ucqVariable);
    }

    @Test
    void getUcqVariablesNamesTest(){
        //GIVEN
        String variableName = "testVariable";
        McqVariable mcqVariable = new McqVariable(variableName, new Group("testGroup"), VariableType.BOOLEAN);
        variablesMap.getVariables().put(variableName, mcqVariable);
        String variableName2 = "testVariable2";
        UcqVariable ucqVariable = new UcqVariable(variableName2, new Group("testGroup2"), VariableType.STRING);
        String questionName = "question";
        ucqVariable.questionName = questionName;
        variablesMap.getVariables().put(variableName2, ucqVariable);


        //WHEN + THEN
        Assertions.assertThat(variablesMap.getUcqVariablesNames()).containsExactly(questionName);
    }

    @Test
    void getMcqVariablesNamesTest(){
        //GIVEN
        String variableName = "testVariable";
        McqVariable mcqVariable = new McqVariable(variableName, new Group("testGroup"), VariableType.BOOLEAN);
        String questionName = "question";
        mcqVariable.questionName = questionName;
        variablesMap.getVariables().put(variableName, mcqVariable);
        String variableName2 = "testVariable2";
        UcqVariable ucqVariable = new UcqVariable(variableName2, new Group("testGroup2"), VariableType.STRING);
        variablesMap.getVariables().put(variableName2, ucqVariable);


        //WHEN + THEN
        Assertions.assertThat(variablesMap.getMcqVariablesNames()).containsExactly(questionName);
    }

    @Test
    void getPaperUcqTest(){
        //GIVEN
        String variableName = "testVariable";
        McqVariable mcqVariable = new McqVariable(variableName, new Group("testGroup"), VariableType.BOOLEAN);
        String questionName = "question";
        mcqVariable.questionName = questionName;
        variablesMap.getVariables().put(variableName, mcqVariable);
        String variableName2 = "testVariable2";
        UcqVariable ucqVariable = new UcqVariable(variableName2, new Group("testGroup2"), VariableType.STRING);
        String testValue = "testValue";
        ucqVariable.modalities.add(new UcqModality(testValue, "testText"));
        variablesMap.getVariables().put(variableName2, ucqVariable);
        PaperUcq paperUcq = new PaperUcq("testVariable3", ucqVariable, testValue);
        variablesMap.getVariables().put("testVariable3", paperUcq);


        //WHEN + THEN
        Assertions.assertThat(variablesMap.getPaperUcq()).containsExactly(paperUcq);
    }
}