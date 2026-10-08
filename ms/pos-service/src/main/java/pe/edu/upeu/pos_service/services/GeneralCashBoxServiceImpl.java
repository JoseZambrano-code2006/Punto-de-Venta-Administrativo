package pe.edu.upeu.pos_service.services;

import org.springframework.stereotype.Service;
import pe.edu.upeu.pos_service.entity.*;
import pe.edu.upeu.pos_service.repository.AccountRepository;
import pe.edu.upeu.pos_service.repository.CashMovementRepository;
import pe.edu.upeu.pos_service.repository.GeneralCashBoxRepository;
import pe.edu.upeu.pos_service.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GeneralCashBoxServiceImpl implements GeneralCashBoxService {

    private final GeneralCashBoxRepository generalCashBoxRepository;
    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final CashMovementRepository cashMovementRepository;

    public GeneralCashBoxServiceImpl(GeneralCashBoxRepository generalCashBoxRepository, AccountRepository accountRepository, OrderRepository orderRepository, CashMovementRepository cashMovementRepository) {
        this.generalCashBoxRepository = generalCashBoxRepository;
        this.accountRepository = accountRepository;
        this.orderRepository = orderRepository;
        this.cashMovementRepository = cashMovementRepository;
    }

    @Override
    public GeneralCashBox openCashBox(GeneralCashBox cashBox) {

        // Validar que no exista otra caja abierta
        if (generalCashBoxRepository.existsByEstado(CashBoxStatus.OPEN)) {
            throw new RuntimeException("Ya existe una caja abierta");
        }

        // Validar que la cuenta exista
        Long accountId = cashBox.getAccount().getId();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con id: " + accountId));

        cashBox.setAccount(account);
        cashBox.setEstado(CashBoxStatus.OPEN);
        cashBox.setOpening(LocalDateTime.now());
        cashBox.setClosing(null);
        cashBox.setSaldoFinal(null);

        return generalCashBoxRepository.save(cashBox);
    }

    @Override
    public GeneralCashBox closeCashBox(Long id) {

        GeneralCashBox cashBox = generalCashBoxRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada con id: " + id));

        if (cashBox.getEstado() == CashBoxStatus.CLOSED) {
            throw new RuntimeException("La caja ya está cerrada");
        }

        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalEgresos = BigDecimal.ZERO;

        List<Order> orders = orderRepository.findByCashBoxId(id);

        for (Order order : orders) {
            totalVentas = totalVentas.add(order.getTotalAmount());
        }

        List<CashMovement> movements = cashMovementRepository.findByCashBoxId(id);

        for (CashMovement movement : movements) {

            if (movement.getType() == MovementType.INCOME) {
                totalIngresos = totalIngresos.add(movement.getAmount());
            }

            if (movement.getType() == MovementType.EXPENSE) {
                totalEgresos = totalEgresos.add(movement.getAmount());
            }
        }

        BigDecimal saldoFinal = cashBox.getSaldoInicial()
                .add(totalVentas)
                .add(totalIngresos)
                .subtract(totalEgresos);

        cashBox.setSaldoFinal(saldoFinal);
        cashBox.setEstado(CashBoxStatus.CLOSED);
        cashBox.setEstado(CashBoxStatus.CLOSED);
        cashBox.setClosing(LocalDateTime.now());

        return generalCashBoxRepository.save(cashBox);
    }

    @Override
    public GeneralCashBox readById(Long id) {
        return generalCashBoxRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada con id: " + id));
    }

    @Override
    public List<GeneralCashBox> readAll() {
        return generalCashBoxRepository.findAll();
    }

    @Override
    public GeneralCashBox getOpenCashBox() {
        return generalCashBoxRepository.findByEstado(CashBoxStatus.OPEN)
                .orElseThrow(() -> new RuntimeException("No hay caja abierta"));
    }
}