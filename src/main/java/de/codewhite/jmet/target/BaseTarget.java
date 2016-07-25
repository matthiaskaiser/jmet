package de.codewhite.jmet.target;

import de.codewhite.jmet.types.DestType;
import de.codewhite.jmet.types.MessageType;
import de.codewhite.jmet.types.ModeType;

/**
 * Created by kaimatt
 */
public abstract class BaseTarget implements Target {
    private String host;
    private int port;
    private String destination;
    private String user;
    private String password;
    private ModeType mode;
    private String cmd;
    private MessageType type;

    private String vhost;

    public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = vhost;
    }


    public DestType getDestType() {
        return destType;
    }

    public void setDestType(DestType destType) {
        this.destType = destType;
    }

    private DestType destType;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ModeType getMode() {
        return mode;
    }

    public void setMode(ModeType mode) {
        this.mode = mode;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

}
