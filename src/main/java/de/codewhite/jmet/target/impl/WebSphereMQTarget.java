package de.codewhite.jmet.target.impl;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import de.codewhite.jmet.exceptions.InitException;
import de.codewhite.jmet.target.JMSTarget;
import de.codewhite.jmet.types.DestType;
import de.codewhite.jmet.types.ModeType;

import javax.jms.JMSException;
import javax.jms.Session;

/**
 * Created by kaimatt.
 */
public class WebSphereMQTarget extends JMSTarget {

    private String channel;
    private String queueManager;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(String queueManager) {
        this.queueManager = queueManager;
    }

    public ModeType[] getSupportedModes() {
        return new ModeType[]{ModeType.WEBSPHEREMQ};
    }

    public void init() throws InitException {

        try {
            MQConnectionFactory realfactory = new MQConnectionFactory();
            realfactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
            realfactory.setHostName(getHost());
            realfactory.setPort(getPort());
            realfactory.setQueueManager(getQueueManager());
            realfactory.setChannel(getChannel());

            factory = realfactory;
            connection = factory.createConnection(getUser(), getPassword());
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            dest = (getDestType() == DestType.QUEUE) ? session.createQueue(getDestination()) : session.createTopic(getDestination());
            producer = session.createProducer(dest);
            connection.start();
        } catch (JMSException e) {
            throw new InitException(e.fillInStackTrace());
        }

    }

}
