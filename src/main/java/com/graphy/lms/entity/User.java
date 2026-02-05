package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users") // Ensure this matches your actual database table name for users
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    // You can add other fields (password, phone, etc.) as needed later
}