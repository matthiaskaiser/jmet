package de.codewhite.jmet.target.impl;

import de.codewhite.jmet.exceptions.InitException;
import de.codewhite.jmet.target.JMSTarget;
import de.codewhite.jmet.types.ModeType;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.JMSException;

/**
 * Created by kaimatt.
 */
public class ActiveMQTarget extends JMSTarget {

    public ModeType[] getSupportedModes() {
        return new ModeType[]{ModeType.OPENWIRE};
    }


    public void init() throws InitException {

        try {
            factory = new ActiveMQConnectionFactory("tcp://" + getHost() + ":" + getPort());
            connection = factory.createConnection(getUser(), getPassword());

            session = connection.createSession(false, ActiveMQSession.AUTO_ACKNOWLEDGE);

            switch (getDestType()) {
                case QUEUE:
                    dest = new ActiveMQQueue(getDestination());
                    break;
                case TOPIC:
                    dest = new ActiveMQTopic(getDestination());
                    break;
            }


            producer = session.createProducer(dest);
            connection.start();
        } catch (JMSException e) {
            throw new InitException(e.fillInStackTrace());
        }
    }


}
