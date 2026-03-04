package fr.insee.bpm.metadata.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.Date;

class VariableTypeTest {

    @ParameterizedTest
    @ValueSource(classes = {Float.class, Double.class})
    void getTypeFromJavaClass_number(Class<?> javaClass) {
        Assertions.assertThat(VariableType.getTypeFromJavaClass(javaClass))
                .isEqualTo(VariableType.NUMBER);
    }

    @ParameterizedTest
    @ValueSource(classes = {Integer.class, Long.class, Short.class})
    void getTypeFromJavaClass_integer(Class<?> javaClass) {
        Assertions.assertThat(VariableType.getTypeFromJavaClass(javaClass))
                .isEqualTo(VariableType.INTEGER);
    }

    @ParameterizedTest
    @ValueSource(classes = {String.class, Character.class})
    void getTypeFromJavaClass_string(Class<?> javaClass) {
        Assertions.assertThat(VariableType.getTypeFromJavaClass(javaClass))
                .isEqualTo(VariableType.STRING);
    }

    @Test
    void getTypeFromJavaClass_boolean() {
        Assertions.assertThat(VariableType.getTypeFromJavaClass(Boolean.class))
                .isEqualTo(VariableType.BOOLEAN);
    }

    @ParameterizedTest
    @ValueSource(classes = {Date.class, Instant.class})
    void getTypeFromJavaClass_date(Class<?> javaClass) {
        Assertions.assertThat(VariableType.getTypeFromJavaClass(javaClass))
                .isEqualTo(VariableType.DATE);
    }

    @Test
    void getTypeFromJavaClass_null() {
        Assertions.assertThat(VariableType.getTypeFromJavaClass(VariableType.class))
                .isNull();
    }
}