package com.sukruokul.todo.controller;

import com.sukruokul.todo.dto.TodoDTO;
import com.sukruokul.todo.model.Todo;
import com.sukruokul.todo.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

/**
 * author: sukru.okul
 */
@Tag(name = "TodoController", description = "REST APIs for managing Todo items")
@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    private final TodoService todoService;

    @Operation(summary = "Get all Todo items", description = "Retrieves a comprehensive list of all Todo items in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of todos"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/getTodos")
    public ResponseEntity<List<Todo>> getAllTodos() {
        logger.info("Received request to get all todos.");
        try {
            List<Todo> todoList = todoService.findAll();
            logger.debug("Returning {} todos.", todoList.size());
            return ResponseEntity.ok(todoList);
        } catch (Exception e) {
            logger.error("Error retrieving all todos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Create a new Todo item", description = "Adds a new Todo item to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Todo item created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Todo input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/createTodo")
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody TodoDTO todoDTO) {
        logger.info("Received request to create new todo: {}", todoDTO);
        try {
            Todo newTodo = new Todo();
            newTodo.setTitle(todoDTO.getTitle());
            newTodo.setCompleted(todoDTO.isCompleted());

            Todo savedTodo = todoService.save(newTodo);
            logger.info("Todo created successfully: {}", savedTodo);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTodo);
        } catch (Exception e) {
            logger.error("Error creating todo {}: {}", todoDTO, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get a Todo item by ID", description = "Retrieves a single Todo item based on its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todo item found"),
            @ApiResponse(responseCode = "404", description = "Todo item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/getTodo/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable("id") String id) {
        logger.info("Received request to get todo with ID: {}", id);
        try {
            Optional<Todo> todo = todoService.findById(id);
            if (todo.isPresent()) {
                logger.debug("Todo with ID {} found.", id);
                return ResponseEntity.ok(todo.get());
            } else {
                logger.warn("Todo with ID {} not found.", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error retrieving todo with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Update an existing Todo item", description = "Updates the details of an existing Todo item identified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todo item updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Todo input"),
            @ApiResponse(responseCode = "404", description = "Todo item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/updateTodo/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable("id") String id,
                                           @Valid @RequestBody TodoDTO todoDTO) {
        logger.info("Received request to update todo with ID {}. New data: {}", id, todoDTO);
        try {
            Optional<Todo> todoData = todoService.findById(id);
            if (todoData.isPresent()) {
                Todo existingTodo = todoData.get();
                existingTodo.setTitle(todoDTO.getTitle());
                existingTodo.setCompleted(todoDTO.isCompleted());

                Todo updatedTodo = todoService.update(existingTodo);
                logger.info("Todo with ID {} updated successfully: {}", id, updatedTodo);
                return ResponseEntity.ok(updatedTodo);
            } else {
                logger.warn("Todo with ID {} not found for update.", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error updating todo with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Delete a Todo item by ID", description = "Removes a Todo item from the system based on its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Todo item deleted successfully (No Content)"),
            @ApiResponse(responseCode = "404", description = "Todo item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/deleteTodo/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable("id") String id) {
        logger.info("Received request to delete todo with ID: {}", id);
        try {
            Optional<Todo> todo = todoService.findById(id);
            if (todo.isPresent()) {
                todoService.deleteById(id);
                logger.info("Todo with ID {} deleted successfully.", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Todo with ID {} not found for deletion.", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting todo with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}