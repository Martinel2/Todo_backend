package com.todo.todo.Service;

import com.todo.todo.Dto.UserProfile;
import com.todo.todo.Entity.User;
import com.todo.todo.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class OAuth2ServiceTest {
    @InjectMocks
    private OAuth2Service oAuth2Service;

    @Mock
    private UserRepository userRepository;

    @Test
    void testUpdateOrSaveUser_NewUser_ShouldSave() {
        // Given
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername("newUser");
        userProfile.setEmail("newUser@example.com");
        userProfile.setProvider("google");

        User mockUser = new User();
        mockUser.setUsername("newUser");
        mockUser.setEmail("newUser@example.com");
        mockUser.setProvider("google");

        when(userRepository.findByEmailAndProvider(anyString(), anyString()))
                .thenReturn(Optional.empty()); // 새 유저라서 없음

        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When
        User savedUser = oAuth2Service.updateOrSaveUser(userProfile);

        // Then
        assertEquals("newUser", savedUser.getUsername());
        assertEquals("newUser@example.com", savedUser.getEmail());
    }

    @Test
    void testUpdateOrSaveUser_ShouldThrowException_WhenUserRepositoryReturnsNull() {
        // Given
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail("test@example.com");
        userProfile.setProvider("google");

        when(userRepository.findByEmailAndProvider(anyString(), anyString()))
                .thenReturn(null); // ❌ Optional.empty()가 아닌 null 반환

        // When & Then
        assertThrows(NullPointerException.class, () -> oAuth2Service.updateOrSaveUser(userProfile));
    }

}

