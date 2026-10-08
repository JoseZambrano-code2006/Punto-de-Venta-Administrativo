package pe.edu.upeu.pos_service.services;

import org.springframework.stereotype.Service;
import pe.edu.upeu.pos_service.entity.CashBoxStatus;
import pe.edu.upeu.pos_service.entity.CashMovement;
import pe.edu.upeu.pos_service.entity.GeneralCashBox;
import pe.edu.upeu.pos_service.repository.CashMovementRepository;
import pe.edu.upeu.pos_service.repository.GeneralCashBoxRepository;


import java.util.List;

@Service
public class CashMovementServiceImpl implements CashMovementService {

    private final CashMovementRepository cashMovementRepository;
    private final GeneralCashBoxRepository generalCashBoxRepository;

    public CashMovementServiceImpl(CashMovementRepository cashMovementRepository,
                                   GeneralCashBoxRepository generalCashBoxRepository) {
        this.cashMovementRepository = cashMovementRepository;
        this.generalCashBoxRepository = generalCashBoxRepository;
    }

    @Override
    public CashMovement create(CashMovement cashMovement) {

        Long cashBoxId = cashMovement.getCashBox().getId();

        GeneralCashBox cashBox = generalCashBoxRepository.findById(cashBoxId)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada con id: " + cashBoxId));

        if (cashBox.getEstado() != CashBoxStatus.OPEN) {
            throw new RuntimeException("No se puede registrar movimientos en una caja cerrada");
        }

        cashMovement.setCashBox(cashBox);

        return cashMovementRepository.save(cashMovement);
    }

    @Override
    public CashMovement readById(Long id) {
        return cashMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con id: " + id));
    }

    @Override
    public void delete(Long id) {

        CashMovement existing = cashMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con id: " + id));

        cashMovementRepository.delete(existing);
    }

    @Override
    public List<CashMovement> readAll() {
        return cashMovementRepository.findAll();
    }

    @Override
    public List<CashMovement> readByCashBox(Long cashBoxId) {
        return cashMovementRepository.findByCashBoxId(cashBoxId);
    }
}
