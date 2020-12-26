package io.roemers.receiver.errors;

public class NoFileError extends Exception {

    public NoFileError() {
        super();
    }

    public NoFileError(String message) {
        super(message);
    }

    public NoFileError(String message, Throwable cause) {
        super(message, cause);
    }

    public NoFileError(Throwable cause) {
        super(cause);
    }

}
