package com.notes.app.service;

import com.notes.app.dto.NoteDTO;
import com.notes.app.dto.UserDTO;
import com.notes.app.entity.Note;
import com.notes.app.entity.User;
import org.springframework.stereotype.Service;

@Service
public class MapperService {

    // ===== User Mapping =====
    public UserDTO toUserDTO(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }

    // ===== Note Mapping =====
    public NoteDTO toNoteDTO(Note note) {
        if (note == null) return null;

        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setVersion(note.getVersion());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        dto.setShared(note.isShared());
        dto.setShareId(note.getShareId());
        return dto;
    }
}
