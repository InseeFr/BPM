package fr.insee.bpm.metadata.model;

import fr.insee.bpm.metadata.Constants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;

class MetadataModelTest {

    private MetadataModel metadataModel;

    @BeforeEach
    void setUp() {
        metadataModel = new MetadataModel();
    }

    @Test
    void constructorPutRootGroupTest(){
        //WHEN
        //Already done in setUp

        //THEN
        Assertions.assertThat(metadataModel.getGroups()).containsKey(Constants.ROOT_GROUP_NAME);
        Assertions.assertThat(metadataModel.getGroups().get(Constants.ROOT_GROUP_NAME)).isNotNull();
        Assertions.assertThat(metadataModel.getGroups().get(Constants.ROOT_GROUP_NAME).isRoot()).isTrue();
    }

    @Test
    void putGroupTest(){
        //GIVEN
        String groupName = "group";
        Group group = new Group(groupName, Constants.ROOT_GROUP_NAME);

        //WHEN
        metadataModel.putGroup(group);


        //THEN
        Assertions.assertThat(metadataModel.getGroups()).containsEntry(groupName, group);
    }

    @Test
    void getGroupTest(){
        //GIVEN
        String groupName = "group";
        Group group = new Group(groupName, Constants.ROOT_GROUP_NAME);
        metadataModel.getGroups().put(groupName, group);

        //WHEN
        Group result = metadataModel.getGroup(groupName);


        //THEN
        Assertions.assertThat(result).isEqualTo(group);
    }

    @Test
    void getRootGroupTest_noRootGroup(){
        //GIVEN
        metadataModel.getGroups().remove(Constants.ROOT_GROUP_NAME);

        //WHEN
        Group result = metadataModel.getRootGroup();

        //THEN
        Assertions.assertThat(result).isNull();
    }

    @Test
    void getRootGroupTest(){
        //WHEN
        Group result = metadataModel.getRootGroup();

        //THEN
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.isRoot()).isTrue();
        Assertions.assertThat(result.getName()).isEqualTo(Constants.ROOT_GROUP_NAME);
    }

    @Test
    void getReportingDataGroupTest_notPresent(){
        //GIVEN
        metadataModel.getGroups().remove(Constants.REPORTING_DATA_GROUP_NAME);

        //WHEN
        Group result = metadataModel.getReportingDataGroup();

        //THEN
        Assertions.assertThat(result).isNull();
    }

    @Test
    void getReportingDataGroupTest(){
        //GIVEN
        metadataModel.getGroups().put(Constants.REPORTING_DATA_GROUP_NAME, new Group(Constants.REPORTING_DATA_GROUP_NAME));

        //WHEN
        Group result = metadataModel.getReportingDataGroup();

        //THEN
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo(Constants.REPORTING_DATA_GROUP_NAME);
    }

    @Test
    void getGroupNamesTest(){
        //GIVEN
        String groupName = "testGroup";
        Group group = new Group(groupName, Constants.ROOT_GROUP_NAME);
        metadataModel.getGroups().put(groupName, group);

        //WHEN
        List<String> result = metadataModel.getGroupNames();

        //THEN
        Assertions.assertThat(result).containsExactlyInAnyOrder(
                Constants.ROOT_GROUP_NAME, groupName
        );
    }

    @Test
    void getSubGroupNamesTest(){
        //GIVEN
        String groupName = "testGroup";
        Group group = new Group(groupName, Constants.ROOT_GROUP_NAME);
        metadataModel.getGroups().put(groupName, group);

        //WHEN
        List<String> result = metadataModel.getSubGroupNames();

        //THEN
        Assertions.assertThat(result).containsExactly(
                groupName
        );
    }

    @Test
    void getGroupsCountTest(){
        //GIVEN
        int expectedGroupCount = metadataModel.getGroups().size();

        //WHEN + THEN
        Assertions.assertThat(metadataModel.getGroupsCount()).isEqualTo(expectedGroupCount);
    }

    @Test
    void hasGroupTest(){
        //GIVEN
        String groupName = "testGroup";
        Group group = new Group(groupName, Constants.ROOT_GROUP_NAME);
        metadataModel.getGroups().put(groupName, group);
        String absentGroupName = "absentGroupe";

        //WHEN + THEN
        Assertions.assertThat(metadataModel.hasGroup(groupName)).isTrue();
        Assertions.assertThat(metadataModel.hasGroup(absentGroupName)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"","unknownVariable"})
    void getFullyQualifiedNameTest_unknownVariableOrEmpty(String variableName) {
        //WHEN + THEN
        Assertions.assertThat(metadataModel.getFullyQualifiedName(variableName)).isNull();
    }

    @Test
    void getFullyQualifiedNameTest_root() {
        //GIVEN
        String variableName = "testVariable";
        Variable variable = new Variable(
            variableName,
            metadataModel.getRootGroup(),
            VariableType.STRING
        );
        metadataModel.getVariables().variables.put(variableName, variable);

        //WHEN
        String fullyQualifiedName = metadataModel.getFullyQualifiedName(variableName);

        //THEN
        Assertions.assertThat(fullyQualifiedName).isEqualTo(variableName);
    }

    @Test
    void getFullyQualifiedNameTest_group() {
        //GIVEN
        String groupName = "testGroup";
        Group group = new Group(groupName, Constants.ROOT_GROUP_NAME);
        metadataModel.getGroups().put(groupName, group);
        String variableName = "testVariable";
        Variable variable = new Variable(
                variableName,
                group,
                VariableType.STRING
        );
        metadataModel.getVariables().variables.put(variableName, variable);


        //WHEN
        String fullyQualifiedName = metadataModel.getFullyQualifiedName(variableName);

        //THEN
        Assertions.assertThat(fullyQualifiedName).isEqualTo(
                group.name + Constants.METADATA_SEPARATOR + variableName
        );
    }

    @Test
    void getFullyQualifiedNamesTest() {
        //GIVEN
        //Group variable
        String groupName = "testGroup";
        Group group = new Group(groupName, Constants.ROOT_GROUP_NAME);
        metadataModel.getGroups().put(groupName, group);
        String variableName = "testVariable";
        Variable variable = new Variable(
                variableName,
                group,
                VariableType.STRING
        );
        metadataModel.getVariables().variables.put(variableName, variable);
        //Root variable
        String rootVariableName = "testVariable2";
        Variable rootVariable = new Variable(
                rootVariableName,
                metadataModel.getRootGroup(),
                VariableType.STRING
        );
        metadataModel.getVariables().variables.put(rootVariableName, rootVariable);


        //WHEN
        Set<String> fullyQualifiedNames = metadataModel.getFullyQualifiedNames();

        //THEN
        Assertions.assertThat(fullyQualifiedNames).containsExactlyInAnyOrder(
                rootVariableName,
                group.name + Constants.METADATA_SEPARATOR + variableName
        );
    }


    @Test
    void getDistinctVariableNamesAndFullyQualifiedNamesTest() {
        //GIVEN
        //Group variable
        String groupName = "testGroup";
        Group group = new Group(groupName, Constants.ROOT_GROUP_NAME);
        metadataModel.getGroups().put(groupName, group);
        String variableName = "testVariable";
        Variable variable = new Variable(
                variableName,
                group,
                VariableType.STRING
        );
        metadataModel.getVariables().variables.put(variableName, variable);
        //Root variable
        String rootVariableName = "testVariable2";
        Variable rootVariable = new Variable(
                rootVariableName,
                metadataModel.getRootGroup(),
                VariableType.STRING
        );
        metadataModel.getVariables().variables.put(rootVariableName, rootVariable);

        //WHEN
        Set<String> variableNames = metadataModel.getDistinctVariableNamesAndFullyQualifiedNames();

        //THEN
        Assertions.assertThat(variableNames).containsExactlyInAnyOrder(
                rootVariableName,
                variableName,
                group.name + Constants.METADATA_SEPARATOR + variableName
        );
    }

    @Test
    void getSequencesName() {
        //GIVEN
        String sequenceName = "testSequence";
        Sequence sequence = new Sequence(sequenceName);
        metadataModel.getSequences().add(sequence);

        //WHEN
        List<String> sequenceNames = metadataModel.getSequencesName();

        //THEN
        Assertions.assertThat(sequenceNames).containsExactly(sequenceName);
    }
}
