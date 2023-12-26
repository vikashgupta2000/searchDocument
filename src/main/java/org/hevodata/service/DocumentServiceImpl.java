package org.hevodata.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hevodata.dao.ElasticSearch;
import org.hevodata.exception.FileDataException;
import org.hevodata.external.PersistentStorage;
import org.hevodata.handler.FileHandler;
import org.hevodata.model.ExtensionType;
import org.hevodata.model.FileData;
import org.hevodata.model.FileMetadata;
import org.hevodata.model.SourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentServiceImpl implements DocumentService {
    final static Logger logger = LogManager.getLogger(DocumentServiceImpl.class);
    @Autowired
    private ElasticSearch elasticSearch;

    private static final Map<SourceType, PersistentStorage> documentSourceCache = new HashMap<>();

    private static final Map<ExtensionType, FileHandler> dataHandlerCache = new HashMap<>();

    @Autowired
    private DocumentServiceImpl(List<PersistentStorage> persistentStorages,
                                List<FileHandler> fileHandlers) {
        for (PersistentStorage service : persistentStorages) {
            documentSourceCache.put(service.getSourceType(), service);
        }

        for (FileHandler fileHandler : fileHandlers) {
            for(ExtensionType extensionType : fileHandler.supportedExtensions()) {
                dataHandlerCache.put(extensionType, fileHandler);
            }
        }
    }

    @Override
    public List<FileData> search(String index, String text) throws FileDataException {
        try {
            return elasticSearch.match(index, "content", text);
        } catch (Exception e) {
            logger.error("Error while search", e);
            throw new FileDataException("There is an error while searching", e);
        }
    }

    @Override
    public void refresh(String source, String index) throws FileDataException {
        List<String> updatedFiles = new ArrayList<>();
        try {
            PersistentStorage persistentStorage = documentSourceCache.get(SourceType.valueOf(source));
            List<FileMetadata> fileMetadataList = persistentStorage.getAllFilePath(index);
            for (FileMetadata metadata : fileMetadataList) {
                ExtensionType extension = metadata.getExtension();
                if (dataHandlerCache.containsKey(extension)) {
                    logger.info("Extension {} is present", extension);
                    FileData savedFileData = elasticSearch.get(index, metadata.getFileId(), "content");
                    if (savedFileData == null || !savedFileData.getMetadata().getContentHash().equals(metadata.getContentHash())) {
                        logger.info("updating data in ES");
                        InputStream inputStream = persistentStorage.readFile(index, metadata.getPath());
                        FileHandler fileHandler = dataHandlerCache.get(extension);
                        String content = fileHandler.extractText(inputStream);
                        FileData fileData = new FileData(metadata, content);

                        elasticSearch.post(index, fileData);
                    } else {
                        logger.info("The file {} is already present in ES with same contentHash", metadata.getPath());
                    }
                } else {
                    logger.warn("Extension not supported. File Path : {}", metadata.getPath());
                }
                updatedFiles.add(metadata.getFileId());
            }
            deleteAllExtraDocuments(index, updatedFiles);
        } catch (Exception e) {
            logger.error("Error while refresh", e);
            throw new FileDataException("There is an error while refreshing data for the client", e);
        }
    }

    private void deleteAllExtraDocuments(String index, List<String> fileDataIdToRetain) throws IOException {
        List<FileData> documentPresent = elasticSearch.getAllExcept(fileDataIdToRetain, index, "content");
        for (FileData dataDoc : documentPresent) {
            elasticSearch.delete(index, dataDoc.getMetadata().getFileId());
        }
    }
}
