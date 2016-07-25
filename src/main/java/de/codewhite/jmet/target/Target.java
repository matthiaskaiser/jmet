package de.codewhite.jmet.target;

import de.codewhite.jmet.exceptions.CheckException;
import de.codewhite.jmet.exceptions.InitException;
import de.codewhite.jmet.exceptions.SendException;
import de.codewhite.jmet.exceptions.ShutdownException;
import de.codewhite.jmet.payload.impl.BytesPayloadItem;
import de.codewhite.jmet.payload.impl.ObjectPayloadItem;
import de.codewhite.jmet.payload.impl.TextPayloadItem;
import de.codewhite.jmet.types.ModeType;

import javax.script.ScriptException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by kaimatt
 */
public interface Target {

    void addObjectPayloads(List<ObjectPayloadItem> payload);

    void addObjectPayload(String type, String command, Serializable payload);

    void addTextPayloads(List<TextPayloadItem> payload);

    void addTextPayload(String payloadName, String payloadText);

    void addBytesPayloads(List<BytesPayloadItem> payload);

    void addBytesPayload(String payloadName, Object[] payloadBytes);

    ModeType[] getSupportedModes();

    void check() throws CheckException;

    void init() throws InitException;

    void sendPayloads() throws SendException, ScriptException, NoSuchMethodException;

    void shutdown() throws ShutdownException;
}
