package com.lamdayne.todo.service.impl;

import com.lamdayne.todo.dto.request.CreateTodoRequest;
import com.lamdayne.todo.dto.request.UpdateTodoRequest;
import com.lamdayne.todo.dto.response.PageResponse;
import com.lamdayne.todo.dto.response.TodoResponse;
import com.lamdayne.todo.dto.response.TodoStatsResponse;
import com.lamdayne.todo.entity.Todo;
import com.lamdayne.todo.exception.AppException;
import com.lamdayne.todo.exception.ErrorCode;
import com.lamdayne.todo.repository.TodoRepository;
import com.lamdayne.todo.service.TodoService;
import com.lamdayne.todo.util.PageableUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    public TodoResponse createTodo(CreateTodoRequest request) {
        Todo todo = Todo.builder()
                .title(request.getTitle())
                .build();

        return todoResponse(todoRepository.save(todo));
    }

    @Override
    public TodoResponse getTodoById(Long id) {
        Todo todo = todoRepository.findByIdAndDeletedAtIsNull((id))
                .orElseThrow(() -> new AppException(ErrorCode.TODO_NOT_FOUND));
        return todoResponse(todo);
    }

    @Override
    public PageResponse<TodoResponse> getTodos(int page, int size, Boolean completed, String search, String... sorts) {
        Pageable pageable = PageableUtil.buildPageable(page, size, sorts);

        Page<Todo> todoPage = todoRepository.findTodos(completed, search, pageable);

        List<TodoResponse> todoResponses = todoPage.stream()
                .map(this::todoResponse)
                .toList();

        return PageResponse.<TodoResponse>builder()
                .pageNo(page)
                .pageSize(size)
                .totalElements(todoPage.getTotalElements())
                .totalPages(todoPage.getTotalPages())
                .items(todoResponses)
                .build();
    }

    @Override
    public TodoResponse updateTodo(Long id, UpdateTodoRequest request) {
        Todo todo = todoRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.TODO_NOT_FOUND));
        todo.setTitle(request.getTitle());
        if (request.getCompleted() != null) {
            todo.setCompleted(request.getCompleted());
        }
        return todoResponse(todoRepository.save(todo));
    }

    @Override
    public TodoStatsResponse getTodoStats() {
        long total = todoRepository.countByDeletedAtIsNull();
        long completed = todoRepository.countByCompletedTrueAndDeletedAtIsNull();
        long active = total - completed;
        return TodoStatsResponse.builder()
                .total(total)
                .completed(completed)
                .active(active)
                .build();
    }

    @Override
    public void deleteTodoById(Long id) {
        Todo todo = todoRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.TODO_NOT_FOUND));
        todo.setDeletedAt(Instant.now());
        todoRepository.save(todo);
    }

    private TodoResponse todoResponse(Todo todo) {
        return TodoResponse.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .completed(todo.getCompleted())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt())
                .build();
    }

}
