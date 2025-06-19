package com.example.spring_jwt.repository;

import com.example.spring_jwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
// https://chatgpt.com/c/6853008b-85a8-800a-b898-e0f6bb489b25?model=gpt-4o
