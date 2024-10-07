package com.asalavei.weathertracker.dbaccess.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "login", length = 39, nullable = false, unique = true)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;
}
