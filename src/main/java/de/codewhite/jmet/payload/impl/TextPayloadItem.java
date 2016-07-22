package de.codewhite.jmet.payload.impl;

/**
 * Created by kaimatt.
 */
public class TextPayloadItem {

    private String payloadName;
    private String payloadText;

    public TextPayloadItem(String payloadName, String payloadText) {
        this.payloadName = payloadName;
        this.payloadText = payloadText;
    }

    public String getPayloadName() {
        return payloadName;
    }

    public void setPayloadName(String payloadName) {
        this.payloadName = payloadName;
    }

    public String getPayloadText() {
        return payloadText;
    }

    public void setPayloadText(String payloadText) {
        this.payloadText = payloadText;
    }
}
