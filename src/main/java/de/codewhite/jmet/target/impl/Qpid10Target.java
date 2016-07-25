package de.codewhite.jmet.target.impl;

import de.codewhite.jmet.exceptions.InitException;
import de.codewhite.jmet.target.JMSTarget;
import de.codewhite.jmet.types.ModeType;
import org.apache.qpid.jms.JmsConnectionFactory;

import javax.jms.Session;

/**
 * Created by kaimatt.
 */
public class Qpid10Target extends JMSTarget {

    public ModeType[] getSupportedModes() {
        return new ModeType[]{ModeType.AMQP10};
    }

    public void init() throws InitException {

        try {
            String url = "amqp://" + getHost() + ":" + getPort();
            String vhost = getVhost();
            if (vhost != null) {
                url +="?amqp.vhost=" + vhost;
            }
            factory = new JmsConnectionFactory(url);
            connection = factory.createConnection(getUser(), getPassword());


            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            switch (getDestType()) {
                case QUEUE:
                    dest = session.createQueue(getDestination());
                    break;
                case TOPIC:
                    dest = session.createTopic(getDestination());
                    break;
            }
            connection.start();
            producer = session.createProducer(dest);


        } catch (Exception e) {
            throw new InitException(e.fillInStackTrace());
        }
    }

}
