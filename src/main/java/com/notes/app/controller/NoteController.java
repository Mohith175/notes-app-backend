package com.notes.app.controller;

import com.notes.app.dto.NoteCreateDTO;
import com.notes.app.dto.NoteDTO;
import com.notes.app.dto.NoteUpdateDTO;
import com.notes.app.entity.Note;
import com.notes.app.entity.UserPrincipal;
import com.notes.app.service.MapperService;
import com.notes.app.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;
    private final MapperService mapperService;

    @GetMapping
    public List<NoteDTO> getUserNotes(@AuthenticationPrincipal UserPrincipal user) {
        return noteService.getNotesByUser(user.getId())
                .stream()
                .map(mapperService::toNoteDTO)
                .toList();
    }

    @PostMapping
    public ResponseEntity<NoteDTO> createNote(@RequestBody Note note,
                                              @AuthenticationPrincipal UserPrincipal user) {
        Note created = noteService.createNote(note, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapperService.toNoteDTO(created));
    }

    @GetMapping("/{id}")
    public NoteDTO getNote(@PathVariable Long id,
                           @AuthenticationPrincipal UserPrincipal user) {
        Note note = noteService.getNoteById(id, user.getId());
        return mapperService.toNoteDTO(note);
    }

    @PutMapping("/{id}")
    public NoteDTO updateNote(@PathVariable Long id,
                              @RequestBody NoteUpdateDTO dto,
                              @AuthenticationPrincipal UserPrincipal user) {
        Note updated = noteService.updateNote(id, dto, user.getId());
        return mapperService.toNoteDTO(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id,
                                           @AuthenticationPrincipal UserPrincipal user) {
        noteService.deleteNote(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/share")
    public ResponseEntity<String> shareNote(@PathVariable Long id,
                                            @AuthenticationPrincipal UserPrincipal user) {
        String shareLink = noteService.generateShareLink(id, user.getId());
        return ResponseEntity.ok(shareLink);
    }

    @GetMapping("/shared/{shareId}")
    public NoteDTO viewSharedNote(@PathVariable String shareId) {
        return noteService.mapToDTO(noteService.getSharedNote(shareId));
    }

    @PutMapping("/shared/{shareId}")
    public NoteDTO updateSharedNote(@PathVariable String shareId,
                                    @RequestBody NoteUpdateDTO dto,
                                    @AuthenticationPrincipal UserPrincipal user) {
        Note updated = noteService.updateSharedNote(shareId, dto, user.getId());
        return mapperService.toNoteDTO(updated);
    }
}
