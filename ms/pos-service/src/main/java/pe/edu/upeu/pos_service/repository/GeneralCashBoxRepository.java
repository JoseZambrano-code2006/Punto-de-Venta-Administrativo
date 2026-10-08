package pe.edu.upeu.pos_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.pos_service.entity.CashBoxStatus;
import pe.edu.upeu.pos_service.entity.GeneralCashBox;


import java.util.Optional;

public interface GeneralCashBoxRepository extends JpaRepository<GeneralCashBox, Long> {

    // Buscar la caja que está abierta
    Optional<GeneralCashBox> findByEstado(CashBoxStatus estado);

    // Verificar si existe una caja abierta
    boolean existsByEstado(CashBoxStatus estado);
}