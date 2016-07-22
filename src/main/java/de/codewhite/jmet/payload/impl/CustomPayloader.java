package de.codewhite.jmet.payload.impl;

import de.codewhite.jmet.target.BaseTarget;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class CustomPayloader {
    private static final Logger logger = LogManager.getLogger(CustomPayloader.class);

    public void setupPayloadsOnTarget(String customScriptName, BaseTarget target) throws ScriptException, FileNotFoundException, NoSuchMethodException, NoClassDefFoundError {

        Path scriptDir = Paths.get("scripts", customScriptName);
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.eval(new java.io.FileReader(scriptDir.toFile()));

        Invocable invoker = (Invocable) engine;
        invoker.invokeFunction("payload", target);

    }

}
