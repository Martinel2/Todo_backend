package com.todo.todo.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoCreateDTO {
    String email;
    String provider;
    String content;
}
