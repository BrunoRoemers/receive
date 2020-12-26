package io.roemers.receiver.errors;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

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
     * Uploaded file too big
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> largeFileErrorHandler(HttpServletRequest req, MaxUploadSizeExceededException error) {
      error.printStackTrace();
      return errorResponse(req, HttpStatus.BAD_REQUEST, "Uploaded file is too big");
    }

    /**
     * Could not parse path from uri
     */
    @ExceptionHandler(PathParseError.class)
    public ResponseEntity<?> pathParseErrorHandler(HttpServletRequest req, PathParseError error) {
      error.printStackTrace();
      return errorResponse(req, HttpStatus.BAD_REQUEST, error.getLocalizedMessage());
    }

    /**
     * Missing multipart fragment
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> missingMultipartFragmentErrorHandler(
      HttpServletRequest req, MissingServletRequestPartException error
    ) {
      error.printStackTrace();
      return errorResponse(req, HttpStatus.BAD_REQUEST, error.getLocalizedMessage());
    }

    /**
     * Provided file is empty
     */
    @ExceptionHandler(NoFileError.class)
    public ResponseEntity<?> noFileErrorHandler(HttpServletRequest req, NoFileError error) {
      error.printStackTrace();
      return errorResponse(req, HttpStatus.BAD_REQUEST, error.getLocalizedMessage());
    }

    /**
     * Default error handling
     */
    @RequestMapping("${server.error.path:${error.path:/error}}")
    public ResponseEntity<?> handleError(HttpServletRequest req, HttpServletResponse res) {
        // publicly expose simple error message
        // NOTE: spring already logs stacktrace in console
        HttpStatus statusCode = HttpStatus.valueOf(res.getStatus());
        return errorResponse(req, statusCode);
    }

    protected ResponseEntity<?> errorResponse(HttpServletRequest req, HttpStatus statusCode) {
      return errorResponse(req, statusCode, null);
    }

    /**
     * Print a standard error response. The http method and request uri are taken
     * from the request object. The status code can be customized. An optional
     * message can be added.
     */
    protected ResponseEntity<?> errorResponse(HttpServletRequest req, HttpStatus statusCode, String message) {
      // get request URI
      String requestURI = (String) req.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
      if (requestURI == null || requestURI.isBlank()) {
        requestURI = req.getRequestURI();
      }

      // standard message
      String msg = String.format("%s %s : %s", req.getMethod(), requestURI, statusCode);

      // append custom message
      if (message != null && !message.isBlank()) {
        msg = String.format("%s : %s", msg, message);
      }

      return new ResponseEntity<String>(msg, statusCode);
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
