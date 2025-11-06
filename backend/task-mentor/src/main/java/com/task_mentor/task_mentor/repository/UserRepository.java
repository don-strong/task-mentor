package com.task_mentor.task_mentor.repository;

import com.task_mentor.task_mentor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    // Spring Data JPA automatically implements this method to find a user by username
    Optional<User> findByUsername(String username);

    // Spring Data JPA automatically implements this method to check existence by username
    boolean existsByUsername(String username);

    // Spring Data JPA automatically implements this method to check existence by email
    boolean existsByEmail(String email);
}
