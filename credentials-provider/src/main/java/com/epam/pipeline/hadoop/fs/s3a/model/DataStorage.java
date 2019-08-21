package com.epam.pipeline.hadoop.fs.s3a.model;

public class DataStorage {

    private Long id;
    private String name;
    private String path;
    private String type;
    private Integer mask;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public Integer getMask() {
        return mask;
    }
}
