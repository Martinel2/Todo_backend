package com.todo.todo.Service;

import com.todo.todo.Entity.User;
import com.todo.todo.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    void testLoadUserByUserName() {
        // Given
        User mockUser = new User();
        mockUser.setUsername("newUser");
        mockUser.setEmail("newUser@example.com");
        mockUser.setProvider("google");
        mockUser.setPassword("password");

        when(userRepository.findByEmail("newUser@example.com"))
                .thenReturn(Optional.of(mockUser));

        // When
        UserDetails savedUser = customUserDetailsService.loadUserByUsername("newUser@example.com");

        // Then
        assertEquals("newUser@example.com", savedUser.getUsername());
        assertEquals("password", savedUser.getPassword());
    }
}
