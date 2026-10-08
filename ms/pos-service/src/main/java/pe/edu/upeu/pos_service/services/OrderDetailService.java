package pe.edu.upeu.pos_service.services;

import pe.edu.upeu.pos_service.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {

    OrderDetail create(OrderDetail orderDetail);

    OrderDetail readById(Long id);

    void delete(Long id);

    List<OrderDetail> readAll();

    List<OrderDetail> readByOrder(Long orderId);

    List<OrderDetail> readByProduct(Long productId);
}
