package net.crew_vre.authorization;


public class AccessDeniedException extends Exception{

    public AccessDeniedException(String msg) {
        super(msg);
    }

}
