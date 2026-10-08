package pe.edu.upeu.pos_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.pos_service.entity.OrderDetail;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    // Listar detalles por orden
    List<OrderDetail> findByOrderId(Long orderId);

    //buscar si existe productos en una orden
    boolean existsByProductId(Long productId);

    // Listar detalles por producto (para reportes)
    List<OrderDetail> findByProductId(Long productId);
}

