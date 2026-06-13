package com.mk.cinesmart.repository;

import com.mk.cinesmart.model.User;
import com.mk.cinesmart.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    boolean existsByEmail(String email);
    // புதிய மாற்றம்: தியேட்டர் அட்மின்களை தியேட்டர் வாரியாக பிரிக்க
    List<User> findByRoleAndTheatreName(UserRole role, String theatreName);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.theatre WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailWithTheatre(@Param("email") String email);

    boolean existsByRole(UserRole role);
}