package pe.edu.upeu.pos_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.upeu.pos_service.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository <Order, Long> {

    List<Order> findByCreationDateBetween(LocalDateTime start, LocalDateTime end);

    // Listar órdenes por caja
    List<Order> findByCashBoxId(Long cashBoxId);



    // Obtener el número máximo de orden por caja
    @Query("SELECT MAX(o.orderNumber) FROM Order o WHERE o.cashBox.id = :cashBoxId")
    Integer findMaxOrderNumberByCashBox(Long cashBoxId);

}
