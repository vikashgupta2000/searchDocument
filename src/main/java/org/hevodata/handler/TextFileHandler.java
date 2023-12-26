package org.hevodata.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hevodata.model.ExtensionType;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class TextFileHandler implements FileHandler {

    final static Logger logger = LogManager.getLogger(TextFileHandler.class);

    @Override
    public ExtensionType[] supportedExtensions() {
        return new ExtensionType[]{ExtensionType.TXT};
    }

    @Override
    public String extractText(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[inputStream.available()];
            int n;
            while (true) {
                if (!((n = inputStream.read(buffer)) > 0)) break;
                outputStream.write(buffer, 0, n);
            }
            return outputStream.toString("UTF-8");
        } catch (IOException e) {
            logger.error("Error while Extracting data", e);
            throw e;
        }
    }
}
