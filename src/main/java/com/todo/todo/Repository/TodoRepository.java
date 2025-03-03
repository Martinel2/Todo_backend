package com.todo.todo.Repository;

import com.todo.todo.Entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUserEmailAndUserProvider(String email, String provider);
}
