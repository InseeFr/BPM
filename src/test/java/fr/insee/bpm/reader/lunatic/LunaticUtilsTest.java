package fr.insee.bpm.reader.lunatic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.bpm.metadata.reader.lunatic.LunaticUtils.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.xmlunit.assertj3.XmlAssert.assertThat;

class LunaticUtilsTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Tests for compareVersion method

    @Test
    void compareVersion_testEqualVersions() {
        assertThat(compareVersions("1.0", "1.0")).isEqualTo(0);
        assertThat(compareVersions("2.0.1", "2.0.1")).isEqualTo(0);
        assertThat(compareVersions("3.0.0", "3.0")).isEqualTo(0);
    }

    @Test
    void compareVersion_testVersion1GreaterThanVersion2() {
        assertThat(compareVersions("1.2", "1.1")).isEqualTo(1);
        assertThat(compareVersions("2.0", "1.9")).isEqualTo(1);
        assertThat(compareVersions("1.0.1", "1.0")).isEqualTo(1);
    }

    @Test
    void compareVersion_testVersion2GreaterThanVersion1() {
        assertThat(compareVersions("1.1", "1.2")).isEqualTo(-1);
        assertThat(compareVersions("1.9", "2.0")).isEqualTo(-1);
        assertThat(compareVersions("1.0", "1.0.1")).isEqualTo(-1);
    }

    @Test
    void compareVersion_testDifferentLengthVersions() {
        assertThat(compareVersions("1.0", "1.0.0")).isEqualTo(0);
        assertThat(compareVersions("2.0", "2.0.0.0")).isEqualTo(0);
        assertThat(compareVersions("1.0.0", "1.0.1")).isEqualTo(-1);
    }

    @Test
    void compareVersion_testMultipleZerosInVersions() {
        assertThat(compareVersions("1.0.0", "1.0")).isEqualTo(0);
        assertThat(compareVersions("1.0.0.0.0", "1.0")).isEqualTo(0);
    }

    @Test
    void compareVersion_testComplexCompareVersion() {
        assertThat(compareVersions("1.0.1", "1.0.2")).isEqualTo(-1);
        assertThat(compareVersions("2.1.3", "2.1.2")).isEqualTo(1);
        assertThat(compareVersions("1.0.0.1", "1.0.0.0")).isEqualTo(1);
    }


    @Test
    void compareVersion_testInvalidVersionInput() {
        assertThrows(NumberFormatException.class,() -> compareVersions("1.a", "1.0"));
        assertThrows(NumberFormatException.class,() -> compareVersions("1..0", "1.0"));
    }

    // Tests for findLongestCommonPrefix method

    @Test
    void findLongestCommonPrefix_exists(){
        List<String> strs = new ArrayList<>();
        strs.add("VARIABLE1");
        strs.add("VARIABLE2");
        strs.add("VARS");
        assertThat(findLongestCommonPrefix(strs)).isEqualTo("VAR");
    }

    @Test
    void findLongestCommonPrefix_NoCommonPrefix() {
        List<String> input = List.of("dog", "racecar", "car");
        assertThat(findLongestCommonPrefix(input)).isEqualTo("");
    }

    @Test
    void findLongestCommonPrefix_SingleString() {
        List<String> input = List.of("hello");
        assertThat(findLongestCommonPrefix(input)).isEqualTo("hello");
    }

    @Test
    void findLongestCommonPrefix_WithEmptyString() {
        List<String> input = List.of("", "test", "testing");
        assertThat(findLongestCommonPrefix(input)).isEqualTo("");
    }

    @Test
    void findLongestCommonPrefix_EmptyList() {
        List<String> input = List.of();
        assertThat(findLongestCommonPrefix(input)).isEqualTo("");
    }

    @Test
    void findLongestCommonPrefix_VeryShortStrings() {
        List<String> input1 = List.of("a", "a", "a");
        List<String> input2 = List.of("a", "a", "b");

        assertThat(findLongestCommonPrefix(input1)).isEqualTo("a");
        assertThat(findLongestCommonPrefix(input2)).isEqualTo("");
    }

    // Tests for getFirstResponseName method

    @Test
    void getFirstResponseName_testSingleComponentWithResponse() throws Exception {
        String json = "[{\"componentType\": \"Radio\", \"response\": {\"name\": \"responseName1\"}}]";
        JsonNode components = objectMapper.readTree(json);
        assertThat(getFirstResponseName(components)).isEqualTo("responseName1");
    }

    @Test
    void getFirstResponseName_SingleComponentQuestion() throws Exception {
        String json = "[{\"componentType\": \"Question\", \"components\": [{\"componentType\": \"Input\", \"response\": {\"name\": \"responseName1\"}}]}]";
        JsonNode components = objectMapper.readTree(json);
        assertThat(getFirstResponseName(components)).isEqualTo("responseName1");
    }

    @Test
    void getFirstResponseName_MultipleComponentsWithResponse() throws Exception {
        String json = "[{\"componentType\": \"Input\", \"response\": {\"name\": \"responseName1\"}}, " +
                "{\"componentType\": \"Input\", \"response\": {\"name\": \"responseName2\"}}]";
        JsonNode components = objectMapper.readTree(json);
        assertThat(getFirstResponseName(components)).isEqualTo("responseName1");
    }


}
