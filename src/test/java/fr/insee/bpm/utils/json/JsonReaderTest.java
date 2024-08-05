package fr.insee.bpm.utils.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonReaderTest {
    @Test
    void exceptionTest(){
        assertThrows(IllegalStateException.class, JsonReader::new);
    }
}
