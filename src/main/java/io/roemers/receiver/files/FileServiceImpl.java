package io.roemers.receiver.files;

import io.roemers.receiver.errors.ConfigurationError;
import io.roemers.receiver.errors.NoFileError;
import io.roemers.receiver.errors.PathParseError;
import io.roemers.receiver.errors.PathScopeError;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

@Service
public class FileServiceImpl implements FileService {

  private Path uploadsRoot;

  public Path getUploadsRoot()
  throws ConfigurationError {
    verifyUploadsRoot();
    return uploadsRoot;
  }

  @Autowired
  public void setUploadsRoot(@Value("${receiver.uploadsRoot}") String uploadsRoot)
  throws ConfigurationError {
    // replace ~/ with $HOME/
    String pathStr = uploadsRoot.replaceFirst("^~/", System.getProperty("user.home") + "/");

    // store normalized absolute path
    this.uploadsRoot = Paths.get(pathStr).normalize().toAbsolutePath();

    // verify
    this.verifyUploadsRoot();
  }

  public void verifyUploadsRoot() throws ConfigurationError {
    // make sure uploads root exists
    if (!Files.exists(this.uploadsRoot)) {
      throw new ConfigurationError(String.format("%s does not exist", this.uploadsRoot));
    }

    // make sure uploads root is a folder
    if (!Files.isDirectory(this.uploadsRoot)) {
      throw new ConfigurationError(String.format("%s is not a directory", this.uploadsRoot));
    }
  }

  public void verifyPathScope(Path path, Path scope) throws PathScopeError {
    if (!path.normalize().startsWith(scope)) {
      throw new PathScopeError(String.format("Path %s is not scoped to %s", path, scope));
    }
  }

  public Path getPathFrom(String uri, String splitStr, Path pathRoot)
  throws PathParseError {
    // NOTE: frags[1] should not end with a slash
    if (!splitStr.endsWith("/")) splitStr += "/";
    String[] frags = uri.split(splitStr);

    // make sure url contains path
    if (frags.length < 2 || frags[1].isBlank()) {
      throw new PathParseError(String.format("Could not get path from uri %s", uri));
    }

    Path path = pathRoot.resolve(UriUtils.decode(frags[1], "utf-8"));
    Path normalizedPath = path.normalize();

    // security: do not support ./ or ../ in user submitted paths
    if (!path.equals(normalizedPath)) {
      throw new PathParseError(String.format("Path from uri %s is not valid", uri));
    }

    return normalizedPath.toAbsolutePath();
  }

  public Path createDirectories(Path path)
  throws IOException {
    Path parent = path.getParent();

    // make sure parent is a directory (recursive)
    if (!Files.isDirectory(parent)) {
      parent = createDirectories(parent);
      path = parent.resolve(path.getFileName());
    }

    // dodge files with same path
    path = appendNumberToDirName(path);

    // directory exists
    if (Files.isDirectory(path)) return path;

    // create directory
    return Files.createDirectory(path);
  }

  public Path appendNumberToFileName(Path path) {
    return appendNumberToPath(path, Files::exists);
  }

  public Path appendNumberToDirName(Path path) {
    return appendNumberToPath(path, newPath -> Files.exists(newPath) && !Files.isDirectory(newPath));
  }

  public Path appendNumberToPath(Path path, Function<Path, Boolean> shouldAppend) {
    // get file name and extension form path
    String[] frags = path.getFileName().toString().split("\\.", 2);
    String fileName = !frags[0].isBlank() ? frags[0] + "_" : "";
    String fileExt = frags.length > 1 ? "." + frags[1] : "";

    // append/increment number if shouldAppend returns true
    for (int i = 1; shouldAppend.apply(path); i++) {
      path = path.resolveSibling(fileName + i + fileExt);
    }

    // return modified path
    return path;
  }

  public void saveFile(MultipartFile multipartFile, Path inPath)
  throws ConfigurationError, NoFileError, PathScopeError, IllegalStateException, IOException {
    // abort on empty file
    if (multipartFile.isEmpty()) throw new NoFileError("Uploaded file is empty");

    // security: inPath should be scoped to uploads root
    verifyPathScope(inPath, getUploadsRoot());

    // create directories
    Path dirPath = createDirectories(inPath.getParent());
    inPath = dirPath.resolve(inPath.getFileName());

    // dodge files (including dirs) with same path
    Path outPath = appendNumberToFileName(inPath);

    // security: outPath should be scoped to uploads root
    verifyPathScope(outPath, getUploadsRoot());

    // save file
    Files.createFile(outPath);
    multipartFile.transferTo(outPath);
  }
}
