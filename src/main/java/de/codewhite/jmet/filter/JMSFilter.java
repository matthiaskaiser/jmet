package de.codewhite.jmet.filter;

import javax.jms.Message;
import javax.script.ScriptException;

/**
 * Created by kaimatt.
 */
public interface JMSFilter {

    Message filter(Message message) throws ScriptException, NoSuchMethodException;
}
