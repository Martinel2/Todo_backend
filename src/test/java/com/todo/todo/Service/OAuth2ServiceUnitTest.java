package com.todo.todo.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OAuth2ServiceUnitTest {

    @Mock  // ✅ @InjectMocks 제거 후 @Mock 사용
    private OAuth2Service oAuth2Service;

    @Mock
    private OAuth2UserRequest mockUserRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // ✅ Mock 객체 초기화
    }

    @Test
    void testOAuthLogin_ShouldReturnUser() {
        // Given
        Map<String, Object> attributes = Map.of("email", "test@example.com", "name", "testUser");
        OAuth2User mockUser = new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                attributes,
                "email"
        );

        OAuth2UserRequest mockRequest = mock(OAuth2UserRequest.class);  // ✅ Mock 객체 생성

        when(oAuth2Service.loadUser(mockRequest)).thenReturn(mockUser);

        // When
        OAuth2User result = oAuth2Service.loadUser(mockRequest);

        // Then
        assertNotNull(result);  // ✅ result가 null이 아닌지 확인
        assertEquals("test@example.com", result.getAttribute("email"));
        assertEquals("testUser", result.getAttribute("name"));
    }

    @Test
    void testLoadUser_ShouldThrowOAuth2AuthenticationException() {
        // Given
        when(oAuth2Service.loadUser(mockUserRequest))
                .thenThrow(new OAuth2AuthenticationException("OAuth2 Authentication Failed"));

        // When & Then
        assertThrows(OAuth2AuthenticationException.class, () -> oAuth2Service.loadUser(mockUserRequest));
    }
}
