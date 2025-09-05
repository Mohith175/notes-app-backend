package com.notes.app.dto;

import lombok.Data;

@Data
public class NoteUpdateDTO {
    private String title;
    private String content;
    private Long version;  // required for optimistic locking
}
