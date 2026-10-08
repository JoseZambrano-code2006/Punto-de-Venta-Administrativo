package pe.edu.upeu.pos_service.services;

import org.springframework.stereotype.Service;
import pe.edu.upeu.pos_service.entity.OrderDetail;
import pe.edu.upeu.pos_service.repository.OrderDetailRepository;
import java.util.List;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    public OrderDetailServiceImpl(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    public OrderDetail create(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail readById(Long id) {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado con id: " + id));
    }

    @Override
    public void delete(Long id) {

        OrderDetail existing = orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado con id: " + id));

        orderDetailRepository.delete(existing);
    }

    @Override
    public List<OrderDetail> readAll() {
        return orderDetailRepository.findAll();
    }

    @Override
    public List<OrderDetail> readByOrder(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderDetail> readByProduct(Long productId) {
        return orderDetailRepository.findByProductId(productId);
    }
}
