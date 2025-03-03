package com.todo.todo.Config;

import com.todo.todo.Repository.TodoRepository;
import com.todo.todo.Service.TodoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    public TodoService todoService() {return new TodoService();}
}
