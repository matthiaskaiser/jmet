package de.codewhite.jmet.filter;

import javax.jms.Message;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by kaimatt.
 */
public class ScriptMesageFilter implements JMSFilter {

    private final Invocable invoker;

    public ScriptMesageFilter(String customScriptName) throws FileNotFoundException, ScriptException {


            Path scriptDir = Paths.get("scripts",customScriptName);
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine =  manager.getEngineByName("JavaScript");
            engine.eval(new java.io.FileReader(scriptDir.toFile()));

            invoker = (Invocable) engine;

    }

    @Override
    public Message filter(Message message) throws ScriptException, NoSuchMethodException {
        return (Message) invoker.invokeFunction("filter",message);
    }
}
