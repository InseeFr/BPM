package fr.insee.bpm.metadata.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

class GroupTest {

    private Group group;

    @BeforeEach
    void setUp() {
        group = new Group();
    }

    @Test
    @SuppressWarnings({"null", "ConstantConditions"})
    void constructorNullParentNameTest() {
        assertThrows(NullPointerException.class,
                () -> new Group("TEST", null)
        );
    }

    @Test
    void constructorEmptyParentNameTest() {
        assertThrows(IllegalArgumentException.class,
                () -> new Group("TEST", "")
        );
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void isRootTest(boolean expectedBool) {
        //GIVEN
        if(!expectedBool){
            group.parentName = "parentName";
        }

        //WHEN
        boolean result = group.isRoot();

        //THEN
        Assertions.assertThat(result).isEqualTo(expectedBool);

    }
}
