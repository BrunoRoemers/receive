package io.roemers.receiver.files;

import io.roemers.receiver.errors.PathParseError;

import java.nio.file.Path;

public interface FileService {

    Path getUploadsRoot();

    Path getPathFrom(String uri) throws PathParseError;

}
