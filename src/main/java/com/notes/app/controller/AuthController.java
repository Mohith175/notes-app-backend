package com.notes.app.controller;

import com.notes.app.dto.LoginDTO;
import com.notes.app.entity.User;
import com.notes.app.service.AuthService;
import com.notes.app.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final NoteService noteService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User request) {
        String token = authService.register(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginDTO request,
            @RequestParam(required = false) String shareId // <-- optional shareId
    ) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Generate JWT token
        String token = authService.login(request);

        // If login is from a shared note, add the note to the user's account
        if (shareId != null && !shareId.isBlank()) {
            noteService.addCollaborator(shareId, request.getUsername());
        }

        return ResponseEntity.ok(token);
    }

}
