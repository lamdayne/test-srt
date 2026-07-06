package com.lamdayne.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateTodoRequest {

    @NotBlank(message = "TODO_TITLE_REQUIRED")
    private String title;

}
