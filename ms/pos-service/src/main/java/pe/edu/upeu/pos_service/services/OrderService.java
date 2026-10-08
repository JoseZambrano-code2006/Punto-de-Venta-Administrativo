package pe.edu.upeu.pos_service.services;

import pe.edu.upeu.pos_service.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    Order create(Order order);

    Order readById(Long id);

    Order update(Order order, Long id);

    void delete(Long id);

    List<Order> readAll();

    List<Order> readByCashBox(Long cashBoxId);

    List<Order> readByDate(String date);

    BigDecimal sumTotalByDate(String date);
}