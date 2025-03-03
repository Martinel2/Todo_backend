package com.todo.todo.Controller;

import com.todo.todo.Dto.TodoCreateDTO;
import com.todo.todo.Dto.TodoDTO;
import com.todo.todo.Entity.Todo;
import com.todo.todo.Result.ResultCode;
import com.todo.todo.Result.ResultResponse;
import com.todo.todo.Service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // ✅ 할 일 추가
    @PostMapping("/create")
    public ResponseEntity<Todo> createTodo(@RequestBody TodoCreateDTO todoCreateDTO) {
        Todo todo = todoService.createTodo(todoCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(todo); // 예외 발생 시 자동 처리
    }

    @PostMapping("/update")
    public ResponseEntity<ResultResponse> updateTodo(@RequestBody TodoDTO todoDTO,
                                             @RequestParam String email,
                                             @RequestParam String provider) {
        todoService.updateTodo(todoDTO, email, provider);

        ResultResponse result = ResultResponse.of(ResultCode.UPDATE_SUCCESS, true);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    // ✅ 할 일 삭제 (email, provider 정보 필요)
    @PostMapping("/delete")
    public ResponseEntity<ResultResponse> deleteTodo(@RequestBody Map<String, Object> request) {
        Long id = ((Number) request.get("id")).longValue();
        String email = (String) request.get("email");
        String provider = (String) request.get("provider");

        todoService.deleteTodo(id, email, provider);
        ResultResponse result = ResultResponse.of(ResultCode.DELETE_SUCCESS, true);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("/user")
    public ResponseEntity<List<TodoDTO>> getTodos(@RequestParam String email, @RequestParam String provider) {
        List<TodoDTO> todos = new ArrayList<>();
        if(todoService.getTodosByUser(email, provider) != null) todos = todoService.getTodosByUser(email, provider);
        return ResponseEntity.ok(todos);
    }




}

