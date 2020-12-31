package io.roemers.receiver.errors;

import io.roemers.receiver.files.StatusResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@ControllerAdvice
public class ReceiverControllerAdvice implements ErrorController {

    /**
     * Mask certain errors with provided response
     */
    @ExceptionHandler(MaskedError.class)
    public Object maskedErrorHandler(MaskedError error) {
        error.printStackTrace();
        return error.response;
    }

    /**
     * Uploaded file too big
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public StatusResponse largeFileErrorHandler(HttpServletRequest req, MaxUploadSizeExceededException error) {
      error.printStackTrace();
      return new StatusResponse(req, HttpStatus.BAD_REQUEST, "Uploaded file is too big");
    }

    /**
     * Could not parse path from uri
     */
    @ExceptionHandler(PathParseError.class)
    public StatusResponse pathParseErrorHandler(HttpServletRequest req, PathParseError error) {
      error.printStackTrace();
      return new StatusResponse(req, HttpStatus.BAD_REQUEST, error.getLocalizedMessage());
    }

    /**
     * Missing multipart fragment
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public StatusResponse missingMultipartFragmentErrorHandler(
      HttpServletRequest req, MissingServletRequestPartException error
    ) {
      error.printStackTrace();
      return new StatusResponse(req, HttpStatus.BAD_REQUEST, error.getLocalizedMessage());
    }

    /**
     * Provided file is empty
     */
    @ExceptionHandler(NoFileError.class)
    public StatusResponse noFileErrorHandler(HttpServletRequest req, NoFileError error) {
      error.printStackTrace();
      return new StatusResponse(req, HttpStatus.BAD_REQUEST, error.getLocalizedMessage());
    }

    /**
     * Default error handling
     */
    @RequestMapping("${server.error.path:${error.path:/error}}")
    public StatusResponse handleError(HttpServletRequest req, HttpServletResponse res) {
        // publicly expose simple error message
        // NOTE: spring already logs stacktrace in console
        HttpStatus statusCode = HttpStatus.valueOf(res.getStatus());
        return new StatusResponse(req, statusCode, statusCode.getReasonPhrase());
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
