package com.example.wsdpractice.controller;

import com.example.wsdpractice.dto.ApiResponse;
import com.example.wsdpractice.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    // Controller에서 데이터 관리
    private final Map<Long, UserDto> store = new ConcurrentHashMap<>();
    private final Map<String, UserDto> userIdIndex = new ConcurrentHashMap<>(); // userId로 검색용
    private final AtomicLong sequence = new AtomicLong(1);

    // POST 1: 회원가입
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody UserDto user) {
        // 유효성 검사
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("사용자 ID는 필수입니다"));
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("비밀번호는 필수입니다"));
        }

        // 중복 확인
        if (userIdIndex.containsKey(user.getUserId())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("이미 존재하는 사용자 ID입니다"));
        }

        // 데이터 생성
        user.setId(sequence.getAndIncrement());
        store.put(user.getId(), user);
        userIdIndex.put(user.getUserId(), user);

        // 응답 시 비밀번호 제거
        UserDto response = new UserDto();
        response.setId(user.getId());
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    // POST 2: 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @RequestHeader(value = "X-USER-ID") String userId,
            @RequestHeader(value = "X-USER-PW") String password) {

        // 사용자 조회
        UserDto user = userIdIndex.get(userId);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("사용자를 찾을 수 없습니다"));
        }

        // 비밀번호 확인
        if (!user.getPassword().equals(password)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("비밀번호가 일치하지 않습니다"));
        }

        // 로그인 성공
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getUserId());
        result.put("name", user.getName());
        result.put("message", "로그인 성공");

        return ResponseEntity
                .ok(ApiResponse.success(result));
    }

    // GET 1: 전체 사용자 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = new ArrayList<>();

        // 비밀번호 제거하고 반환
        for (UserDto user : store.values()) {
            UserDto response = new UserDto();
            response.setId(user.getId());
            response.setUserId(user.getUserId());
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            users.add(response);
        }

        return ResponseEntity
                .ok(ApiResponse.success(users));
    }

    // GET 2: 특정 사용자 조회 - id로
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        UserDto user = store.get(id);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("ID " + id + "에 해당하는 사용자를 찾을 수 없습니다"));
        }

        // 비밀번호 제거
        UserDto response = new UserDto();
        response.setId(user.getId());
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());

        return ResponseEntity
                .ok(ApiResponse.success(response));
    }
}