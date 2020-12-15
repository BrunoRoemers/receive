package io.roemers.receiver.errors;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@RestController
public class ErrorHandler implements ErrorController {

    /**
     * Mask certain errors with provided response
     */
    @ExceptionHandler(MaskedError.class)
    public ResponseEntity<?> maskedErrorHandler(MaskedError error) {
        error.printStackTrace();
        return error.response;
    }

    /**
     * Default error handling
     */
    @RequestMapping("${server.error.path:${error.path:/error}}")
    public ResponseEntity<?> handleError(HttpServletRequest req, HttpServletResponse res) {
        // publicly expose simple error message
        // NOTE: spring already logs stacktrace in console
        String requestURI = (String) req.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        HttpStatus statusCode = HttpStatus.valueOf(res.getStatus());
        return new ResponseEntity<String>(String.format(
            "%s %s : %s", req.getMethod(), requestURI, statusCode
        ), statusCode);
    }

    /**
     * See: https://github.com/spring-projects/spring-boot/issues/19844
     */
    @Override
    @Deprecated
    public String getErrorPath() {
        return null;
    }

}
