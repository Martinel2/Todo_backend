package com.todo.todo.Dto;

import com.todo.todo.Entity.Todo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor  // ✅ 기본 생성자 추가
@AllArgsConstructor
public class TodoDTO {
    private Long id;
    private String content;

    public TodoDTO(Todo todo) {
        this.id = todo.getId();
        this.content = todo.getTitle();
    }
}
