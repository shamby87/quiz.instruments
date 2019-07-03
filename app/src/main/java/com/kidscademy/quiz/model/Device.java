package com.kidscademy.quiz.model;

public class Device {
    private int id;

    private Model model = new Model();

    private String serial;

    public Device() {
    }

    public Device(int id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public int getId() {
        return id;
    }

    public Model getModel() {
        return model;
    }

    public String getSerial() {
        return serial;
    }
}
