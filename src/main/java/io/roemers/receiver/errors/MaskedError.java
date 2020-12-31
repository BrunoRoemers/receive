package io.roemers.receiver.errors;

import org.springframework.http.ResponseEntity;

/**
 * Allow endpoints to abort processing while keeping control over what is shown to the user.
 * @see ReceiverControllerAdvice#maskedErrorHandler
 */
public class MaskedError extends Exception {

    public final Object response;

    public MaskedError(Object response) {
        super();
        this.response = response;
    }

    public MaskedError(String message, Object response) {
        super(message);
        this.response = response;
    }

    public MaskedError(String message, Object response, Throwable cause) {
        super(message, cause);
        this.response = response;
    }

    public MaskedError(Object response, Throwable cause) {
        super(cause);
        this.response = response;
    }

}
