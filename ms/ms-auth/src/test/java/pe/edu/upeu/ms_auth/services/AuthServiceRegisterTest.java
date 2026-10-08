package pe.edu.upeu.ms_auth.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.ms_auth.dtos.TokenDto;
import pe.edu.upeu.ms_auth.dtos.UserDto;
import pe.edu.upeu.ms_auth.entity.UserEntity;
import pe.edu.upeu.ms_auth.helpers.JwtHelper;
import pe.edu.upeu.ms_auth.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceRegisterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtHelper jwtHelper;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void registerEncodesPasswordWithBcryptAndReturnsToken() {
        UserDto dto = UserDto.builder().username("nuevo").password("secret123").build();
        UserEntity saved = new UserEntity();
        saved.setUsername("nuevo");
        saved.setPassword("$2a$10$hashed");

        when(userRepository.findByUsername("nuevo"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(saved));
        when(passwordEncoder.encode("secret123")).thenReturn("$2a$10$hashed");
        when(passwordEncoder.matches("secret123", "$2a$10$hashed")).thenReturn(true);
        when(jwtHelper.createToken("nuevo")).thenReturn("jwt-token");

        TokenDto token = authService.register(dto);

        assertEquals("jwt-token", token.getAccessToken());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertEquals("$2a$10$hashed", captor.getValue().getPassword());
    }

    @Test
    void registerRejectsDuplicateUsername() {
        UserDto dto = UserDto.builder().username("admin").password("secret123").build();
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(new UserEntity()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(dto));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(userRepository, never()).save(any());
    }
}
