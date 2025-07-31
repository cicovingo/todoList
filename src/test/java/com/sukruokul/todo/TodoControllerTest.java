package com.sukruokul.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sukruokul.todo.controller.TodoController;
import com.sukruokul.todo.model.Todo;
import com.sukruokul.todo.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * author: sukru.okul
 */
@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Todo testTodo;

    @BeforeEach
    void setUp() {
        testTodo = new Todo("1", "Learn Spring Boot", false, LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /api/todos - Should return all todos")
    void getAllTodos_ReturnsListOfTodos() throws Exception {
        // Given: Mock the service to return a list of objects
        when(todoService.findAll()).thenReturn(Arrays.asList(testTodo, new Todo("2", "Go shopping", true, LocalDateTime.now())));

        // When & Then: Perform GET request and assert the response
        mockMvc.perform(get("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$[0].title").value("Learn Spring Boot"))
                .andExpect(jsonPath("$[1].completed").value(true))
                .andExpect(jsonPath("$.length()").value(2));

        verify(todoService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/todos/{id} - Should return a todo by ID")
    void getTodoById_ReturnsTodo() throws Exception {
        // Given: Mock the service to return the testTodo
        when(todoService.findById(testTodo.getId())).thenReturn(Optional.of(testTodo));

        // When & Then: Perform GET request and assert the response
        mockMvc.perform(get("/api/todos/{id}", testTodo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.title").value(testTodo.getTitle()))
                .andExpect(jsonPath("$.completed").value(testTodo.isCompleted()));

        verify(todoService, times(1)).findById(testTodo.getId()); // Verify findById was called
    }

    @Test
    @DisplayName("GET /api/todos/{id} - Should return 404 if todo not found")
    void getTodoById_ReturnsNotFound() throws Exception {
        // Given: Mock the service to return an empty Optional
        when(todoService.findById(String.valueOf(anyInt()))).thenReturn(Optional.empty());

        // When & Then: Perform GET request and assert 404 Not Found
        mockMvc.perform(get("/api/todos/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(todoService, times(1)).findById(String.valueOf(999));
    }

    @Test
    @DisplayName("POST /api/todos - Should create a new todo")
    void createTodo_CreatesNewTodo() throws Exception {
        // Given: A new create
        Todo newTodo = new Todo("0", "Write tests", false, LocalDateTime.now());
        Todo savedTodo = new Todo("3", "Write tests", false, LocalDateTime.now());

        // Given: Mock the service to return the saved it
        when(todoService.save(any(Todo.class))).thenReturn(savedTodo);

        // When & Then: Perform POST request and assert the response
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTodo)))
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("Write tests"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(todoService, times(1)).save(any(Todo.class));
    }

    @Test
    @DisplayName("PUT /api/todos/{id} - Should update an existing todo")
    void updateTodo_UpdatesExistingTodo() throws Exception {
        // Given: An existing it and updated data
        Todo existingTodo = new Todo("1", "Old Title", false, LocalDateTime.now());
        Todo updatedData = new Todo("1", "New Title", true, LocalDateTime.now());
        Todo updatedResult = new Todo("1", "New Title", true, LocalDateTime.now());

        // Given: Mock service behavior
        when(todoService.findById(existingTodo.getId())).thenReturn(Optional.of(existingTodo));
        when(todoService.update(any(Todo.class))).thenReturn(updatedResult);

        // When & Then: Perform PUT request and assert the response
        mockMvc.perform(put("/api/todos/{id}", existingTodo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.completed").value(true));

        verify(todoService, times(1)).findById(existingTodo.getId());
        verify(todoService, times(1)).update(any(Todo.class)); // Verify update was called
    }

    @Test
    @DisplayName("PUT /api/todos/{id} - Should return 404 if todo not found for update")
    void updateTodo_ReturnsNotFound() throws Exception {
        // Given: Mock service to return empty for findById
        when(todoService.findById(String.valueOf(anyInt()))).thenReturn(Optional.empty());

        // When & Then: Perform PUT request for non-existent ID
        mockMvc.perform(put("/api/todos/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTodo)))
                .andExpect(status().isNotFound());

        verify(todoService, times(1)).findById(String.valueOf(999));
        verify(todoService, never()).update(any(Todo.class));
    }


    @Test
    @DisplayName("DELETE /api/todos/{id} - Should delete a todo successfully")
    void deleteTodo_DeletesSuccessfully() throws Exception {
        // Given: Mock service to return with findById
        when(todoService.findById(testTodo.getId())).thenReturn(Optional.of(testTodo));
        // Given: Mock service's deleteById to do nothing
        doNothing().when(todoService).deleteById(testTodo.getId());

        // When & Then: Perform DELETE request and assert no content
        mockMvc.perform(delete("/api/todos/{id}", testTodo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(todoService, times(1)).findById(testTodo.getId());
        verify(todoService, times(1)).deleteById(testTodo.getId());
    }

    @Test
    @DisplayName("DELETE /api/todos/{id} - Should return 404 if todo not found for deletion")
    void deleteTodo_ReturnsNotFound() throws Exception {
        // Given: Mock service to return empty for findById
        when(todoService.findById(String.valueOf(anyInt()))).thenReturn(Optional.empty());

        // When & Then: Perform DELETE request for non-existent ID
        mockMvc.perform(delete("/api/todos/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(todoService, times(1)).findById(String.valueOf(999));
        verify(todoService, never()).deleteById(String.valueOf(anyInt()));
    }
}