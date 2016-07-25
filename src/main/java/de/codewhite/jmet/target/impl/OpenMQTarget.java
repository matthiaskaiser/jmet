package de.codewhite.jmet.target.impl;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;
import de.codewhite.jmet.exceptions.InitException;
import de.codewhite.jmet.target.JMSTarget;
import de.codewhite.jmet.types.DestType;
import de.codewhite.jmet.types.ModeType;

import javax.jms.JMSException;
import javax.jms.Session;

/**
 * Created by kaimatt.
 */
public class OpenMQTarget extends JMSTarget {
    public ModeType[] getSupportedModes() {
        return new ModeType[]{ModeType.OPENMQ} ;
    }

    public void init() throws InitException {

        ConnectionFactory concreteFactory = new ConnectionFactory();
        try {
            concreteFactory.setProperty(ConnectionConfiguration.imqBrokerHostName, getHost());
            concreteFactory.setProperty(ConnectionConfiguration.imqBrokerHostPort, Integer.toString(getPort()));
            factory = concreteFactory;
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
