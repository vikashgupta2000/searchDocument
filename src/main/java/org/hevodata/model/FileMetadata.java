package org.hevodata.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileMetadata {

    private String fileId;
    private SourceType source;
    private String name;
    private String path;
    private String contentHash;
    private ExtensionType extension;

    public FileMetadata(){}

    public FileMetadata(String fileId, SourceType source, String name, String path, String contentHash, ExtensionType extension) {
        this.fileId = fileId;
        this.source = source;
        this.name = name;
        this.path = path;
        this.contentHash = contentHash;
        this.extension = extension;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public SourceType getSource() {
        return source;
    }

    public void setSource(SourceType source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public ExtensionType getExtension() {
        return extension;
    }

    public void setExtension(ExtensionType extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "FileMetadata{" +
                "fileId='" + fileId + '\'' +
                ", source=" + source +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", contentHash='" + contentHash + '\'' +
                ", extension=" + extension +
                '}';
    }
}
