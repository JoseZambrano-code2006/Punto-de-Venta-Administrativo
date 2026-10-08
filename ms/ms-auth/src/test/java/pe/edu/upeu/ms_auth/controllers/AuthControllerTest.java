package pe.edu.upeu.ms_auth.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upeu.ms_auth.dtos.TokenDto;
import pe.edu.upeu.ms_auth.dtos.UserDto;
import pe.edu.upeu.ms_auth.dtos.UserPublicDto;
import pe.edu.upeu.ms_auth.services.AuthService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void loginReturnsAccessToken() throws Exception {
        when(authService.login(any(UserDto.class)))
                .thenReturn(TokenDto.builder().accessToken("test-jwt-token").build());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test-jwt-token"));
    }

    @Test
    void registerReturnsAccessToken() throws Exception {
        when(authService.register(any(UserDto.class)))
                .thenReturn(TokenDto.builder().accessToken("register-jwt-token").build());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nuevo\",\"password\":\"secret123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("register-jwt-token"));
    }

    @Test
    void listUsersReturnsPublicUserData() throws Exception {
        when(authService.listUsers(eq("Bearer test-jwt-token")))
                .thenReturn(List.of(new UserPublicDto(1L, "admin")));

        mockMvc.perform(get("/auth/users")
                        .header("Authorization", "Bearer test-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("admin"));
    }

    @Test
    void jwtValidateReturnsSameToken() throws Exception {
        when(authService.validateToken(any(TokenDto.class)))
                .thenReturn(TokenDto.builder().accessToken("test-jwt-token").build());

        mockMvc.perform(post("/auth/jwt")
                        .header("accessToken", "test-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test-jwt-token"));
    }
}
