package de.codewhite.jmet.target.impl;

import com.swiftmq.jms.SwiftMQConnectionFactory;
import de.codewhite.jmet.exceptions.InitException;
import de.codewhite.jmet.target.JMSTarget;
import de.codewhite.jmet.types.DestType;
import de.codewhite.jmet.types.ModeType;

import javax.jms.Session;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaimatt.
 */
public class SwiftMQTarget extends JMSTarget {


    public ModeType[] getSupportedModes() {
        return new ModeType[]{ModeType.SMQP};
    }

    public void init() throws InitException {

        Map<String,String> m = new HashMap<String, String>();
        m.put("socketfactory", "com.swiftmq.net.PlainSocketFactory");
        m.put("hostname", getHost());
        m.put("port", Integer.toString(getPort()));

        try {

            factory = SwiftMQConnectionFactory.create(m);
            connection = factory.createConnection(getUser(), getPassword());
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            dest = (getDestType() == DestType.QUEUE) ? session.createQueue(getDestination()) : session.createTopic(getDestination());
            producer = session.createProducer(dest);
            connection.start();

        } catch (Exception e) {
            throw new InitException(e.fillInStackTrace());
        }


    }
}
