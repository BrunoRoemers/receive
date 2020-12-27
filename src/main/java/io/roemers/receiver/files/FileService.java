package io.roemers.receiver.files;

import io.roemers.receiver.errors.ConfigurationError;
import io.roemers.receiver.errors.NoFileError;
import io.roemers.receiver.errors.PathParseError;
import io.roemers.receiver.errors.PathScopeError;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * @return path of the folder where all uploads are stored
     * @throws ConfigurationError if uploads root is not a directory
     */
    Path getUploadsRoot()
    throws ConfigurationError;

    /**
     * initialize the uploads root for this service.
     * (E.g. using Spring dependency injection.)
     * @param uploadsRoot
     * @throws ConfigurationError if uploads root is not a directory
     */
    void setUploadsRoot(String uploadsRoot);

    /**
     * Make sure path is a directory.
     * @throws ConfigurationError if check fails.
     */
    void verifyDirectory(Path directory)
    throws ConfigurationError;

    /**
     * Normalize path and check if it starts with the scope path.
     * @param path
     * @param scope
     * @throws PathScopeError if check fails
     */
    void verifyPathScope(Path path, Path scope)
    throws PathScopeError;

    /**
     * Extract path string from uri and return as {@link java.nio.file.Path}
     * @param uri 
     * @param splitStr uri is split using this string and the second fragment is used as path string
     * @param pathRoot path that is prepended to extracted path
     * @return path that was extracted from uri
     * @throws PathParseError if path cannot be obtained from uri
     */
    Path getPathFrom(String uri, String splitStr, Path pathRoot)
    throws PathParseError;

    /**
     * Create directories (if they do not already exist) as specified
     * by the input path. If a path segment refers to a file,
     * a number is added to that segment, such that child directories
     * can be created.
     * @param path of folders that need to be created
     * @return path which is guaranteed to be a folder (may differ from input)
     */
    Path createDirectories(Path path)
    throws IOException;

    /**
     * {@link #appendNumberToPath(Path, Function)}
     * with lambda that returns true if path already exists.
     * I.e. dodge files (including directories).
     */
    Path appendNumberToFileName(Path path);

    /**
     * {@link #appendNumberToPath(Path, Function)}
     * with lambda that returns true if path apready exists and is not a directory.
     * I.e. dodge files but return on directories.
     * @param path
     * @return
     */
    Path appendNumberToDirName(Path path);

    /**
     * Append a number to the last segment of path if shouldAppend
     * returns true. The number is incremented for each consecutive time
     * that shouldAppend returns true. When shouldAppend returns false,
     * the new path is returned.
     * @param path number will be added to last segment of this path
     * @param shouldAppend append/increment number if lambda returns true
     * @return new path whose last segment may have a number appended to it
     */
    Path appendNumberToPath(Path path, Function<Path, Boolean> shouldAppend);

    /**
     * Store submitted file on file system. The location is specified by path, but
     * numbers are appended to path segments to avoid writing to or deleting
     * existing files.
     * 
     * @param multipartFile
     * @param path
     * @throws ConfigurationError    if uploads root is not a directory
     * @throws NoFileError           if multipartFile is empty
     * @throws PathScopeError        if path is not properly scoped (Note that
     *                               numbers may be appended to one or more
     *                               segments)
     * @throws IllegalStateException if something went wrong while saving the file
     * @throws IOException           if something went wrong while saving the file
     */
    void saveFile(MultipartFile multipartFile, Path path)
    throws ConfigurationError, NoFileError, PathScopeError, IllegalStateException, IOException;

}
