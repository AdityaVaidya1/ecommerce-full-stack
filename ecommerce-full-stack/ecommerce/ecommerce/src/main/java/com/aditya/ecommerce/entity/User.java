package com.aditya.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users") // 'user' is a reserved word in many databases
@Data                    // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor       // Generates a no-arg constructor
@AllArgsConstructor      // Generates an all-args constructor
@Builder                 // Enables builder pattern
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.BUYER;  // default role is BUYER

    private boolean enabled = false;     // true after email verification

    private boolean approved = false;    // true after admin approves seller

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;
}
