package io.roemers.receiver.errors;

public class PathScopeError extends Exception {

    public PathScopeError() {
        super();
    }

    public PathScopeError(String message) {
        super(message);
    }

    public PathScopeError(String message, Throwable cause) {
        super(message, cause);
    }

    public PathScopeError(Throwable cause) {
        super(cause);
    }

}
