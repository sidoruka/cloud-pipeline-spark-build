package com.epam.pipeline.hadoop.fs.s3a.client;

import com.epam.pipeline.hadoop.fs.s3a.model.CPCredentials;
import com.epam.pipeline.hadoop.fs.s3a.model.CredentialsRequest;
import com.epam.pipeline.hadoop.fs.s3a.model.DataStorage;

public class CloudPipeline {

    private static final int WRITE_MASK = 1 << 1;

    private CloudPipelineClient client;

    public CloudPipeline(final String host, final String token) {
        this.client = new CloudPipelineClient(host, token);
    }

    public DataStorage load(final String name) {
        return client.findStorage(name);
    }

    public CPCredentials getCredentials(final DataStorage storage) {
        final CredentialsRequest request = buildCredentialsRequest(storage);
        return CPCredentials.fromTemp(client.getCredentials(request));
    }

    private CredentialsRequest buildCredentialsRequest(final DataStorage storage) {
        boolean requestWrite = (storage.getMask() & WRITE_MASK) == WRITE_MASK;
        return new CredentialsRequest(storage.getId(), requestWrite);
    }
}
