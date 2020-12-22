package io.roemers.receiver.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
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
