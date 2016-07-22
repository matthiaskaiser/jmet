package de.codewhite.jmet.messagetexts;

/**
 * Created by kaimatt.
 */
public enum Messages {

    CONN_FAILED ("Init of {0} connection failed");

    String message;

    Messages(String s){
    this.message = s;
    }

    public String getText(){
        return message;
    }
}
