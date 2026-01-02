package com.Beast.notes.notes_app.repository;

import com.Beast.notes.notes_app.model.TimeCapsule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimeCapsuleRepository extends JpaRepository<TimeCapsule, Long> {
    Optional<TimeCapsule> findByShareToken(String shareToken);
}
