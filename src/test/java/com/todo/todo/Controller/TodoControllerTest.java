package com.todo.todo.Controller;

import com.todo.todo.Dto.TodoCreateDTO;
import com.todo.todo.Dto.TodoDTO;
import com.todo.todo.Entity.Todo;
import com.todo.todo.Service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

@ActiveProfiles("test")
public class TodoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TodoService todoService;

    @InjectMocks
    private TodoController todoController;

    private String email = "test@example.com";
    private String provider = "google";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build();
    }

    @Test
    void testCreateTodo() throws Exception {
        // Given
        Todo todo = new Todo();
        todo.setTitle("Test Todo");

        when(todoService.createTodo(any(TodoCreateDTO.class))).thenReturn(todo);

        // When & Then
        mockMvc.perform(post("/api/todos/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Test Todo\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Todo"));
    }

    @Test
    void testUpdateTodo() throws Exception {
        doNothing().when(todoService).updateTodo(any(TodoDTO.class),anyString(),anyString());  // Mock the service method

        // When & Then
        mockMvc.perform(post("/api/todos/update")
                        .param("email", email)
                        .param("provider", provider)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + 1L + "\",\"content\":\"" + "Updated Content" + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("업데이트 완료"));
    }

    @Test
    void testGetTodos() throws Exception {
        // Given
        List<TodoDTO> todos = Arrays.asList(new TodoDTO(1L, "Test Todo"));
        when(todoService.getTodosByUser(anyString(), anyString())).thenReturn(todos);

        // When & Then
        mockMvc.perform(get("/api/todos/user")
                        .param("email", email)
                        .param("provider", provider))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test Todo"));
    }

    @Test
    void testDeleteTodo() throws Exception {
        doNothing().when(todoService).deleteTodo(any(Long.class),anyString(),anyString());  // Mock the service method

        // When & Then
        mockMvc.perform(post("/api/todos/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("삭제 완료"));
    }


}
