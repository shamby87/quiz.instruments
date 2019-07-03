package com.kidscademy.quiz.model;


public class Model {
    private int id;

    private String manufacturer;
    private String model;
    private String version;
    private int apiLevel;

    public void setId(int id) {
        this.id = id;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setApiLevel(int apiLevel) {
        this.apiLevel = apiLevel;
    }

    public int getId() {
        return id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getVersion() {
        return version;
    }

    public int getApiLevel() {
        return apiLevel;
    }
}