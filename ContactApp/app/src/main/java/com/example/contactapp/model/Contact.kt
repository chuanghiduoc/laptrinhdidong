package com.example.contactapp.model

class Contact {
    private val id: Int
    private val name: String
    private val email: String
    private val phone: String

    constructor(id: Int, name: String, email: String, phone: String) {
        this.id = id
        this.name = name
        this.email = email
        this.phone = phone
    }
}