package com.Beast.notes.notes_app.service;
import com.Beast.notes.notes_app.dto.NoteDto;
import com.Beast.notes.notes_app.model.Note;
import com.Beast.notes.notes_app.model.User;
import com.Beast.notes.notes_app.repository.NoteRepository;
import com.Beast.notes.notes_app.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Note createNote(NoteDto dto) {
        User currentUser = getCurrentUser();
        Note note = new Note();
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setUser(currentUser);
        return noteRepository.save(note);
    }

    public List<Note> getRegularNotes() {
        User currentUser = getCurrentUser();
        return noteRepository.findByIsTimeCapsuleFalseAndUserId(currentUser.getId());
    }

    public Optional<Note> getNoteById(Long id) {
        User currentUser = getCurrentUser();
        return noteRepository.findById(id)
                .filter(note -> note.getUser().getId().equals(currentUser.getId()));
    }

    public Note updateNote(Long id, NoteDto dto) {
        User currentUser = getCurrentUser();
        return noteRepository.findById(id)
                .filter(note -> note.getUser().getId().equals(currentUser.getId()))
                .map(existingNote -> {
                    existingNote.setTitle(dto.getTitle());
                    existingNote.setContent(dto.getContent());
                    return noteRepository.save(existingNote);
                })
                .orElse(null);
    }

    public void deleteNote(Long id) {
        User currentUser = getCurrentUser();
        noteRepository.findById(id)
                .filter(note -> note.getUser().getId().equals(currentUser.getId()))
                .ifPresent(note -> noteRepository.deleteById(id));
    }
}
