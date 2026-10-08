package pe.edu.upeu.pos_service.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "general_cash_box")
public class GeneralCashBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Account (muchas cajas → un usuario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_account", nullable = false)
    private Account account;

    // Fecha de apertura
    @Column(nullable = false)
    private LocalDateTime opening;

    // Fecha de cierre
    @Column
    private LocalDateTime closing;

    // Saldo inicial
    @Column(name = "saldo_inicial", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoInicial;

    // Saldo final
    @Column(name = "saldo_final", precision = 10, scale = 2)
    private BigDecimal saldoFinal;

    // ENUM → estado (OPEN / CLOSED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CashBoxStatus estado;

    // Fecha de creación automática
    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    public GeneralCashBox() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public LocalDateTime getOpening() {
        return opening;
    }

    public void setOpening(LocalDateTime opening) {
        this.opening = opening;
    }

    public LocalDateTime getClosing() {
        return closing;
    }

    public void setClosing(LocalDateTime closing) {
        this.closing = closing;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public CashBoxStatus getEstado() {
        return estado;
    }

    public void setEstado(CashBoxStatus estado) {
        this.estado = estado;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
