package eu.rigeldev.kuberig.dsl.kubernetes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;

import java.io.IOException;

public class UnirestConfigurator {

    public static void configureUnirest() {
        Unirest.setObjectMapper(new com.mashape.unirest.http.ObjectMapper() {

            private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new ObjectMapper();

            @Override
            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return this.objectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String writeValue(Object value) {
                try {
                    return this.objectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
