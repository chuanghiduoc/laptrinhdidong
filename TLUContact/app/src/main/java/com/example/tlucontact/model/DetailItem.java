package com.example.tlucontact.model;

import java.io.Serializable;

public class DetailItem implements Serializable {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String position;
    private String department;
    private String type; // "UNIT" hoặc "STAFF"

    public DetailItem() {}

    public DetailItem(String id, String name, String phone, String email,
                      String address, String position, String department, String type) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.position = position;
        this.department = department;
        this.type = type;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getPosition() { return position; }
    public String getDepartment() { return department; }
    public String getType() { return type; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setPosition(String position) { this.position = position; }
    public void setDepartment(String department) { this.department = department; }
    public void setType(String type) { this.type = type; }

    // toString() method for debugging
    @Override
    public String toString() {
        return "DetailItem{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
