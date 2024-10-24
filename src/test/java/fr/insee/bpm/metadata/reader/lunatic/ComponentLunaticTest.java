package fr.insee.bpm.metadata.reader.lunatic;

import fr.insee.bpm.metadata.model.VariableType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ComponentLunaticTest {

    @Test
    void testFromJsonName_ValidName() {
        assertEquals(ComponentLunatic.DATE_PICKER, ComponentLunatic.fromJsonName("Datepicker"));
        assertEquals(ComponentLunatic.CHECKBOX_BOOLEAN, ComponentLunatic.fromJsonName("CheckboxBoolean"));
        assertEquals(ComponentLunatic.INPUT, ComponentLunatic.fromJsonName("Input"));
        assertEquals(ComponentLunatic.DYNAMIC_TABLE, ComponentLunatic.fromJsonName("RosterForLoop"));
    }

    @Test
    void testFromJsonName_InvalidName() {
        assertNull(ComponentLunatic.fromJsonName("InvalidName"));
    }

    @Test
    void testFromJsonName_Null() {
        assertNull(ComponentLunatic.fromJsonName(null));
    }

    @Test
    void testFromJsonName_EmptyString() {
        assertNull(ComponentLunatic.fromJsonName(""));
    }

    @Test
    void testGetJsonName() {
        assertEquals("Datepicker", ComponentLunatic.DATE_PICKER.getJsonName());
        assertEquals("CheckboxBoolean", ComponentLunatic.CHECKBOX_BOOLEAN.getJsonName());
        assertEquals("Input", ComponentLunatic.INPUT.getJsonName());
    }

    @Test
    void testGetType() {
        assertEquals(VariableType.DATE, ComponentLunatic.DATE_PICKER.getType());
        assertEquals(VariableType.BOOLEAN, ComponentLunatic.CHECKBOX_BOOLEAN.getType());
        assertEquals(VariableType.STRING, ComponentLunatic.INPUT.getType());
        assertNull(ComponentLunatic.INPUT_NUMBER.getType());
    }


}
