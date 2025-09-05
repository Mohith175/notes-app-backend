package com.notes.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteDTO {
    private Long id;
    private String title;
    private String content;
    private boolean shared;
    private Long version;
    private String shareId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
