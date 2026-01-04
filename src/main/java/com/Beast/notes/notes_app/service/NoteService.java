package com.Beast.notes.notes_app.service;
import com.Beast.notes.notes_app.dto.NoteDto;
import com.Beast.notes.notes_app.model.Note;
import com.Beast.notes.notes_app.model.User;
import com.Beast.notes.notes_app.repository.NoteRepository;
import com.Beast.notes.notes_app.repository.UserRepository;
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

    private User getOrCreateUser(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setFirebaseUid(firebaseUid);
                    return userRepository.save(newUser);
                });
    }

    public Note createNote(NoteDto dto, String firebaseUid) {
        User user = getOrCreateUser(firebaseUid);
        Note note = new Note();
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setTimeCapsule(false);
        note.setUser(user);
        return noteRepository.save(note);
    }

    public List<Note> getRegularNotes(String firebaseUid) {
        User user = getOrCreateUser(firebaseUid);
        return noteRepository.findByIsTimeCapsuleFalseAndUserId(user.getId());
    }

    public Optional<Note> getNoteById(Long id, String firebaseUid) {
        User user = getOrCreateUser(firebaseUid);
        return noteRepository.findByIdAndUserId(id, user.getId());
    }

    public Note updateNote(Long id, NoteDto dto, String firebaseUid) {
        User user = getOrCreateUser(firebaseUid);
        return noteRepository.findByIdAndUserId(id, user.getId())
                .map(existingNote -> {
                    existingNote.setTitle(dto.getTitle());
                    existingNote.setContent(dto.getContent());
                    return noteRepository.save(existingNote);
                })
                .orElse(null);
    }

    public void deleteNote(Long id, String firebaseUid) {
        User user = getOrCreateUser(firebaseUid);
        noteRepository.findByIdAndUserId(id, user.getId())
                .ifPresent(note -> noteRepository.deleteById(id));
    }
}
