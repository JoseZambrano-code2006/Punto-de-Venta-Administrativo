package pe.edu.upeu.ms_auth.services;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.ms_auth.dtos.TokenDto;
import pe.edu.upeu.ms_auth.dtos.UserDto;
import pe.edu.upeu.ms_auth.dtos.UserPublicDto;
import pe.edu.upeu.ms_auth.entity.UserEntity;
import pe.edu.upeu.ms_auth.helpers.JwtHelper;
import pe.edu.upeu.ms_auth.repository.UserRepository;

import java.util.List;


@Transactional
@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtHelper jwtHelper;

    private final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private static final String USER_EXCEPTION_MSG = "Error to auth user";

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtHelper jwtHelper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtHelper = jwtHelper;
    }

    @Override
    public TokenDto login(UserDto user) {
        final var userFromDB = this.userRepository.findByUsername(user.getUsername())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED, USER_EXCEPTION_MSG));
        this.validPassword(user,userFromDB);
        return TokenDto.builder().accessToken(this.jwtHelper.createToken(userFromDB.getUsername())).build();
    }

    @Override
    public TokenDto register(UserDto user) {
        validateRegistration(user);

        if (this.userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuario ya existe");
        }

        UserEntity entity = new UserEntity();
        entity.setUsername(user.getUsername().trim());
        entity.setPassword(this.passwordEncoder.encode(user.getPassword()));
        this.userRepository.save(entity);

        return this.login(user);
    }

    @Override
    public TokenDto validateToken(TokenDto token) {
        log.info("AuthServiceImpl:"+token);

        if(this.jwtHelper.validateToken(token.getAccessToken())){
            log.info("ingresa al if de AuthServiceImpl:"+token);
            return TokenDto.builder().accessToken(token.getAccessToken()).build();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, USER_EXCEPTION_MSG);
    }

    @Override
    public List<UserPublicDto> listUsers(String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        if (!this.jwtHelper.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, USER_EXCEPTION_MSG);
        }

        return this.userRepository.findAll().stream()
                .map(user -> new UserPublicDto(user.getId(), user.getUsername()))
                .toList();
    }

    private void validPassword(UserDto userDto, UserEntity userEntity){
        if(!this.passwordEncoder.matches(userDto.getPassword(), userEntity.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,USER_EXCEPTION_MSG);
        }}

    private void validateRegistration(UserDto user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario es obligatorio");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 6 caracteres");
        }
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, USER_EXCEPTION_MSG);
        }
        return authorizationHeader.substring(7).trim();
    }
}
