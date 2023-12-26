package org.hevodata.service;

import org.hevodata.exception.FileDataException;
import org.hevodata.model.FileData;

import java.util.List;

public interface DocumentService {
    List<FileData> search (String index, String text) throws FileDataException;
    void refresh(String source, String index) throws FileDataException;
}
