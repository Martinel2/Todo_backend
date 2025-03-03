package com.todo.todo.Repository;

import com.todo.todo.Entity.Todo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Test
    void testSaveAndFind() {
        Todo todo = new Todo();
        todo.setTitle("Test Todo");
        todoRepository.save(todo);

        Optional<Todo> found = todoRepository.findById(todo.getId());
        assertTrue(found.isPresent());
    }

    @Test
    void testFindAll() {
        Todo todo = new Todo();
        todo.setTitle("Test Todo");
        todoRepository.save(todo);

        Todo todo2 = new Todo();
        todo2.setTitle("Test Todo2");
        todoRepository.save(todo2);

        List<Todo> found = todoRepository.findAll();
        assertTrue(found.contains(todo) && found.contains(todo2));
    }
}
