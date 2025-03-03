package com.todo.todo.Controller;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.todo.todo.Entity.User;
import com.todo.todo.Util.JwtUtil;
import com.todo.todo.Repository.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(AuthController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthController authController;

    private AutoCloseable mockStatic;

    private DecodedJWT mockJwt;

    @Configuration
    static class JwtUtilTestConfig {
        @Bean
        public JwtUtil jwtUtil() {
            return new JwtUtil();  // JwtUtil을 빈으로 등록
        }
    }

    @BeforeAll
    void setup(){
        String email = "test@example.com";

        // DecodedJWT Mock 객체 생성
        mockJwt = mock(DecodedJWT.class);
        when(mockJwt.getClaim("sub")).thenReturn(new Claim() {
            @Override
            public boolean isNull() {
                return false;
            }
            @Override
            public Boolean asBoolean() {
                return null;
            }
            @Override
            public Integer asInt() {
                return null;
            }
            @Override
            public Long asLong() {
                return null;
            }
            @Override
            public Double asDouble() {
                return null;
            }
            @Override
            public String asString() {
                return email;  // 존재하지 않는 이메일을 반환
            }
            @Override
            public Date asDate() {
                return null;
            }
            @Override
            public <T> T[] asArray(Class<T> tClazz) throws JWTDecodeException {
                return null;
            }
            @Override
            public <T> List<T> asList(Class<T> tClazz) throws JWTDecodeException {
                return null;
            }
            @Override
            public Map<String, Object> asMap() throws JWTDecodeException {
                return null;
            }
            @Override
            public <T> T as(Class<T> tClazz) throws JWTDecodeException {
                return null;
            }
        });
    }

    @BeforeEach
    public void setUp() throws Exception {
        mockStatic = Mockito.mockStatic(JwtUtil.class);  // static mock을 여기서 설정
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @AfterEach
    public void tearDown() throws Exception {
        mockStatic.close();  // 각 테스트 후 static mock 해제
    }

    @Test
    public void testLogin_Success() throws Exception {
        String email = "test@example.com";
        String password = "password";

        // JwtUtil에서 토큰 생성 mock 처리
        when(jwtUtil.createAccessToken(email)).thenReturn("mock-jwt-token");

        // When & Then: 로그인 API 호출
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"mock-jwt-token\"}"));

    }

    @Test
    public void testLogin_Failure_InvalidCredentials() throws Exception {

        // When & Then: 잘못된 자격 증명으로 로그인 시도
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalid@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"error\":\"Invalid credentials\"}"));
    }


    @Test
    public void testGetProfile_Success() throws Exception {
        String token = "valid-token";
        String email = "test@example.com";

        User user = new User();
        user.setEmail(email);
        user.setUsername("testUser");
        user.setProvider("local");

        // jwtUtil.verifyToken(token)이 mockJwt를 반환하도록 설정
        when(jwtUtil.verifyToken(token)).thenReturn(mockJwt);
        when(mockJwt.getSubject()).thenReturn(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When & Then: 사용자 프로필 API 호출
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"username\":\"testUser\",\"email\":\"test@example.com\",\"provider\":\"local\"}"));
    }


    @Test
    public void testGetProfile_Failure_UserNotFound() throws Exception {
        String token = "invalid-token";
        String email = "nonexistent@example.com";

        // jwtUtil.verifyToken(token)이 mockJwt를 반환하도록 설정
        when(jwtUtil.verifyToken(token)).thenReturn(mockJwt);
        when(mockJwt.getSubject()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());  // 사용자 없음 처리

        // When & Then: 사용자 프로필 API 호출
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());  // 403 상태 코드 확인
    }

}
