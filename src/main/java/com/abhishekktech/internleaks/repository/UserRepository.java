package com.abhishekktech.internleaks.repository;

import com.abhishekktech.internleaks.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Ye function login ke waqt email se user ko dhoondhega
    Optional<User> findByEmail(String email);
}