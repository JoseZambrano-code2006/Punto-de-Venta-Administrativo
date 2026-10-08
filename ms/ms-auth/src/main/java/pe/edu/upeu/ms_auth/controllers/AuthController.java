package pe.edu.upeu.ms_auth.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.ms_auth.dtos.TokenDto;
import pe.edu.upeu.ms_auth.dtos.UserDto;
import pe.edu.upeu.ms_auth.dtos.UserPublicDto;
import pe.edu.upeu.ms_auth.services.AuthService;

import java.util.List;


@RestController
@RequestMapping(path ="auth")
public class AuthController {

    private final AuthService authService;
    private final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path = "login")
    public ResponseEntity<TokenDto> jwtCreate(@RequestBody UserDto user) {
        return ResponseEntity.ok(this.authService.login(user));
    }

    @PostMapping(path = "register")
    public ResponseEntity<TokenDto> register(@RequestBody UserDto user) {
        return ResponseEntity.status(201).body(this.authService.register(user));
    }

    @PostMapping(path = "jwt")
    public ResponseEntity<TokenDto> jwtValidate(@RequestHeader String accessToken) {
        log.info("Auth_controller:"+accessToken);
        return  ResponseEntity.ok(this.authService.validateToken(TokenDto.builder()
                .accessToken(accessToken).build()));
    }

    @GetMapping(path = "users")
    public ResponseEntity<List<UserPublicDto>> listUsers(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseEntity.ok(this.authService.listUsers(authorization));
    }

}
