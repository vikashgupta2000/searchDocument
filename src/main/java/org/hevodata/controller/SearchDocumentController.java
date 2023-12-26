package org.hevodata.controller;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hevodata.exception.FileDataException;
import org.hevodata.model.FileData;
import org.hevodata.model.SourceType;
import org.hevodata.response.RefreshResponse;
import org.hevodata.response.SearchResponse;
import org.hevodata.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/client/{client_id}")
public class SearchDocumentController {

    final static Logger logger = LogManager.getLogger(SearchDocumentController.class);
    @Autowired
    private DocumentService documentService;

    private void searchValidation (String clientId, String q) throws FileDataException {
        if(StringUtils.isEmpty(clientId) || StringUtils.isEmpty(q)) {
            throw new FileDataException("SearchValidationException : clientId or searched text cannot be empty");
        }
    }

    @GetMapping("/search")
    public SearchResponse search(@PathVariable("client_id") String clientId, @RequestParam String q) {
        logger.info("Initiating search API with parameters. clientId: {}, q : {} ", clientId, q);
        SearchResponse searchResponse = new SearchResponse();
        try {
            searchValidation(clientId, q);
            List<FileData> result =  documentService.search(clientId, q);
            List<String> filePaths = new ArrayList<>();
            for(FileData filedata : result) {
                filePaths.add(filedata.getMetadata().getPath());
            }
            searchResponse.setFilePaths(filePaths);
        } catch (FileDataException e) {
            logger.error("Error while searching for result", e);
            searchResponse.setErrorMessage(e.getMessage());
        }
        return searchResponse;
    }

    private void refreshValidation (String clientId, String source) throws FileDataException {
        if(StringUtils.isEmpty(clientId)) {
            throw new FileDataException("RefreshValidationException : clientId cannot be empty");
        }
        if(StringUtils.isNotEmpty(source) && !EnumUtils.isValidEnum(SourceType.class, source)) {
            throw new FileDataException("RefreshValidationException : Source passed is incorrect");
        }
    }

    @GetMapping("/refresh")
    public RefreshResponse refresh(@PathVariable("client_id") String clientId, @RequestParam(defaultValue = "DROP_BOX") String source) {
        logger.info("Initiating refresh API with parameters. clientId: {}, source : {}", clientId, source);
        RefreshResponse refreshResponse = new RefreshResponse();
        try {
            refreshValidation(clientId, source);
            documentService.refresh(source,clientId);
            refreshResponse.setMessage("Data was successfully refreshed");
        } catch (FileDataException e) {
            logger.error("Error while refreshing data", e);
            refreshResponse.setMessage(e.getMessage());
        }
        return refreshResponse;
    }
}
