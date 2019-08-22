package com.epam.pipeline.hadoop.fs.s3a.model;

public class CredentialsRequest {

    private Long id;
    private boolean read;
    private boolean readVersion;
    private boolean write;
    private boolean writeVersion;

    public CredentialsRequest(final Long id, final boolean write) {
        this.id = id;
        this.read = true;
        this.readVersion = false;
        this.write = write;
        this.writeVersion = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isReadVersion() {
        return readVersion;
    }

    public void setReadVersion(boolean readVersion) {
        this.readVersion = readVersion;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public boolean isWriteVersion() {
        return writeVersion;
    }

    public void setWriteVersion(boolean writeVersion) {
        this.writeVersion = writeVersion;
    }
}
