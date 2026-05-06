package com.alisafaloba.trainbooking.Domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String role; // ADMIN / CUSTOMER

    public User() {}

    public User(String email, String role) {
        this.email = email;
        this.role = role;
    }
}