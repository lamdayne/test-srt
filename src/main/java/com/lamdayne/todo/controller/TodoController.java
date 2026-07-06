package com.lamdayne.todo.controller;

import com.lamdayne.todo.dto.request.CreateTodoRequest;
import com.lamdayne.todo.dto.request.UpdateTodoRequest;
import com.lamdayne.todo.dto.response.ApiResponse;
import com.lamdayne.todo.dto.response.PageResponse;
import com.lamdayne.todo.dto.response.TodoResponse;
import com.lamdayne.todo.dto.response.TodoStatsResponse;
import com.lamdayne.todo.enums.SuccessCode;
import com.lamdayne.todo.service.TodoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<ApiResponse<TodoResponse>> createTodo(
            @RequestBody @Valid CreateTodoRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        SuccessCode.TASK_CREATE_SUCCESS,
                        todoService.createTodo(request)
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> getTodoById(
            @PathVariable(name = "id") Long id
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(
                        SuccessCode.TASK_READ_SUCCESS,
                        "Get task by id successfully",
                        todoService.getTodoById(id)
                ));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<TodoStatsResponse>> getTodoStats() {
        return ResponseEntity.ok()
                .body(ApiResponse.success(
                        SuccessCode.TASK_READ_SUCCESS,
                        "Get task stats successfully",
                        todoService.getTodoStats()
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> updateTodo(
            @PathVariable(name = "id") Long id,
            @RequestBody @Valid UpdateTodoRequest request
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(
                        SuccessCode.TASK_UPDATE_SUCCESS,
                        todoService.updateTodo(id, request)
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTodoById(
            @PathVariable(name = "id") Long id
    ) {
        todoService.deleteTodoById(id);
        return ResponseEntity.ok().body(ApiResponse.success(SuccessCode.TASK_DELETE_SUCCESS));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TodoResponse>>> getTodos(
            @RequestParam(defaultValue = "0", required = false) @Min(value = 0, message = "PAGE_NO_INVALID") int page,
            @RequestParam(defaultValue = "10", required = false) @Min(value = 10, message = "PAGE_SIZE_INVALID") int size,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String... sorts
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(
                        SuccessCode.TASK_READ_SUCCESS,
                        "Get all task successfully",
                        todoService.getTodos(page, size, completed, search, sorts)
                ));
    }

}
