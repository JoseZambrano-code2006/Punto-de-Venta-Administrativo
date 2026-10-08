package pe.edu.upeu.pos_service.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pe.edu.upeu.pos_service.client.ClienteFeignClient;
import pe.edu.upeu.pos_service.dto.ClienteDTO;
import pe.edu.upeu.pos_service.entity.*;
import pe.edu.upeu.pos_service.repository.GeneralCashBoxRepository;
import pe.edu.upeu.pos_service.repository.OrderRepository;
import pe.edu.upeu.pos_service.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final GeneralCashBoxRepository generalCashBoxRepository;
    private final ProductRepository productRepository;
    private final ClienteFeignClient clienteFeignClient; // Integración de Feign

    // Se añade ClienteFeignClient al constructor
    public OrderServiceImpl(OrderRepository orderRepository,
                            GeneralCashBoxRepository generalCashBoxRepository,
                            ProductRepository productRepository,
                            ClienteFeignClient clienteFeignClient) {
        this.orderRepository = orderRepository;
        this.generalCashBoxRepository = generalCashBoxRepository;
        this.productRepository = productRepository;
        this.clienteFeignClient = clienteFeignClient;
    }

    @Override
    public Order create(Order order) {

        GeneralCashBox openCashBox = generalCashBoxRepository.findByEstado(CashBoxStatus.OPEN)
                .orElseThrow(() -> new RuntimeException("No hay caja abierta. Debe abrir una caja antes de vender."));

        // --- INICIO INTEGRACIÓN MS-CLIENTES (Patrón Snapshot) ---
        Long idBuscar = (order.getClienteId() != null) ? order.getClienteId() : 1L;

        try {
            ClienteDTO cliente = clienteFeignClient.obtenerClientePorId(idBuscar).getBody();

            if (cliente != null) {
                order.setClienteId(cliente.getId());
                order.setCustomerName(cliente.getNombreCompleto());
                order.setCustomerDocument(cliente.getNumeroDocumento());
            } else {
                asignarClientePorDefecto(order);
            }
        } catch (Exception e) {
            // Tolerancia a fallos: Si el ms-clientes está caído, la venta no se detiene
            System.err.println("Error al contactar ms-clientes: " + e.getMessage());
            asignarClientePorDefecto(order);
        }
        // --- FIN INTEGRACIÓN ---

        Integer lastOrderNumber = orderRepository.findMaxOrderNumberByCashBox(openCashBox.getId());
        int newOrderNumber = (lastOrderNumber == null) ? 1 : lastOrderNumber + 1;

        order.setCashBox(openCashBox);
        order.setOrderNumber(newOrderNumber);

        BigDecimal total = BigDecimal.ZERO;

        if (order.getDetails() != null) {
            for (OrderDetail detail : order.getDetails()) {

                Long productId = detail.getProduct().getId();

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));

                detail.setProduct(product);
                detail.setOrder(order);
                detail.setUnitPrice(product.getPrice());

                BigDecimal subtotal = product.getPrice()
                        .multiply(BigDecimal.valueOf(detail.getQuantity()));

                detail.setSubtotal(subtotal);

                total = total.add(subtotal);
            }
        }

        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    @Override
    public Order readById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con id: " + id));
    }

    @Override
    public Order update(Order order, Long id) {

        Order orderToUpdate = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con id: " + id));

        if (orderToUpdate.getCashBox().getEstado() != CashBoxStatus.OPEN) {
            throw new RuntimeException("No se puede actualizar una orden de una caja cerrada");
        }

        // --- ACTUALIZACIÓN DE CLIENTE ---
        // Si en la actualización envían un nuevo clienteId, pedimos sus datos frescos
        if (order.getClienteId() != null && !order.getClienteId().equals(orderToUpdate.getClienteId())) {
            try {
                ClienteDTO cliente = clienteFeignClient.obtenerClientePorId(order.getClienteId()).getBody();
                if (cliente != null) {
                    orderToUpdate.setClienteId(cliente.getId());
                    orderToUpdate.setCustomerName(cliente.getNombreCompleto());
                    orderToUpdate.setCustomerDocument(cliente.getNumeroDocumento());
                }
            } catch (Exception e) {
                System.err.println("Error al contactar ms-clientes en actualización: " + e.getMessage());
            }
        } else if (order.getCustomerName() != null) {
            // Respaldo por si solo están forzando un cambio de nombre manual
            orderToUpdate.setCustomerName(order.getCustomerName());
        }

        orderToUpdate.setOrderType(order.getOrderType());

        if (order.getDetails() != null) {

            orderToUpdate.getDetails().clear();

            BigDecimal nuevoTotal = BigDecimal.ZERO;

            for (OrderDetail nuevoDetail : order.getDetails()) {

                Long productId = nuevoDetail.getProduct().getId();

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));

                nuevoDetail.setOrder(orderToUpdate);
                nuevoDetail.setProduct(product);
                nuevoDetail.setUnitPrice(product.getPrice());

                BigDecimal subtotal = product.getPrice()
                        .multiply(BigDecimal.valueOf(nuevoDetail.getQuantity()));

                nuevoDetail.setSubtotal(subtotal);

                nuevoTotal = nuevoTotal.add(subtotal);

                orderToUpdate.getDetails().add(nuevoDetail);
            }

            orderToUpdate.setTotalAmount(nuevoTotal);
        }

        return orderRepository.save(orderToUpdate);
    }

    @Override
    public void delete(Long id) {

        Order orderToDelete = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con id: " + id));

        if (orderToDelete.getCashBox().getEstado() != CashBoxStatus.OPEN) {
            throw new RuntimeException("No se puede eliminar una orden de una caja cerrada");
        }

        orderRepository.delete(orderToDelete);
    }

    @Override
    public List<Order> readAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> readByCashBox(Long cashBoxId) {
        return orderRepository.findByCashBoxId(cashBoxId);
    }

    @Override
    public List<Order> readByDate(String date) {

        LocalDate localDate = LocalDate.parse(date);

        LocalDateTime start = localDate.atStartOfDay();
        LocalDateTime end = localDate.plusDays(1).atStartOfDay();

        return orderRepository.findByCreationDateBetween(start, end);
    }

    @Override
    public BigDecimal sumTotalByDate(String date) {

        List<Order> orders = readByDate(date);

        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // --- MÉTODO AUXILIAR ---
    private void asignarClientePorDefecto(Order order) {
        order.setClienteId(1L);
        order.setCustomerName("Clientes Varios");
        order.setCustomerDocument("00000000");
    }
}