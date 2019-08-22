package com.epam.pipeline.hadoop.fs.s3a.client;

import com.epam.pipeline.hadoop.fs.s3a.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class CloudPipelineClient {

    private static final int HTTP_CLIENT_TIMEOUT_MS = 5 * 1000;

    private String host;
    private String token;
    private ObjectMapper mapper;
    private HttpClient client;

    public CloudPipelineClient(String host, String token) {
        this.host = host;
        this.token = token;
        this.mapper = buildMapper();
        this.client = buildClient();
    }

    public DataStorage findStorage(final String name) {
        try {
            final URI uri = new URIBuilder(host + "/datastorage/find")
                    .addParameter("id", name)
                    .build();
            final HttpUriRequest request = RequestBuilder.get(uri).build();
            return executeRequest(request, new TypeReference<Response<DataStorage>>() {});
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public TemporaryCredentials getCredentials(final CredentialsRequest credentialsRequest) {
        try {
             final HttpUriRequest request = RequestBuilder.post(
                    new URIBuilder(host + "/datastorage/tempCredentials/").build())
                    .setEntity(new StringEntity(mapper.writeValueAsString(
                            Collections.singletonList(credentialsRequest))))
                    .build();
            return executeRequest(request, new TypeReference<Response<TemporaryCredentials>>() {});
        } catch (URISyntaxException | JsonProcessingException | UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }


    private <T> T executeRequest(final HttpUriRequest request, final TypeReference<Response<T>> type) {
        try {
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Authorization", "Bearer " + token);
            final HttpResponse response = this.client.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IllegalArgumentException("Cloud Pipeline responded with unexpected status: " +
                        response.getStatusLine());
            }
            final String responseBody = EntityUtils.toString(response.getEntity());
            if (StringUtils.isBlank(responseBody)) {
                throw new IllegalArgumentException("Cloud Pipeline responded with empty body.");
            }
            final Response<T> pipelineResponse = mapper.readValue(responseBody, type);
            if (pipelineResponse.getStatus() != ResultStatus.OK) {
                throw new IllegalArgumentException("Cloud pipeline returned an error: " +
                        pipelineResponse.getMessage());
            }
            final T payload = pipelineResponse.getPayload();
            if (payload == null) {
                throw new IllegalArgumentException("Cloud pipeline returned empty response");
            }
            return payload;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private ObjectMapper buildMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private HttpClient buildClient() {
        try {
            final RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(HTTP_CLIENT_TIMEOUT_MS)
                .setConnectionRequestTimeout(HTTP_CLIENT_TIMEOUT_MS)
                .setSocketTimeout(HTTP_CLIENT_TIMEOUT_MS)
                    .build();
            final SSLContext  sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, (x509CertChain, authType) -> true)
                    .build();
            return HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setDefaultRequestConfig(config)
                    .build();
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
