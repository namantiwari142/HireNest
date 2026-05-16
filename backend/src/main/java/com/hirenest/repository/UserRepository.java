package com.hirenest.repository;

import com.hirenest.entity.Role;
import com.hirenest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByOauthIdAndProvider(String oauthId, com.hirenest.entity.AuthProvider provider);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id <> :currentUserId AND u.role = :role AND " +
           "(LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<User> searchByNameOrEmail(@Param("query") String query, @Param("role") Role role, @Param("currentUserId") Long currentUserId);
}
