package com.todo.todo.Service;

import com.todo.todo.Dto.TodoCreateDTO;
import com.todo.todo.Dto.TodoDTO;
import com.todo.todo.Entity.Todo;
import com.todo.todo.Entity.User;
import com.todo.todo.Repository.TodoRepository;
import com.todo.todo.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TodoService {
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private UserRepository userRepository;

    public List<TodoDTO> getTodosByUser(String email, String provider) throws IllegalArgumentException{
        List<Todo> todos = todoRepository.findByUserEmailAndUserProvider(email, provider);
        return todos.stream().map(TodoDTO::new).collect(Collectors.toList());
    }

    public Todo createTodo(TodoCreateDTO todoCreateDTO) {
        //NullPointerException은 예상치못한 예외에서 사용하는 것
        User user = userRepository.findByEmailAndProvider(todoCreateDTO.getEmail(), todoCreateDTO.getProvider())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + todoCreateDTO.getEmail()));

        Todo todo = new Todo();
        todo.setTitle(todoCreateDTO.getContent());
        todo.setUser(user);
        return todoRepository.save(todo);
    }

    // ✅ Todo 수정 (권한 확인 추가)
    public void updateTodo(TodoDTO todoDTO, String email, String provider) {
        User user = userRepository.findByEmailAndProvider(email,provider)
                .orElseThrow(() -> new EntityNotFoundException("Invalid Access"));

        Todo todo = todoRepository.findById(todoDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Todo item not found with id: " + todoDTO.getId()));

        if (!todo.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to update this todo.");
        }

        todo.setTitle(todoDTO.getContent());
        todoRepository.save(todo);
    }

    // ✅ Todo 삭제 (권한 확인 추가)
    public void deleteTodo(Long id, String email, String provider) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo item not found with id: " + id));

        if (!todo.getUser().getEmail().equals(email) || !todo.getUser().getProvider().equals(provider)) {
            throw new AccessDeniedException("You do not have permission to delete this todo.");
        }

        todoRepository.delete(todo);
    }


    public List<Todo> findAll(){
        return todoRepository.findAll();
    }
}
