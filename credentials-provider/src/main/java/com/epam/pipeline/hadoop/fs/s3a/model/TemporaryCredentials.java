package com.epam.pipeline.hadoop.fs.s3a.model;

public class TemporaryCredentials {

    private String accessKey;
    private String keyID;
    private String token;
    private String expiration;
    private String region;

    public String getAccessKey() {
        return accessKey;
    }

    public String getKeyID() {
        return keyID;
    }

    public String getToken() {
        return token;
    }

    public String getExpiration() {
        return expiration;
    }

    public String getRegion() {
        return region;
    }
}
