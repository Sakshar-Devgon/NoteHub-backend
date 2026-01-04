package com.Beast.notes.notes_app.controller;

import com.Beast.notes.notes_app.dto.TimeCapsuleDto;
import com.Beast.notes.notes_app.model.Note;
import com.Beast.notes.notes_app.model.TimeCapsule;
import com.Beast.notes.notes_app.repository.NoteRepository;
import com.Beast.notes.notes_app.repository.TimeCapsuleRepository;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/time-capsules")
@CrossOrigin(origins = "*")
public class TimeCapsuleController {

    private final TimeCapsuleRepository capsuleRepo;
    private final NoteRepository noteRepo;

    public TimeCapsuleController(TimeCapsuleRepository capsuleRepo,
                                 NoteRepository noteRepo) {
        this.capsuleRepo = capsuleRepo;
        this.noteRepo = noteRepo;
    }

    @PostMapping("/{noteId}")
    public String createCapsule(@PathVariable Long noteId, @RequestBody TimeCapsuleDto dto) {
        Note note = noteRepo.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // Mark as time capsule note
        note.setTimeCapsule(true);
        noteRepo.save(note);

        TimeCapsule capsule = new TimeCapsule();
        capsule.setNote(note);
        capsule.setShareToken(UUID.randomUUID().toString());
        capsule.setTimeSpan(dto.getTimeSpan());

        LocalDateTime unlockTime;
        if ("CUSTOM".equals(dto.getTimeSpan()) && dto.getCustomUnlockTime() != null) {
            // Convert from UTC to server timezone
            ZonedDateTime utcTime = dto.getCustomUnlockTime().atZone(ZoneId.of("UTC"));
            unlockTime = utcTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        } else {
            unlockTime = calculateUnlockTime(dto.getTimeSpan());
        }
        capsule.setUnlockAt(unlockTime);

        capsuleRepo.save(capsule);
        return "https://notepad.app/capsule/" + capsule.getShareToken();
    }

    @GetMapping
    public List<TimeCapsule> getAllCapsules() {
        List<TimeCapsule> capsules = capsuleRepo.findAll();
        capsules.forEach(capsule -> {
            if (capsule.getNote() != null) {
                capsule.getNote().getTitle();
            }
        });
        return capsules;
    }

    @DeleteMapping("/{id}")
    public void deleteCapsule(@PathVariable Long id) {
        capsuleRepo.deleteById(id);
    }

    @PostMapping("/{token}/claim")
    public String claimCapsule(@PathVariable String token, HttpServletRequest request) {
        String appKey = request.getHeader("X-APP-KEY");
        if (!"notepad-mobile-v1".equals(appKey)) {
            throw new RuntimeException("App only access");
        }

        TimeCapsule capsule = capsuleRepo.findByShareToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (capsule.isClaimed()) {
            throw new RuntimeException("Already claimed");
        }

        capsule.setClaimed(true);
        capsuleRepo.save(capsule);

        return "Capsule claimed successfully. It will unlock on " + capsule.getUnlockAt();
    }

    @GetMapping("/{token}")
    public Note readCapsule(@PathVariable String token, HttpServletRequest request) {
        String appKey = request.getHeader("X-APP-KEY");
        if (!"notepad-mobile-v1".equals(appKey)) {
            throw new RuntimeException("App only access");
        }

        TimeCapsule capsule = capsuleRepo.findByShareToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (!capsule.isClaimed()) {
            throw new RuntimeException("Capsule not claimed yet");
        }

        if (LocalDateTime.now().isBefore(capsule.getUnlockAt())) {
            throw new RuntimeException("This time capsule is still locked");
        }

        return capsule.getNote();
    }

    private LocalDateTime calculateUnlockTime(String timeSpan) {
        LocalDateTime now = LocalDateTime.now();
        switch (timeSpan) {
            case "12_HOURS": return now.plusHours(12);
            case "1_DAY": return now.plusDays(1);
            case "3_DAYS": return now.plusDays(3);
            case "7_DAYS": return now.plusDays(7);
            default: return now.plusDays(1);
        }
    }
}
