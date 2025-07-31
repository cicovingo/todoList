package com.sukruokul.todo;

import com.sukruokul.todo.model.Todo;
import com.sukruokul.todo.repository.mongo.TodoMongoRepository;
import com.sukruokul.todo.repository.jpa.TodoRepository;
import com.sukruokul.todo.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString; // anyString'i import edin
import static org.mockito.Mockito.*;

/**
 * author: sukru.okul
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private TodoMongoRepository todoMongoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo testTodo;

    @BeforeEach
    void setUp() {
        testTodo = new Todo("1", "Test Todo", false, LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save a todo successfully to both databases")
    void save_Success() {
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);
        when(todoMongoRepository.save(any(Todo.class))).thenReturn(testTodo);

        Todo savedTodo = todoService.save(testTodo);

        assertNotNull(savedTodo);
        assertEquals(testTodo.getTitle(), savedTodo.getTitle());
        verify(todoRepository, times(1)).save(any(Todo.class));
        verify(todoMongoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    @DisplayName("Should update a todo successfully in both databases")
    void update_Success() {
        testTodo.setCompleted(true);
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);
        when(todoMongoRepository.save(any(Todo.class))).thenReturn(testTodo);

        Todo updatedTodo = todoService.update(testTodo);

        assertNotNull(updatedTodo);
        assertTrue(updatedTodo.isCompleted());
        verify(todoRepository, times(1)).save(any(Todo.class));
        verify(todoMongoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    @DisplayName("Should retrieve all todos successfully from SQL database")
    void findAll_Success() {
        List<Todo> todos = Arrays.asList(testTodo, new Todo("2", "Another Todo", true, LocalDateTime.now()));
        when(todoRepository.findAll()).thenReturn(todos);

        List<Todo> foundTodos = todoService.findAll();

        // Then: Verify results
        assertNotNull(foundTodos);
        assertEquals(2, foundTodos.size());
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find a todo by ID successfully from SQL database")
    void findById_Success() {
        when(todoRepository.findById(testTodo.getId())).thenReturn(Optional.of(testTodo));

        Optional<Todo> foundTodo = todoService.findById(testTodo.getId());

        assertTrue(foundTodo.isPresent());
        assertEquals(testTodo.getId(), foundTodo.get().getId());
        verify(todoRepository, times(1)).findById(testTodo.getId());
    }

    @Test
    @DisplayName("Should return empty optional when todo not found by ID")
    void findById_NotFound() {
        when(todoRepository.findById(anyString())).thenReturn(Optional.empty());

        Optional<Todo> foundTodo = todoService.findById("nonExistentId");

        assertFalse(foundTodo.isPresent());
        verify(todoRepository, times(1)).findById(anyString());
    }
}