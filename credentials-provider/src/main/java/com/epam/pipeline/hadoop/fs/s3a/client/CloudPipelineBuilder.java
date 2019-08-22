package com.epam.pipeline.hadoop.fs.s3a.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;

public class CloudPipelineBuilder {

    public CloudPipeline build(Configuration conf) {
        final String host = getValueFromConfOrEnv(conf, "fs.s3a.aws.credentials.cp.host", "API");
        final String token = getValueFromConfOrEnv(conf, "fs.s3a.aws.credentials.cp.token", "API_TOKEN");
        return new CloudPipeline(host, token);
    }

    private String getValueFromConfOrEnv(final Configuration conf,
                                         final String propertyName,
                                         final String envVarName) {
        final String confValue = conf.get(propertyName);
        if (StringUtils.isNotBlank(confValue)) {
            return confValue;
        }
        final String envVarValue = System.getenv(envVarName);
        if (StringUtils.isNotBlank(envVarValue)) {
            return envVarValue;
        }
        throw new IllegalArgumentException(String.format(
                "Failed to get property from configuration '%s' or environment variables '%s'.",
                propertyName, envVarName));
    }
}
