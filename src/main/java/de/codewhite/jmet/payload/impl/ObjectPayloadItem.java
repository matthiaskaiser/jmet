package de.codewhite.jmet.payload.impl;

import java.io.Serializable;

/**
 * Created by kaimatt.
 */
public class ObjectPayloadItem {

    private String type;
    private String command;
    private Serializable payload;

    public ObjectPayloadItem(String type, String command, Serializable payload) {
        this.type = type;
        this.command = command;
        this.payload = payload;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Serializable getPayload() {
        return payload;
    }

    public void setPayload(Serializable payload) {
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
