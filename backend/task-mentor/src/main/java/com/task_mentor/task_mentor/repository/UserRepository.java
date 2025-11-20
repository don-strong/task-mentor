package com.task_mentor.task_mentor.repository;


import com.task_mentor.task_mentor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository - Spring Data JPA repository for User entity
 * Provides CRUD operations and custom query methods for users table
 *
 * @author James No
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by email address
     * Used for login and checking if email already exists during registration
     */
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long userId);

    /**
     * Find all users by account type (student or mentor)
     * Useful for admin dashboards or analytics
     */
    List<User> findByAccountType(String accountType);

    /**
     * Check if email already exists in database
     * Returns true if email exists, false otherwise
     */
    boolean existsByEmail(String email);


}