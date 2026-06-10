package com.mk.cinesmart.repository;


import com.mk.cinesmart.model.User;
import com.mk.cinesmart.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Security Authentication-க்காக Email வச்சு யூசரை தேட
    Optional<User> findByEmail(String email);

    // Admin Dashboard-ல் எத்தனை யூசர்ஸ், எத்தனை அட்மின்ஸ் இருக்காங்கன்னு பிரிக்க
    List<User> findByRole(UserRole role);

    // ஈமெயில் ஏற்கனவே ரிஜிஸ்டர் ஆகியிருக்கானு செக் பண்ண
    boolean existsByEmail(String email);
}
