package io.roemers.receiver.files;

import io.roemers.receiver.errors.PathParseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;

@RestController
@RequestMapping("/files")
public class FilesController {

    private final FileService fileService;

    public FilesController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/**")
    public ResponseEntity<?> acceptData(HttpServletRequest req)
    throws PathParseError {
        Path path = fileService.getPathFrom(req.getRequestURI());

        // security: path should have been scoped to uploads root
        assert path.startsWith(fileService.getUploadsRoot());

        return new ResponseEntity<>(path.toString(), HttpStatus.OK);
    }

}
