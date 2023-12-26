package org.hevodata.external;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hevodata.model.ExtensionType;
import org.hevodata.model.FileMetadata;
import org.hevodata.model.SourceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class DropBox implements PersistentStorage {
    final static Logger logger = LogManager.getLogger(DropBox.class);
    @Value("${dropbox.access_token}")
    private String ACCESS_TOKEN;

    @Override
    public SourceType getSourceType() {
        return SourceType.DROP_BOX;
    }

    @Override
    public List<FileMetadata> getAllFilePath(String clientId) throws DbxException {
        List<FileMetadata> metadataList = new ArrayList<>();
        try {
            listFiles(dropboxClient(clientId), "", metadataList);
        } catch (DbxException e) {
            logger.error("Error while reading all the file Paths", e);
            throw e;
        }
        return metadataList;
    }

    @Override
    public InputStream readFile(String clientId, String path) throws DbxException {
        DbxClientV2 dbClient = dropboxClient(clientId);
        try {
            return dbClient.files().download(path).getInputStream();
        } catch (DbxException e) {
            logger.error("Error while reading file", e);
            throw e;
        }
    }

    private DbxClientV2 dropboxClient(String client) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("DocumentSearch-"+client).build();
        return new DbxClientV2(config, ACCESS_TOKEN);
    }

    private void listFiles(DbxClientV2 client, String path,  List<FileMetadata> metadataList) throws DbxException {
        ListFolderResult result = client.files().listFolder(path);

        while (true) {
            for (Metadata dbMetadata : result.getEntries()) {
                String pathDisplay = dbMetadata.getPathDisplay();
                if (dbMetadata instanceof com.dropbox.core.v2.files.FileMetadata) {
                    com.dropbox.core.v2.files.FileMetadata fileMetadata = (com.dropbox.core.v2.files.FileMetadata) dbMetadata;
                    String extension =  fileMetadata.getName().substring(fileMetadata.getName().lastIndexOf(".") + 1).toUpperCase();
                    if(EnumUtils.isValidEnum(ExtensionType.class, extension)) {
                        FileMetadata metadata = new FileMetadata(fileMetadata.getId(), getSourceType(), fileMetadata.getName(),
                                fileMetadata.getPathDisplay(), fileMetadata.getContentHash(), ExtensionType.valueOf(extension));
                        metadataList.add(metadata);
                    }
                } else {
                    // Recursively list all files in the sub-folders
                    listFiles(client, pathDisplay, metadataList);
                }
            }
            if (!result.getHasMore()) {
                break;
            }
            result = client.files().listFolderContinue(result.getCursor());
        }
    }


}
