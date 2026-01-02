package com.Beast.notes.notes_app.service;
import com.Beast.notes.notes_app.dto.NoteDto;
import com.Beast.notes.notes_app.model.Note;
import com.Beast.notes.notes_app.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note createNote(NoteDto dto) {
        Note note = new Note();
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        return noteRepository.save(note);
    }

    public List<Note> getRegularNotes() {
        return noteRepository.findByIsTimeCapsuleFalse();
    }

    public Optional<Note> getNoteById(Long id) {
        return noteRepository.findById(id);
    }

    public Note updateNote(Long id, NoteDto dto) {
        return noteRepository.findById(id)
                .map(existingNote -> {
                    existingNote.setTitle(dto.getTitle());
                    existingNote.setContent(dto.getContent());
                    return noteRepository.save(existingNote);
                })
                .orElse(null);
    }

    public void deleteNote(Long id) {
        noteRepository.deleteById(id);
    }
}
