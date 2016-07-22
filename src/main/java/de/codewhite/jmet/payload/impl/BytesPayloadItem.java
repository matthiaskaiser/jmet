package de.codewhite.jmet.payload.impl;

/**
 * Created by kaimatt.
 */
public class BytesPayloadItem {

    private String payloadName;
    private Object[] payloadBytes;


    public BytesPayloadItem(String payloadName, Object[] payloadBytes) {
        this.payloadName = payloadName;
        this.payloadBytes = payloadBytes;
    }

    public Object[] getPayloadBytes() {
        return payloadBytes;
    }

    public void setPayloadBytes(Object[] payloadBytes) {
        this.payloadBytes = payloadBytes;
    }

    public String getPayloadName() {
        return payloadName;
    }

    public void setPayloadName(String payloadName) {
        this.payloadName = payloadName;
    }
}
