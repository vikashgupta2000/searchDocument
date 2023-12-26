package org.hevodata.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileData {
    private FileMetadata metadata;
    private String content;


    public FileData() {}
    public FileData(FileMetadata metadata, String content) {
        this.metadata = metadata;
        this.content = content;
    }

    public FileMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(FileMetadata metadata) {
        this.metadata = metadata;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "FileData{" +
                "metadata=" + metadata +
                ", content='" + content + '\'' +
                '}';
    }
}
