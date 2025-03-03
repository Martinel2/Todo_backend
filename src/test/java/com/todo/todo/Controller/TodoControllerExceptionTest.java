package com.todo.todo.Controller;

import com.todo.todo.Dto.TodoCreateDTO;
import com.todo.todo.Dto.TodoDTO;
import com.todo.todo.Entity.Todo;
import com.todo.todo.Entity.User;
import com.todo.todo.Repository.TodoRepository;
import com.todo.todo.Repository.UserRepository;
import com.todo.todo.Service.TodoService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TodoControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private TodoService todoService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    private TodoDTO todoDTO;
    private User user;
    private User user2;
    private Todo todo;

    @BeforeAll
    void setUp() {
        todoDTO = new TodoDTO();
        todoDTO.setContent("Updated Todo");

        String userName = "testUser";
        String email = "test@example.com";
        String provider = "google";

        user = new User();
        user.setUsername(userName);
        user.setEmail(email);
        user.setProvider(provider);
        user = userRepository.save(user);
    }

    // 1. Todo 업데이트 실패 예외 테스트 (존재하지 않는 Todo ID)
    @Test
    void testUpdateTodoNotFound() throws Exception {

        todoDTO.setId(1L);
        doThrow(new IllegalArgumentException("Todo item not found with id: 1"))
                .when(todoService).updateTodo(any(TodoDTO.class),anyString(),anyString());

        mockMvc.perform(post("/api/todos/update")
                        .param("email", user.getEmail())
                        .param("provider", user.getProvider())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].field").value("Todo item not found with id: 1"));
    }

    @Test
    void updateTodo_Fail_AccessDenied() throws Exception {
        // Given
        user2 = new User();
        user2.setUsername("TestUser2");
        user2.setEmail("test2@example.com");
        user2.setProvider("google");
        user2 = userRepository.saveAndFlush(user2); // 저장 후 ID 자동 생성됨

        todo = new Todo();
        todo.setUser(user);
        todo.setTitle("Task");
        todo = todoRepository.saveAndFlush(todo); // 저장 후 ID 자동 생성됨

        // 다른 사용자로 요청
        String email = "test2@example.com";
        String provider = "google";

        todoDTO.setId(todo.getId());

        // When & Then
        mockMvc.perform(post("/api/todos/update")
                        .param("email", email)
                        .param("provider", provider)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors[0].field").value("You do not have permission to update this todo."));
    }



    // 2. Todo 삭제 실패 예외 테스트 (존재하지 않는 Todo ID)
    @Test
    void testDeleteTodoNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Todo item not found with id: 1"))
                .when(todoService).deleteTodo(anyLong(),anyString(),anyString());

        mockMvc.perform(post("/api/todos/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].field").value("Todo item not found with id: 1"));
    }

    @Test
    void deleteTodo_Fail_AccessDenied() throws Exception {
        // Given
        user2 = new User();
        user2.setUsername("TestUser2");
        user2.setEmail("test2@example.com");
        user2.setProvider("google");
        user2 = userRepository.saveAndFlush(user2); // 저장 후 ID 자동 생성됨

        todo = new Todo();
        todo.setUser(user);
        todo.setTitle("Task");
        todo = todoRepository.saveAndFlush(todo); // 저장 후 ID 자동 생성됨

        Map<String, Object> request = new HashMap<>();
        request.put("id", todo.getId());
        request.put("email", "test2@example.com");  // 다른 사용자
        request.put("provider", "google");

        // When & Then
        mockMvc.perform(post("/api/todos/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors[0].field").value("You do not have permission to delete this todo."));
    }


    // 3. 잘못된 요청 (존재하지 않는 사용자)
    @Test
    void testCreateTodoBadRequest() throws Exception {
        //No User

        TodoCreateDTO badTodoCreateDTO = new TodoCreateDTO();
        badTodoCreateDTO.setContent("create");
        badTodoCreateDTO.setEmail("wrong@example.com");
        badTodoCreateDTO.setProvider("google");

        mockMvc.perform(post("/api/todos/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badTodoCreateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].field").value("User not found with email: wrong@example.com"));
    }
}
