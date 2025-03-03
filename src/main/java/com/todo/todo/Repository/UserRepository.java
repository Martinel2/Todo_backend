package com.todo.todo.Repository;

import com.todo.todo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndProvider(String email, String provider);
    Optional<User> findByEmail(String email);  // 추가
    Optional<User> findByUsername(String username);  // 추가

}