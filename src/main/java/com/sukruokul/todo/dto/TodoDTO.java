package com.sukruokul.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * author: sukru.okul
 */
@Data
public class TodoDTO {
    private String id;
    @NotBlank(message = "Title cannot be empty")
    private String title;
    @NotNull(message = "Completed status cannot be null")
    private boolean completed;
    private LocalDateTime createdAt;

    public TodoDTO(String id, String title, @NotNull(message = "Completed status cannot be null") boolean completed, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.createdAt = createdAt;
    }
}