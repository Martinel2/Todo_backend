package com.todo.todo.Controller;

import com.todo.todo.Dto.UserProfile;
import com.todo.todo.Entity.User;
import com.todo.todo.Repository.UserRepository;
import com.todo.todo.Util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        // 실제로는 DB에서 유저를 검증해야 함
        if ("test@example.com".equals(email) && "password".equals(password)) {
            String token = jwtUtil.createAccessToken(email);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }
    @GetMapping("/me") // 로그인 된 사용자라면 토큰을 통해 사용자 정보 불러오기
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        String email = null;

        try {
            log.info("get email");
            email = jwtUtil.verifyToken(token).getSubject();
            log.info("sub : " + jwtUtil.verifyToken(token));
        } catch (Exception e) {
            // 토큰 검증 중 예외가 발생하면 404 반환
            return ResponseEntity.status(401).body(Map.of("error", "User email is not found"));
        }

        if (email != null) {
            log.info("user: {}", email);
            // 사용자 이메일로 찾고, 존재하지 않으면 404 반환
            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                // 사용자 정보가 없을 경우 404 오류 반환
                return ResponseEntity.status(403).body(Map.of("error", "User not found"));
            }

            // 사용자 프로필 반환
            UserProfile userProfile = new UserProfile();
            userProfile.setUsername(user.getUsername());
            userProfile.setEmail(user.getEmail());
            userProfile.setProvider(user.getProvider());

            return ResponseEntity.ok(userProfile);
        }

        // 이메일이 null인 경우 400 또는 다른 상태 코드를 반환할 수 있음
        return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
    }


   /* @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getUser(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        String username = String.valueOf(jwtUtil.verifyToken(token));
        if (username != null) {
            return ResponseEntity.ok(Map.of("username", username));
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }
    }
    */
}
