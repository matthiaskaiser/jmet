package de.codewhite.jmet.target.impl;

import de.codewhite.jmet.exceptions.InitException;
import de.codewhite.jmet.target.JMSTarget;
import de.codewhite.jmet.types.DestType;
import de.codewhite.jmet.types.ModeType;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.jms.client.HornetQJMSConnectionFactory;

import javax.jms.JMSException;
import javax.jms.Session;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaimatt.
 */
public class HornetQTarget extends JMSTarget {

    public ModeType[] getSupportedModes() {
        return new ModeType[]{ModeType.HORNETQ};
    }

    public void init() throws InitException {



        Map<String, Object> connectionParams = new HashMap<>();

        connectionParams.put(TransportConstants.HOST_PROP_NAME, getHost());
        connectionParams.put(TransportConstants.PORT_PROP_NAME, getPort());

        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(),
                connectionParams);

        factory = new HornetQJMSConnectionFactory(false,transportConfiguration);

        try {
            connection = factory.createConnection(getUser(),getPassword());

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            dest = (getDestType() == DestType.QUEUE) ? session.createQueue(getDestination()) : session.createTopic(getDestination());

            producer = session.createProducer(dest);
            connection.start();
        } catch (JMSException e) {
            throw new InitException(e.fillInStackTrace());
        }

    }


}
