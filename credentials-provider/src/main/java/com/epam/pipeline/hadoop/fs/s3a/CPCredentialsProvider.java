package com.epam.pipeline.hadoop.fs.s3a;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.epam.pipeline.hadoop.fs.s3a.client.CloudPipeline;
import com.epam.pipeline.hadoop.fs.s3a.client.CloudPipelineBuilder;
import com.epam.pipeline.hadoop.fs.s3a.model.CPCredentials;
import com.epam.pipeline.hadoop.fs.s3a.model.DataStorage;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class CPCredentialsProvider implements AWSCredentialsProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(CPCredentialsProvider.class);

    private static final long EXPIRATION_LIMIT = 10;
    private DataStorage bucket;
    private CloudPipeline cloudPipeline;
    private CPCredentials credentials;

    public CPCredentialsProvider(final URI uri,
                                 final Configuration conf) {
        LOGGER.error("Initializing Cloud Pipeline credentials provider.");
        final String bucketName = Optional.ofNullable(uri)
                .map(URI::getHost)
                .orElse(StringUtils.EMPTY);
        if (StringUtils.isBlank(bucketName)) {
            throw new IllegalArgumentException("Bucket name is not specified.");
        }
        this.cloudPipeline = new CloudPipelineBuilder().build(conf);
        LOGGER.error("Loading bucket with name {} from Cloud Pipeline.", bucketName);
        this.bucket = this.cloudPipeline.load(bucketName);
        LOGGER.error("Successfully loaded bucket with ID {}.", this.bucket.getId());
    }

    @Override
    public AWSCredentials getCredentials() {
        LOGGER.error("Requesting credentials from Cloud Pipeline.");
        if (credentials == null || credentialsExpired()) {
            LOGGER.error("Credentials are missing or expired. Refreshing.");
            refresh();
        }
        LOGGER.error("Returning credentials.");
        return credentials;
    }

    @Override
    public void refresh() {
        LOGGER.error("Requesting new credentials from Cloud Pipeline.");
        credentials = cloudPipeline.getCredentials(bucket);
    }

    private boolean credentialsExpired() {
        if (credentials == null) {
            return true;
        }
        final LocalDateTime now = LocalDateTime.now();
        return now.until(credentials.getExpiration(), ChronoUnit.MINUTES) <= EXPIRATION_LIMIT;
    }
}
