package com.example.webbattle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class OutgoingMessage {
    private StringBuilder message;

    public OutgoingMessage(String type) {
        message = new StringBuilder();
        message.append("{\"kind\": \"");
        message.append(type);
        message.append("\"");
    }

    public OutgoingMessage add(String id, String value) {
        return addInner(id, "\"" + value + "\"");
    }

    public OutgoingMessage add(String id, int value) {
        return addInner(id, Integer.toString(value));
    }

    public OutgoingMessage add(String id, double value) {
        return addInner(id, Double.toString(value));
    }

    public OutgoingMessage add(String id, boolean value) {
        return addInner(id, Boolean.toString(value));
    }

    public OutgoingMessage add(String id, List<Skill> list) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String value = mapper.writeValueAsString(list);
        return addInner(id, value);
    }

    private OutgoingMessage addInner(String id, String value) {
        message.append(", \"");
        message.append(id);
        message.append("\": ");
        message.append(value);
        return this;
    }

    public String toString() {
        return message.toString() + "}";
    }
}
