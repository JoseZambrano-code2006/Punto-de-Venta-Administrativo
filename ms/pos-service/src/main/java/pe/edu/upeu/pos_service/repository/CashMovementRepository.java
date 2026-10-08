package pe.edu.upeu.pos_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.pos_service.entity.CashMovement;
import pe.edu.upeu.pos_service.entity.MovementType;

import java.util.List;

public interface CashMovementRepository extends JpaRepository<CashMovement, Long> {

    // Listar movimientos por caja
    List<CashMovement> findByCashBoxId(Long cashBoxId);

    // Listar por tipo (INCOME / EXPENSE)
    List<CashMovement> findByType(MovementType type);
}
