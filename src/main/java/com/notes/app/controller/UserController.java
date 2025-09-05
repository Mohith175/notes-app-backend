package com.notes.app.controller;

import com.notes.app.dto.UserDTO;
import com.notes.app.entity.User;
import com.notes.app.entity.UserPrincipal;
import com.notes.app.repository.UserRepo;
import com.notes.app.service.MapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepo userRepo;
    private final MapperService mapperService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getProfile(@AuthenticationPrincipal UserPrincipal user) {
        User userInfo = userRepo.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(mapperService.toUserDTO(userInfo));
    }
}
