package io.roemers.receiver.files;

import io.roemers.receiver.errors.ConfigurationError;
import io.roemers.receiver.errors.MaskedError;
import io.roemers.receiver.errors.NoFileError;
import io.roemers.receiver.errors.PathParseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.nio.file.Path;

@RestController
@RequestMapping("/files")
public class FilesController {

    private final FileService fileService;

    public FilesController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(
      value = "/**",
      consumes = {"multipart/form-data"}
    )
    public StatusResponse acceptData(
      HttpServletRequest req,
      @RequestParam("file") MultipartFile file
    ) throws ConfigurationError, PathParseError, NoFileError, MaskedError {
        // get path from url
        Path path = fileService.getPathFrom(
          req.getRequestURI(), "/files", fileService.getUploadsRoot()
        );

        // security: standard response
        StatusResponse resp = new StatusResponse(req, HttpStatus.OK, "received");

        try {
          fileService.saveFile(file, path);
        } catch (NoFileError e) {
          // user forgot to submit file
          throw e;
        } catch (Exception e) {
          // do not leak info about file system
          throw new MaskedError(resp, e);
        }

        return resp;
    }

}
