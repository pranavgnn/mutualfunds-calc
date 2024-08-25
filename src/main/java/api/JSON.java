package api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

class JSON {
    private static ObjectMapper mapper = new ObjectMapper();;

    static JsonNode parse(String src) {
        try {
            return mapper.readTree(src);
        } catch (IOException e) {
            return null;
        }      
    }
}
