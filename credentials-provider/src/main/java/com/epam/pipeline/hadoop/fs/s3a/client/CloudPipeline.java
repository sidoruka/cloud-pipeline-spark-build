package com.epam.pipeline.hadoop.fs.s3a.client;

import com.epam.pipeline.hadoop.fs.s3a.model.CPCredentials;
import com.epam.pipeline.hadoop.fs.s3a.model.DataStorage;

public class CloudPipeline {

    private CloudPipelineClient client;

    public CloudPipeline(final String host, final String token) {
        this.client = new CloudPipelineClient(host, token);
    }

    public DataStorage load(final String name) {
        return client.findStorage(name);
    }

    public CPCredentials getCredentials(final DataStorage storage) {
        return CPCredentials.fromTemp(client.getCredentials(storage));
    }
}
