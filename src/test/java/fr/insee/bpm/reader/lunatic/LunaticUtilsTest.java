package fr.insee.bpm.reader.lunatic;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.bpm.metadata.reader.lunatic.LunaticUtils.compareVersions;
import static fr.insee.bpm.metadata.reader.lunatic.LunaticUtils.findLongestCommonPrefix;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.xmlunit.assertj3.XmlAssert.assertThat;

class LunaticUtilsTest {

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

    @Test
    void findLongestCommonPrefixTest(){
        List<String> strs = new ArrayList<>();
        strs.add("VARIABLE1");
        strs.add("VARIABLE2");
        strs.add("VARS");
        assertThat(findLongestCommonPrefix(strs)).isEqualTo("VAR");
    }



}
