package com.notes.app.repository;

import com.notes.app.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoteRepo extends JpaRepository<Note, Long> {
    // Fetch all notes of a user
    List<Note> findByUserId(Long userId);

    // Fetch a shared note by shareId
    Optional<Note> findByShareId(String shareId);

    @Query("SELECT n FROM Note n LEFT JOIN n.collaborators c WHERE n.user.id = :userId OR c.id = :userId")
    List<Note> findAllByUserOrCollaborator(@Param("userId") Long userId);
}