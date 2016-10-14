package de.codewhite.jmet.target.impl;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;
import de.codewhite.jmet.exceptions.InitException;
import de.codewhite.jmet.target.JMSTarget;
import de.codewhite.jmet.types.DestType;
import de.codewhite.jmet.types.ModeType;

import javax.jms.JMSException;
import javax.jms.Session;

/**
 * Created by kaimatt.
 */
public class RabbitMQTarget extends JMSTarget {


    public ModeType[] getSupportedModes() {
        return new ModeType[]{ModeType.AMQP10, ModeType.AMQPpre10};
    }

    public void init() throws InitException {

        RMQConnectionFactory implFactory = new RMQConnectionFactory();

        try {
			String uri = "amqp://" + getUser() + ":" + getPassword() + "@" + getHost() + ":" + getPort();

			if (!getVhost().equals("/")) {
				uri = uri + "/" + getVhost();
			}
            factory = implFactory;
            connection = factory.createConnection(getUser(), getPassword());
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            dest = new RMQDestination(getDestination(), getDestType() == DestType.QUEUE , false);
            producer = session.createProducer(dest);
            connection.start();
        } catch (JMSException e) {
            throw new InitException(e.fillInStackTrace());
        }
    }

}
