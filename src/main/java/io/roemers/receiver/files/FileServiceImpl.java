package io.roemers.receiver.files;

import io.roemers.receiver.errors.PathParseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileServiceImpl implements FileService {

    private Path uploadsRoot;

    public Path getUploadsRoot() {
        return uploadsRoot;
    }

    @Autowired
    public void setUploadsRoot(@Value("${receiver.uploadsRoot}") String uploadsRoot) {
        // replace ~/ with $HOME/
        String pathStr = uploadsRoot.replaceFirst("^~/", System.getProperty("user.home") + "/");

        // store normalized absolute path
        this.uploadsRoot = Paths.get(pathStr).normalize().toAbsolutePath();
    }

    public Path getPathFrom(String uri)
    throws PathParseError {
        String[] frags = uri.split("/files/");

        // make sure url contains path
        if (frags.length < 2 || frags[1].length() < 1 ) {
            throw new PathParseError(String.format("Could not get path from uri %s", uri));
        }

        Path path = getUploadsRoot().resolve(UriUtils.decode(frags[1], "utf-8"));
        Path normalizedPath = path.normalize();

        // security: do not support ./ or ../ in user submitted paths
        if (!path.equals(normalizedPath)) {
            throw new PathParseError(String.format("Path from uri %s is not valid", uri));
        }

        return normalizedPath;
    }

}
