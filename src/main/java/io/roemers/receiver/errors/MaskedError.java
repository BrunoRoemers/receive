package io.roemers.receiver.errors;

import org.springframework.http.ResponseEntity;

/**
 * Allow endpoints to abort processing while keeping control over what is shown to the user.
 * @see ErrorHandler#maskedErrorHandler
 */
public class MaskedError extends Exception {

    public final ResponseEntity<?> response;

    public MaskedError(ResponseEntity<?> response) {
        super();
        this.response = response;
    }

    public MaskedError(String message, ResponseEntity<?> response) {
        super(message);
        this.response = response;
    }

    public MaskedError(String message, ResponseEntity<?> response, Throwable cause) {
        super(message, cause);
        this.response = response;
    }

    public MaskedError(ResponseEntity<?> response, Throwable cause) {
        super(cause);
        this.response = response;
    }

}
