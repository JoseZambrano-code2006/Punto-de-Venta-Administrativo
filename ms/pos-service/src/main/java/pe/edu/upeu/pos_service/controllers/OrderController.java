package pe.edu.upeu.pos_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.pos_service.entity.Order;
import pe.edu.upeu.pos_service.services.OrderService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(path = "order")
@Tag(name = "Order resources")
public class OrderController {

    private final OrderService orderService;
    private final Logger log = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Get all orders")
    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        log.info("GET: all orders");
        return ResponseEntity.ok(this.orderService.readAll());
    }

    @Operation(summary = "Get an order by ID")
    @GetMapping(path = "{id}")
    public ResponseEntity<Order> get(@PathVariable Long id) {
        log.info("GET: order {}", id);
        return ResponseEntity.ok(this.orderService.readById(id));
    }

    @Operation(summary = "Get orders by cash box")
    @GetMapping(path = "cash-box/{cashBoxId}")
    public ResponseEntity<List<Order>> getByCashBox(@PathVariable Long cashBoxId) {
        log.info("GET: orders by cash box {}", cashBoxId);
        return ResponseEntity.ok(this.orderService.readByCashBox(cashBoxId));
    }

    @Operation(summary = "Create a new order with details")
    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order order) {
        // Ajuste aquí: Logueamos el clienteId que viene en la petición
        log.info("POST: creating order for clienteId {}", order.getClienteId());
        Order savedOrder = this.orderService.create(order);
        return ResponseEntity.status(201).body(savedOrder);
    }

    @Operation(summary = "Update an order with details")
    @PutMapping(path = "{id}")
    public ResponseEntity<Order> put(@RequestBody Order order, @PathVariable Long id) {
        log.info("PUT: updating order {} for clienteId {}", id, order.getClienteId());
        return ResponseEntity.ok(this.orderService.update(order, id));
    }

    @Operation(summary = "Delete an order")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE: order {}", id);
        this.orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get orders by date")
    @GetMapping(path = "report/list")
    public ResponseEntity<List<Order>> getOrdersByDate(@RequestParam String date) {
        log.info("GET: orders for date {}", date);
        return ResponseEntity.ok(this.orderService.readByDate(date));
    }

    @Operation(summary = "Get total amount by date")
    @GetMapping(path = "report/total")
    public ResponseEntity<BigDecimal> getTotalByDate(@RequestParam String date) {
        log.info("GET: total amount for date {}", date);
        return ResponseEntity.ok(this.orderService.sumTotalByDate(date));
    }
}