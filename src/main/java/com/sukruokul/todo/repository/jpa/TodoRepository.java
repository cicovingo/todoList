package com.sukruokul.todo.repository.jpa;

import com.sukruokul.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * author: sukru.okul
 */
@Repository("todoRepository")
public interface TodoRepository extends JpaRepository<Todo, String> {
}