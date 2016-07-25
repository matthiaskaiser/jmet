package de.codewhite.jmet.target.impl;

import de.codewhite.jmet.exceptions.InitException;
import de.codewhite.jmet.target.JMSTarget;
import de.codewhite.jmet.types.DestType;
import de.codewhite.jmet.types.ModeType;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants;


import javax.jms.*;
import java.util.HashMap;

/**
 * Created by kaimatt.
 */
public class ArtemisTarget extends JMSTarget {

    @Override
    public ModeType[] getSupportedModes() {
        return new ModeType[]{ModeType.ARTEMIS};
    }

    @Override
    public void init() throws InitException {

        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("host", getHost());
            map.put("port", getPort());
            map.put(TransportConstants.HTTP_UPGRADE_ENABLED_PROP_NAME, true);
            map.put(TransportConstants.HTTP_UPGRADE_ENDPOINT_PROP_NAME, "http-acceptor");
            TransportConfiguration transportConfiguration = new TransportConfiguration(
                    NettyConnectorFactory.class.getName(), map);

            factory = new org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory(false, transportConfiguration);

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
