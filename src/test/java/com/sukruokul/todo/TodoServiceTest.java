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
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        // Given: Mock repository behavior
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);
        when(todoMongoRepository.save(any(Todo.class))).thenReturn(testTodo);

        // When: Service method is called
        Todo savedTodo = todoService.save(testTodo);

        // Then: Verify interactions and result
        assertNotNull(savedTodo);
        assertEquals(testTodo.getTitle(), savedTodo.getTitle());
        // Verify that save was called on both repositories
        verify(todoRepository, times(1)).save(any(Todo.class));
        verify(todoMongoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when SQL save fails")
    void save_SqlFailure() {
        // Given: Mock SQL repository to throw an exception
        when(todoRepository.save(any(Todo.class))).thenThrow(new DataAccessException("SQL DB error") {});

        // When & Then: Expect RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> todoService.save(testTodo));
        assertTrue(exception.getMessage().contains("Failed to save todo due to database error."));
        // Verify that MongoDB save was NOT called
        verify(todoMongoRepository, never()).save(any(Todo.class));
    }

    @Test
    @DisplayName("Should delete a todo by ID successfully from both databases")
    void deleteById_Success() {
        // Given: FindById returns it, and deleteById doesn't throw exception
        when(todoRepository.findById(testTodo.getId())).thenReturn(Optional.of(testTodo));
        doNothing().when(todoRepository).deleteById(testTodo.getId());
        doNothing().when(todoMongoRepository).deleteById(String.valueOf(testTodo.getId()));

        // When: Service method is called
        todoService.deleteById(testTodo.getId());

        // Then: Verify delete was called on both repositories
        verify(todoRepository, times(1)).deleteById(testTodo.getId());
        verify(todoMongoRepository, times(1)).deleteById(String.valueOf(testTodo.getId()));
    }

    @Test
    @DisplayName("Should throw RuntimeException when SQL delete fails")
    void deleteById_SqlFailure() {
        // Given: Mock SQL repository to throw an exception
        when(todoRepository.findById(testTodo.getId())).thenReturn(Optional.of(testTodo));
        doThrow(new DataAccessException("SQL DB error") {}).when(todoRepository).deleteById(testTodo.getId());

        // When & Then: Expect RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> todoService.deleteById(testTodo.getId()));
        assertTrue(exception.getMessage().contains("Failed to delete todo due to database error."));
        // Verify that MongoDB delete was NOT called
        verify(todoMongoRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Should update a todo successfully in both databases")
    void update_Success() {
        // Given: Mock repository behavior
        testTodo.setCompleted(true);
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);
        when(todoMongoRepository.save(any(Todo.class))).thenReturn(testTodo);

        // When: Service method is called
        Todo updatedTodo = todoService.update(testTodo);

        // Then: Verify interactions and result
        assertNotNull(updatedTodo);
        assertTrue(updatedTodo.isCompleted());
        verify(todoRepository, times(1)).save(any(Todo.class));
        verify(todoMongoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    @DisplayName("Should retrieve all todos successfully from SQL database")
    void findAll_Success() {
        // Given: Mock SQL repository to return a list of them
        List<Todo> todos = Arrays.asList(testTodo, new Todo("2", "Another Todo", true, LocalDateTime.now()));
        when(todoRepository.findAll()).thenReturn(todos);

        // When: Service method is called
        List<Todo> foundTodos = todoService.findAll();

        // Then: Verify results
        assertNotNull(foundTodos);
        assertEquals(2, foundTodos.size());
        verify(todoRepository, times(1)).findAll();
        // Verify MongoDB findAll was NOT called (as per current implementation logic)
        verify(todoMongoRepository, never()).findAll();
    }

    @Test
    @DisplayName("Should find a todo by ID successfully from SQL database")
    void findById_Success() {
        // Given: Mock SQL repository to return an Optional with it
        when(todoRepository.findById(testTodo.getId())).thenReturn(Optional.of(testTodo));

        // When: Service method is called
        Optional<Todo> foundTodo = todoService.findById(testTodo.getId());

        // Then: Verify result
        assertTrue(foundTodo.isPresent());
        assertEquals(testTodo.getId(), foundTodo.get().getId());
        verify(todoRepository, times(1)).findById(testTodo.getId());
    }

    @Test
    @DisplayName("Should return empty optional when todo not found by ID")
    void findById_NotFound() {
        // Given: Mock SQL repository to return an empty Optional
        when(todoRepository.findById(String.valueOf(anyInt()))).thenReturn(Optional.empty());

        // When: Service method is called
        Optional<Todo> foundTodo = todoService.findById(String.valueOf(99)); // Non-existent ID

        // Then: Verify result is empty
        assertFalse(foundTodo.isPresent());
        verify(todoRepository, times(1)).findById(String.valueOf(99));
    }
}