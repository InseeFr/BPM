package fr.insee.metadataparserlib.utils.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class JsonReader {

    JsonReader(){
        throw new IllegalStateException("Utility class");
    }

    /**
     * Read a json input stream
     *
     * @param inputStream Path to a json file.
     *
     *  @return A jackson.databind.JsonNode.
     */
    public static JsonNode read(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(inputStream);
    }

}
