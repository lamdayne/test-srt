package com.lamdayne.todo.service;

import com.lamdayne.todo.dto.request.CreateTodoRequest;
import com.lamdayne.todo.dto.request.UpdateTodoRequest;
import com.lamdayne.todo.dto.response.PageResponse;
import com.lamdayne.todo.dto.response.TodoResponse;
import com.lamdayne.todo.dto.response.TodoStatsResponse;

public interface TodoService {

    TodoResponse createTodo(CreateTodoRequest request);

    TodoResponse getTodoById(Long id);

    PageResponse<TodoResponse> getTodos(int page, int size, Boolean completed, String search, String... sorts);

    TodoResponse updateTodo(Long id, UpdateTodoRequest request);

    TodoStatsResponse getTodoStats();

    void deleteTodoById(Long id);

}
