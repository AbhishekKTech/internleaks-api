package com.abhishekktech.internleaks.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String websiteUrl;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}