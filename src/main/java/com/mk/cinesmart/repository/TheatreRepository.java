package com.mk.cinesmart.repository;

import com.mk.cinesmart.model.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {
    // தியேட்டர் அட்மின் லாகினுக்கு ஈமெயில் வைத்து தேட
    Optional<Theatre> findByAdminEmail(String adminEmail);
}