package com.todo.todo.Repository;

import com.todo.todo.Entity.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.Assert.assertEquals;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    private String userName = "testUser";
    private String email = "test@example.com";
    private String provider = "google";

    @BeforeEach
    void setup(){
        user = new User();
        user.setUsername(userName);
        user.setEmail(email);
        user.setProvider(provider);
        user = userRepository.save(user);
    }

    @Test
    void test_findUserByEmail() {
        Optional<User> savedUser = userRepository.findByEmail(email);

        assertEquals(savedUser.get().getUsername(), user.getUsername());
        assertEquals(savedUser.get().getEmail(), user.getEmail());
        assertEquals(savedUser.get().getProvider(), user.getProvider());
    }

    @Test
    void test_findUserByUsername() {
        Optional<User> savedUser = userRepository.findByUsername(userName);

        assertEquals(savedUser.get().getUsername(), user.getUsername());
        assertEquals(savedUser.get().getEmail(), user.getEmail());
        assertEquals(savedUser.get().getProvider(), user.getProvider());
    }

    @Test
    void test_findUserByEmailAndProvider() {
       Optional<User> savedUser = userRepository.findByEmailAndProvider(email,provider);

       assertEquals(savedUser.get().getUsername(), user.getUsername());
       assertEquals(savedUser.get().getEmail(), user.getEmail());
       assertEquals(savedUser.get().getProvider(), user.getProvider());
    }

}
