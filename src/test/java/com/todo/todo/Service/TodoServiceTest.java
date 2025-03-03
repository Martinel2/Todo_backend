package com.todo.todo.Service;

import com.todo.todo.Dto.TodoCreateDTO;
import com.todo.todo.Dto.TodoDTO;
import com.todo.todo.Entity.Todo;
import com.todo.todo.Entity.User;
import com.todo.todo.Repository.TodoRepository;
import com.todo.todo.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;



@SpringBootTest
@Transactional
@ActiveProfiles("test")
class TodoServiceTest {
    @Autowired
    private TodoService todoService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    private User user;
    private Todo todo1;

    private String email = "user@example.com";
    private String provider = "google";
    private String task = "Complete the task";
    @BeforeEach
    void setup(){
        // Setup test user
        user = new User();
        user.setUsername("TestUser");
        user.setEmail(email);
        user.setProvider(provider);
        userRepository.save(user);


        // Add todos for the user
        todo1 = new Todo();
        todo1.setTitle(task);
        todo1.setUser(user);
        todoRepository.save(todo1);
    }

    @Test
    void testCreateTodoForUser() {
        TodoCreateDTO todoCreateDTO = new TodoCreateDTO();
        todoCreateDTO.setProvider(provider);
        todoCreateDTO.setEmail(email);
        todoCreateDTO.setContent(task);

        Todo todo = todoService.createTodo(todoCreateDTO);

        assertNotNull(todo);
        assertEquals(task, todo.getTitle());
        assertEquals(user, todo.getUser());
    }

    @Test
    void testCreateTodo_Exception() {
        //No exist user
        String fakeEmail = "noexist@example.com";

        //Create Todo
        TodoCreateDTO todoCreateDTO = new TodoCreateDTO();
        todoCreateDTO.setProvider(provider);
        todoCreateDTO.setEmail(fakeEmail);
        todoCreateDTO.setContent(task);

        assertThrows(IllegalArgumentException.class, ()-> todoService.createTodo(todoCreateDTO));
    }

    @Test
    void testGetTodosByUser() {
        // Add todos for the user
        TodoCreateDTO todoCreateDTO = new TodoCreateDTO();
        todoCreateDTO.setContent("Task 2");
        todoCreateDTO.setProvider(provider);
        todoCreateDTO.setEmail(email);
        todoService.createTodo(todoCreateDTO);

        // Test fetching todos
        List<TodoDTO> todos = todoService.getTodosByUser(email, provider);

        assertEquals(2, todos.size());
        assertTrue(todos.stream().anyMatch(t -> t.getContent().equals(task)));
        assertTrue(todos.stream().anyMatch(t -> t.getContent().equals("Task 2")));
    }

    @Test
    void testUpdate() {
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setId(todo1.getId());
        todoDTO.setContent("ddd");
        todoService.updateTodo(todoDTO,email,provider);

        // Test fetching todos
        List<TodoDTO> todos = todoService.getTodosByUser(email, provider);

        assertEquals(1, todos.size());
        assertTrue(todos.stream().anyMatch(t -> t.getContent().equals("ddd")));
    }

    @Test
    void testUpdateTodo_ShouldThrowException_WhenTodoNotFound() {
        // Given
        long invalidId = 999L;

        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setId(invalidId);
        todoDTO.setContent("Exception");

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> todoService.updateTodo(todoDTO,email,provider));

        assertEquals("Todo item not found with id: 999", exception.getMessage());
    }

    @Test
    void testDelete() {
        // Add todos for the user
        Todo todo2 = new Todo();
        todo2.setTitle("Task 2");
        todo2.setUser(user);
        todoRepository.save(todo2);

        todoService.deleteTodo(todo1.getId(),email,provider);

        // Test fetching todos
        List<TodoDTO> todos = todoService.getTodosByUser(email, provider);

        assertEquals(1, todos.size());
        assertTrue(!todos.stream().anyMatch(t -> t.getContent().equals(task)));
        assertTrue(todos.stream().anyMatch(t -> t.getContent().equals("Task 2")));
    }

    @Test
    void testDeleteTodo_ShouldThrowException_WhenTodoNotFound() {
        // Given
        long invalidId = 999L;

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> todoService.deleteTodo(invalidId,email,provider));

        assertEquals("Todo item not found with id: 999", exception.getMessage());
    }

}
