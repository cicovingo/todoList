package com.sukruokul.todo.repository.mongo;

import com.sukruokul.todo.model.Todo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * author: sukru.okul
 */
@Repository("todoMongoRepository")
public interface TodoMongoRepository extends MongoRepository<Todo, String> {
}