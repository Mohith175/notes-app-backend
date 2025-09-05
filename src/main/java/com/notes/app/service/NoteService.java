package com.notes.app.service;

import com.notes.app.dto.NoteCreateDTO;
import com.notes.app.dto.NoteDTO;
import com.notes.app.dto.NoteUpdateDTO;
import com.notes.app.entity.Note;
import com.notes.app.entity.User;
import com.notes.app.repository.NoteRepo;
import com.notes.app.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepo noteRepository;
    private final UserRepo userRepository;


    public NoteDTO mapToDTO(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setShared(note.isShared());
        dto.setVersion(note.getVersion());
        return dto;
    }


    public List<Note> getNotesByUser(Long userId) {
        return noteRepository.findAllByUserOrCollaborator(userId);
    }

    public Note createNote(Note note, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        note.setUser(user);
        return noteRepository.save(note);
    }

    public Note getNoteById(Long id, Long userId) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Note not found"));

        if (!note.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "Not your note");
        }
        return note;
    }

    @Transactional
    public Note updateNote(Long id, NoteUpdateDTO updatedNote, Long userId) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Note not found"));

        boolean isOwner = note.getUser().getId().equals(userId);
        boolean isCollaborator = note.getCollaborators().stream()
                .anyMatch(u -> u.getId().equals(userId));

        if (!isOwner && !isCollaborator) {
            throw new ResponseStatusException(FORBIDDEN, "Not your note");
        }

        // Optimistic locking check
        if (!note.getVersion().equals(updatedNote.getVersion())) {
            throw new ResponseStatusException(CONFLICT, "Note has been updated by someone else");
        }

        note.setTitle(updatedNote.getTitle());
        note.setContent(updatedNote.getContent());

        return noteRepository.save(note);
    }


    public void deleteNote(Long id, Long userId) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Note not found"));

        if (!note.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "Not your note");
        }
        noteRepository.delete(note);
    }

    public String generateShareLink(Long id, Long userId) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Note not found"));

        if (!note.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "Not your note");
        }

        String shareId = UUID.randomUUID().toString();
        note.setShareId(shareId);
        note.setShared(true);
        noteRepository.save(note);

        return "http://localhost:5173/shared/" + shareId;
    }

    public Note getSharedNote(String shareId) {
        return noteRepository.findByShareId(shareId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Shared note not found"));
    }

    public Note updateSharedNote(String shareId, NoteUpdateDTO updatedNote, Long userId) {
        Note note = noteRepository.findByShareId(shareId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Shared note not found"));

        // Only allow update if owner or shared user has access
        if (!note.getUser().getId().equals(userId) && !note.isShared()) {
            throw new ResponseStatusException(FORBIDDEN, "Cannot edit this note");
        }

        // Optimistic locking check
        if (!note.getVersion().equals(updatedNote.getVersion())) {
            throw new ResponseStatusException(CONFLICT, "Note has been updated by someone else");
        }

        note.setTitle(updatedNote.getTitle());
        note.setContent(updatedNote.getContent());

        return noteRepository.save(note);
    }

    @Transactional
    public Note addCollaborator(String shareId, String username) {
        Note note = noteRepository.findByShareId(shareId)
                .orElseThrow(() -> new RuntimeException("Shared note not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!note.getCollaborators().contains(user)) {
            note.getCollaborators().add(user);
            noteRepository.save(note);
        }

        return note;
    }
}
