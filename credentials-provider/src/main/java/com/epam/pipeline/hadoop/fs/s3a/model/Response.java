package com.epam.pipeline.hadoop.fs.s3a.model;

public class Response<T> {

    private T payload;
    private String message;
    private ResultStatus status;

    public T getPayload() {
        return payload;
    }

    public String getMessage() {
        return message;
    }

    public ResultStatus getStatus() {
        return status;
    }
}
