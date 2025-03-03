package com.todo.todo.Intergration;

import com.todo.todo.Dto.TodoCreateDTO;
import com.todo.todo.Dto.TodoDTO;
import com.todo.todo.Entity.Todo;
import com.todo.todo.Entity.User;
import com.todo.todo.Repository.TodoRepository;
import com.todo.todo.Repository.UserRepository;
import com.todo.todo.Result.ResultCode;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 랜덤 포트에서 서버 실행
class TodoIntergrationTest {

    @LocalServerPort
    private int port;

    private User user;
    private Todo todo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    private String email = "test@example.com";
    private String provider = "google";

    @BeforeAll
    void setup() {
        RestAssured.baseURI = "http://localhost";

        user = new User();
        user.setUsername("TestUser");
        user.setEmail(email);
        user.setProvider(provider);
        user = userRepository.save(user); // 저장 후 ID 자동 생성됨
    }

    @BeforeEach
    void setPort() {
        RestAssured.port = port;

        todo = new Todo();
        todo.setUser(user);
        todo.setTitle("Task");
        todo = todoRepository.save(todo); // 저장 후 즉시 반영
    }

    @Test
    void TodoCreate() {
        TodoCreateDTO todoCreateDTO = new TodoCreateDTO();
        todoCreateDTO.setContent("RestAssured 테스트");
        todoCreateDTO.setProvider(provider);
        todoCreateDTO.setEmail(email);

        given()
                .contentType(ContentType.JSON)
                .body(todoCreateDTO)
                .when()
                .post("/api/todos/create")
                .then()
                .statusCode(201)
                .body("title", equalTo("RestAssured 테스트"));
    }

    @Test
    void GetTodo() {
        System.out.println("Saved todos: " + todoRepository.findByUserEmailAndUserProvider(email,provider)); // 저장 확인용 출력

        given()
                .queryParam("email", email)
                .queryParam("provider", provider)
                .when()
                .get("/api/todos/user")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1));
    }

    @Test
    void UpdateTodo() {
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setId(todo.getId());
        todoDTO.setContent("Updated");

        given()
                .contentType(ContentType.JSON)
                .queryParam("email", email)
                .queryParam("provider", provider)
                .body(todoDTO)
                .when()
                .post("/api/todos/update")
                .then()
                .statusCode(200)
                .body("code", equalTo(ResultCode.UPDATE_SUCCESS.getCode()));
    }

    @Test
    void DeleteTodo() {
        Map<String, Object> request = new HashMap<>();
        request.put("id", todo.getId());
        request.put("email", email);
        request.put("provider", provider);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/todos/delete")
                .then()
                .statusCode(200)
                .body("code", equalTo(ResultCode.DELETE_SUCCESS.getCode()));
    }
}

