package io.roemers.receiver.files;

import org.springframework.http.HttpStatus;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

public class StatusResponse {

    public String message;
    public String statusCode;
    public String requestMethod;
    public String requestURI;

    public StatusResponse(HttpServletRequest req, HttpStatus statusCode) {
        this(req, statusCode, null);
    }

    public StatusResponse(HttpServletRequest req, HttpStatus statusCode, String message) {
        this.message = message;
        this.statusCode = statusCode.toString();
        this.requestMethod = req.getMethod();
        this.requestURI = getRequestURI(req);
    }

    public String getRequestURI(HttpServletRequest req) {
        String requestURI = (String) req.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        if (requestURI == null || requestURI.isBlank()) {
            requestURI = req.getRequestURI();
        }
        return requestURI;
    }

    public String toString() {
        // standard message
        String msg = String.format("%s %s : %s", requestMethod, requestURI, statusCode);

        // append custom message
        if (message != null && !message.isBlank()) {
            msg = String.format("%s : %s", msg, message);
        }

        return msg;
    }

}
