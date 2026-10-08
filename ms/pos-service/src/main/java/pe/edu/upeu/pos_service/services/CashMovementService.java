package pe.edu.upeu.pos_service.services;

import pe.edu.upeu.pos_service.entity.CashMovement;

import java.util.List;

public interface CashMovementService {

    CashMovement create(CashMovement cashMovement);

    CashMovement readById(Long id);

    void delete(Long id);

    List<CashMovement> readAll();

    List<CashMovement> readByCashBox(Long cashBoxId);
}
