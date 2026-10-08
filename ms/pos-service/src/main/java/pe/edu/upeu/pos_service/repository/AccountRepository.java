package pe.edu.upeu.pos_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.pos_service.entity.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // Busca una cuenta por correo
    Optional<Account> findByMail(String mail);

    // Verifica si ya existe una cuenta con ese correo
    boolean existsByMail(String mail);
}
