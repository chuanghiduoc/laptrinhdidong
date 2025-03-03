package com.example.tlucontact.model;

public class CBNV {
    private String name;
    private String position;
    private String phone;
    private String email;
    private String unit;

    public CBNV(String name, String position, String phone, String email, String unit) {
        this.name = name;
        this.position = position;
        this.phone = phone;
        this.email = email;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }
}
