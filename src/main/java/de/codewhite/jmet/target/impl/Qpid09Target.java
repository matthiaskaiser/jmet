package de.codewhite.jmet.target.impl;

import de.codewhite.jmet.exceptions.InitException;
import de.codewhite.jmet.target.JMSTarget;
import de.codewhite.jmet.types.ModeType;
import org.apache.qpid.client.*;


import javax.jms.Session;

/**
 * Created by kaimatt.
 */
public class Qpid09Target extends JMSTarget {

    public ModeType[] getSupportedModes() {
        return new ModeType[]{ModeType.AMQPpre10};
    }

    public void init() throws InitException {

        try {
            String url = "amqp://" + getUser() + ":" + getPassword() + "@jmet";
            String vhost = getVhost();
            if(vhost != null){
                vhost += "/" + vhost;
            }
            url += vhost +  "/?brokerlist='tcp://" + getHost() + ":" + getPort() + "'";
            factory = new AMQConnectionFactory(url);
            connection = factory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            switch (getDestType()) {
                case QUEUE:
                    dest = session.createQueue(getDestination());
                    break;
                case TOPIC:
                    dest = session.createTopic(getDestination());
                    break;
            }


            producer = session.createProducer(dest);
            connection.start();

        } catch (Exception e) {
            throw new InitException(e.fillInStackTrace());
        }

    }

}
