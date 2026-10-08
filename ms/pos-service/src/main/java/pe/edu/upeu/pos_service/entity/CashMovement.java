package pe.edu.upeu.pos_service.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_movement")
public class CashMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación: muchos movimientos → una caja
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cash_box", nullable = false)
    private GeneralCashBox cashBox;

    // ENUM → tipo de movimiento (INCOME / EXPENSE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovementType type;

    // Monto del movimiento
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // Descripción del movimiento
    @Column(length = 255)
    private String description;

    // Fecha de creación automática
    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    public CashMovement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GeneralCashBox getCashBox() {
        return cashBox;
    }

    public void setCashBox(GeneralCashBox cashBox) {
        this.cashBox = cashBox;
    }

    public MovementType getType() {
        return type;
    }

    public void setType(MovementType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
