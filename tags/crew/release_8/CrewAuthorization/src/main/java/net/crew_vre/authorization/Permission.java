package net.crew_vre.authorization;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: Permission.java 1092 2009-03-11 19:01:38Z cmmaj $
 */
public enum Permission {

    READ(1), WRITE(2), DELETE(4);
        
    private Permission(int value) {
        this.value = value;
    }

    public int intValue() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }

    private int value;
}
