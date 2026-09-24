package app.whosin.anonymousIdentities.exception;

public class InvalidAnonymousTokenException extends RuntimeException {
    public InvalidAnonymousTokenException() {
        super("Invalid anonymous token");
    }
}
