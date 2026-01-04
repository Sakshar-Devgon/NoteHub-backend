package com.Beast.notes.notes_app.controller;

import com.Beast.notes.notes_app.dto.NoteDto;
import com.Beast.notes.notes_app.model.Note;
import com.Beast.notes.notes_app.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody NoteDto dto, @RequestHeader("X-User-ID") String firebaseUid) {
        return ResponseEntity.ok(noteService.createNote(dto, firebaseUid));
    }

    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes(@RequestHeader("X-User-ID") String firebaseUid) {
        return ResponseEntity.ok(noteService.getRegularNotes(firebaseUid));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id, @RequestHeader("X-User-ID") String firebaseUid) {
        return noteService.getNoteById(id, firebaseUid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody NoteDto dto, @RequestHeader("X-User-ID") String firebaseUid) {
        Note updated = noteService.updateNote(id, dto, firebaseUid);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id, @RequestHeader("X-User-ID") String firebaseUid) {
        noteService.deleteNote(id, firebaseUid);
        return ResponseEntity.noContent().build();
    }
}
