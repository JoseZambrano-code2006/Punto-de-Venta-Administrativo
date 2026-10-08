package pe.edu.upeu.ms_auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.ms_auth.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

}
