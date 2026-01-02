package com.Beast.notes.notes_app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "time_capsules")
public class TimeCapsule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String shareToken;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    private boolean claimed = false;

    @Column(nullable = false)
    private LocalDateTime unlockAt;

    @Column(nullable = false)
    private String timeSpan;

    private LocalDateTime createdAt = LocalDateTime.now();
}
