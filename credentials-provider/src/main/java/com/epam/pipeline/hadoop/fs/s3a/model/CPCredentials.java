package com.epam.pipeline.hadoop.fs.s3a.model;

import com.amazonaws.auth.AWSSessionCredentials;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CPCredentials implements AWSSessionCredentials {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    private String sessionToken;
    private String accessKeyId;
    private String secretKey;
    private LocalDateTime expiration;

    public static CPCredentials fromTemp(final TemporaryCredentials credentials) {
        CPCredentials cpCredentials = new CPCredentials();
        cpCredentials.accessKeyId = credentials.getKeyID();
        cpCredentials.secretKey = credentials.getAccessKey();
        cpCredentials.sessionToken = credentials.getToken();
        cpCredentials.expiration = LocalDateTime.parse(credentials.getExpiration(), DATE_TIME_FORMATTER);
        return cpCredentials;
    }

    @Override
    public String getSessionToken() {
        return sessionToken;
    }

    @Override
    public String getAWSAccessKeyId() {
        return accessKeyId;
    }

    @Override
    public String getAWSSecretKey() {
        return secretKey;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }
}
