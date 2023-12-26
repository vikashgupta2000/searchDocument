package org.hevodata.external;

import com.dropbox.core.DbxException;
import org.hevodata.model.FileMetadata;
import org.hevodata.model.SourceType;

import java.io.InputStream;
import java.util.List;

public interface PersistentStorage {

    SourceType getSourceType();
    List<FileMetadata> getAllFilePath(String clientId) throws DbxException;
    InputStream readFile(String clientId, String path) throws DbxException;

}
