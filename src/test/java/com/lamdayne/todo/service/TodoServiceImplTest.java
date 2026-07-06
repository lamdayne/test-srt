package com.lamdayne.todo.service;

import com.lamdayne.todo.dto.request.CreateTodoRequest;
import com.lamdayne.todo.dto.request.UpdateTodoRequest;
import com.lamdayne.todo.dto.response.PageResponse;
import com.lamdayne.todo.dto.response.TodoResponse;
import com.lamdayne.todo.dto.response.TodoStatsResponse;
import com.lamdayne.todo.entity.Todo;
import com.lamdayne.todo.exception.AppException;
import com.lamdayne.todo.exception.ErrorCode;
import com.lamdayne.todo.repository.TodoRepository;
import com.lamdayne.todo.service.impl.TodoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    private Todo todo;

    @BeforeEach
    void setUp() {
        todo = Todo.builder()
                .title("Test Task")
                .completed(false)
                .build();
        todo.setId(1L);
        todo.setCreatedAt(Instant.now());
        todo.setUpdatedAt(Instant.now());
    }

    @Test
    void createTodo_success() {
        CreateTodoRequest request = CreateTodoRequest.builder()
                .title("Test Task")
                .build();

        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        TodoResponse response = todoService.createTodo(request);

        assertNotNull(response);
        assertEquals(todo.getId(), response.getId());
        assertEquals(todo.getTitle(), response.getTitle());
        assertFalse(response.getCompleted());
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void getTodoById_success() {
        when(todoRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(todo));

        TodoResponse response = todoService.getTodoById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Task", response.getTitle());
    }

    @Test
    void getTodoById_notFound() {
        when(todoRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> todoService.getTodoById(1L));

        assertEquals(ErrorCode.TODO_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getTodos_success() {
        Page<Todo> page = new PageImpl<>(List.of(todo));
        when(todoRepository.findTodos(any(), any(), any(Pageable.class))).thenReturn(page);

        PageResponse<TodoResponse> response = todoService.getTodos(0, 10, null, null);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(1L, response.getItems().get(0).getId());
    }

    @Test
    void updateTodo_success() {
        UpdateTodoRequest request = UpdateTodoRequest.builder()
                .title("Updated Task")
                .completed(true)
                .build();

        when(todoRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TodoResponse response = todoService.updateTodo(1L, request);

        assertNotNull(response);
        assertEquals("Updated Task", response.getTitle());
        assertTrue(response.getCompleted());
    }

    @Test
    void getTodoStats_success() {
        when(todoRepository.countByDeletedAtIsNull()).thenReturn(10L);
        when(todoRepository.countByCompletedTrueAndDeletedAtIsNull()).thenReturn(4L);

        TodoStatsResponse stats = todoService.getTodoStats();

        assertNotNull(stats);
        assertEquals(10L, stats.getTotal());
        assertEquals(4L, stats.getCompleted());
        assertEquals(6L, stats.getActive());
        verify(todoRepository, times(1)).countByDeletedAtIsNull();
        verify(todoRepository, times(1)).countByCompletedTrueAndDeletedAtIsNull();
    }

    @Test
    void deleteTodo_success() {
        when(todoRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        assertDoesNotThrow(() -> todoService.deleteTodoById(1L));

        assertNotNull(todo.getDeletedAt());
        verify(todoRepository, times(1)).save(todo);
    }
}
