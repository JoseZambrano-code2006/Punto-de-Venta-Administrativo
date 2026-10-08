package pe.edu.upeu.ms_auth.services;

import pe.edu.upeu.ms_auth.dtos.TokenDto;
import pe.edu.upeu.ms_auth.dtos.UserDto;
import pe.edu.upeu.ms_auth.dtos.UserPublicDto;

import java.util.List;

public interface AuthService {
    TokenDto login(UserDto user);
    TokenDto register(UserDto user);
    TokenDto validateToken(TokenDto token);
    List<UserPublicDto> listUsers(String authorizationHeader);
}
