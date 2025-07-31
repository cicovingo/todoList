package com.sukruokul.todo.service;

import com.sukruokul.todo.model.Todo;
import com.sukruokul.todo.repository.jpa.TodoRepository;
import com.sukruokul.todo.repository.mongo.TodoMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoSqliteRepository;
    private final TodoMongoRepository todoMongoRepository;

    public List<Todo> findAll() {
        List<Todo> todos = new ArrayList<>();
        todos.addAll(todoSqliteRepository.findAll());
        todos.addAll(todoMongoRepository.findAll());
        return todos.stream().collect(Collectors.toMap(Todo::getId, todo -> todo, (existing, replacement) -> existing))
                .values().stream().collect(Collectors.toList());
    }

    public Optional<Todo> findById(String id) {
        Optional<Todo> mongoTodo = todoMongoRepository.findById(id);
        if (mongoTodo.isPresent()) {
            return mongoTodo;
        }
        return todoSqliteRepository.findById(id);
    }

    public Todo save(Todo todo) {
        if (todo.getId() == null) {
            todo.generateId(); // Manuel olarak ID üret
        }

        Todo savedMongoTodo = todoMongoRepository.save(todo);
        Todo savedSqliteTodo = todoSqliteRepository.save(savedMongoTodo);
        return savedSqliteTodo;
    }

    public Todo update(Todo todo) {
        Todo updatedMongoTodo = todoMongoRepository.save(todo);
        Todo updatedSqliteTodo = todoSqliteRepository.save(updatedMongoTodo);
        return updatedSqliteTodo;
    }

    public void deleteById(String id) {
        todoMongoRepository.deleteById(id);
        todoSqliteRepository.deleteById(id);
    }
}