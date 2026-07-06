package com.lamdayne.todo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
public class TodoResponse implements Serializable {
    private Long id;
    private String title;
    private Boolean completed;
    private Instant createdAt;
    private Instant updatedAt;
}
