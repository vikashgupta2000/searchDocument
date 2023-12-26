package org.hevodata.handler;

import org.hevodata.model.ExtensionType;

import java.io.IOException;
import java.io.InputStream;

public interface FileHandler {
    ExtensionType[] supportedExtensions();
    String extractText(InputStream inputStream) throws IOException;
}
