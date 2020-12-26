package io.roemers.receiver.errors;

public class PathParseError extends Exception {

    public PathParseError() {
        super();
    }

    public PathParseError(String message) {
        super(message);
    }

    public PathParseError(String message, Throwable cause) {
        super(message, cause);
    }

    public PathParseError(Throwable cause) {
        super(cause);
    }

}
