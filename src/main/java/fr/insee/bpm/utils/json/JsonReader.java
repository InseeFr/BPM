package fr.insee.bpm.utils.json;


import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;

public class JsonReader {

    private static final JsonMapper MAPPER = JsonMapper.builder()
            .findAndAddModules()
            .build();

    JsonReader() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Read a json input stream.
     *
     * @param inputStream JSON input stream
     * @return Jackson JsonNode
     */
    public static JsonNode read(InputStream inputStream) throws IOException {
        return MAPPER.readTree(inputStream);
    }

}
