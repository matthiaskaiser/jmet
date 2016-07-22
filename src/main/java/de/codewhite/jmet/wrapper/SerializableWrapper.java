package de.codewhite.jmet.wrapper;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kaimatt.
 */
public class SerializableWrapper implements Serializable {
    private static ConcurrentHashMap instanceMap = new ConcurrentHashMap();
    private int counter;
    private Integer id;


    public SerializableWrapper(Serializable gadget, int counter) {
        this.id = this.hashCode();
        this.counter = counter;
        instanceMap.put(this.id, gadget);
    }


    private Object writeReplace()
            throws ObjectStreamException {

        counter -= 1;
        if (counter == 0) {
            return instanceMap.get(id);
        } else {
            return this;
        }

    }
}
