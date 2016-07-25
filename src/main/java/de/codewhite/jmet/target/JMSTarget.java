package de.codewhite.jmet.target;

import de.codewhite.jmet.exceptions.CheckException;
import de.codewhite.jmet.exceptions.SendException;
import de.codewhite.jmet.exceptions.ShutdownException;
import de.codewhite.jmet.filter.ScriptMesageFilter;
import de.codewhite.jmet.payload.impl.BytesPayloadItem;
import de.codewhite.jmet.payload.impl.ObjectPayloadItem;
import de.codewhite.jmet.payload.impl.TextPayloadItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.*;
import javax.script.ScriptException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaimatt.
 */
public abstract class JMSTarget extends BaseTarget {

    protected final Logger logger = LogManager.getLogger(this.getClass().getName());
    protected ConnectionFactory factory;
    protected Connection connection;
    protected Session session;
    protected Destination dest;
    protected MessageProducer producer;

    protected List<ObjectPayloadItem> objectPayloads = new ArrayList<>();
    protected List<TextPayloadItem> textPayloads = new ArrayList<>();
    protected List<BytesPayloadItem> bytesPayloads = new ArrayList<>();
    protected ScriptMesageFilter filter;

    protected boolean gadgetWrapping = false;

    public boolean isGadgetWrapping() {
        return gadgetWrapping;
    }

    public void setGadgetWrapping(boolean gadgetWrapping) {
        this.gadgetWrapping = gadgetWrapping;
    }

    public void addObjectPayloads(List<ObjectPayloadItem> payload) {
        objectPayloads.addAll(payload);
    }

    public void addTextPayloads(List<TextPayloadItem> payload) {
        textPayloads.addAll(payload);
    }

    public void addBytesPayloads(List<BytesPayloadItem> payload) {
        bytesPayloads.addAll(payload);
    }

    public void addObjectPayload(String type, String command, Serializable payload) {
        objectPayloads.add(new ObjectPayloadItem(type, command, payload));

    }

    public void addTextPayload(String payloadName, String payloadText) {
        textPayloads.add(new TextPayloadItem(payloadName, payloadText));

    }

    public void addBytesPayload(String payloadName, Object[] payloadBytes) {
        bytesPayloads.add(new BytesPayloadItem(payloadName, payloadBytes));

    }

    public List<ObjectPayloadItem> getObjectPayloads() {
        return objectPayloads;
    }

    public List<TextPayloadItem> getTextPayloads() {
        return textPayloads;
    }

    public List<BytesPayloadItem> getBytesPayloads() {
        return bytesPayloads;
    }

    public ScriptMesageFilter getFilter() {
        return filter;
    }

    public void sendPayloads() throws SendException, ScriptException, NoSuchMethodException {


        for (ObjectPayloadItem pay : objectPayloads) {

            try {
                ObjectMessage msg = session.createObjectMessage(pay.getPayload());
                if (filter != null) {

                    msg = (ObjectMessage) filter.filter(msg);

                }
                producer.send(msg);
                logger.info("Sent gadget \"{}\" with command: \"{}\"", pay.getType(), pay.getCommand());
            } catch (JMSException e) {
                throw new SendException("Sending ObjectMessage failed", e);
            }

        }

        for (TextPayloadItem pay : textPayloads) {

            TextMessage msg;
            try {
                msg = session.createTextMessage(pay.getPayloadText());
                if (filter != null) {
                    msg = (TextMessage) filter.filter(msg);
                }
                producer.send(msg);
                logger.info("Sent TextMessage with payload \"{}\"", pay.getPayloadName());
            } catch (JMSException e) {
                throw new SendException("Sending TextMessage failed", e);
            }


        }

        for (BytesPayloadItem pay : bytesPayloads) {

            try {
                BytesMessage msg = session.createBytesMessage();
                if (filter != null) {
                    msg = (BytesMessage) filter.filter(msg);
                }
                msg.writeObject(pay.getPayloadBytes());
                producer.send(msg);
                logger.info("Sent BytesMessage: Payload name: \"{}\"", pay.getPayloadName());
            } catch (JMSException e) {
                throw new SendException("Sending BytesMessage failed", e);
            }


        }

    }


    public void check() throws CheckException {

        String id;
        try {
            id = connection.getClientID();
            logger.info("Connected with ID: {}", id);
        } catch (JMSException e) {
            throw new CheckException("Connecting failed", e);
        }


    }


    public void shutdown() throws ShutdownException {


        try {
            logger.info("Shutting down connection {}", connection.getClientID());
            connection.close();
        } catch (JMSException e) {
            throw new ShutdownException("Shutdown failed", e);
        }


    }

    public void setFilter(ScriptMesageFilter filter) {
        this.filter = filter;
    }
}