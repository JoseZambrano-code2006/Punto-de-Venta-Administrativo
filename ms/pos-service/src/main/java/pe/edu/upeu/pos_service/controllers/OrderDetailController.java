package pe.edu.upeu.pos_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.pos_service.entity.OrderDetail;
import pe.edu.upeu.pos_service.services.OrderDetailService;

import java.util.List;

@RestController
@RequestMapping(path = "order-detail")
@Tag(name = "Order Detail resources")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;
    private final Logger log = LoggerFactory.getLogger(OrderDetailController.class);

    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @Operation(summary = "Get all order details")
    @GetMapping
    public ResponseEntity<List<OrderDetail>> getAll() {
        log.info("GET: all order details");
        return ResponseEntity.ok(this.orderDetailService.readAll());
    }

    @Operation(summary = "Get an order detail by ID")
    @GetMapping(path = "{id}")
    public ResponseEntity<OrderDetail> get(@PathVariable Long id) {
        log.info("GET: order detail {}", id);
        return ResponseEntity.ok(this.orderDetailService.readById(id));
    }

    @Operation(summary = "Get details by order")
    @GetMapping(path = "order/{orderId}")
    public ResponseEntity<List<OrderDetail>> getByOrder(@PathVariable Long orderId) {
        log.info("GET: details for order {}", orderId);
        return ResponseEntity.ok(this.orderDetailService.readByOrder(orderId));
    }

    @Operation(summary = "Get details by product")
    @GetMapping(path = "product/{productId}")
    public ResponseEntity<List<OrderDetail>> getByProduct(@PathVariable Long productId) {
        log.info("GET: details for product {}", productId);
        return ResponseEntity.ok(this.orderDetailService.readByProduct(productId));
    }

    @Operation(summary = "Create a new order detail")
    @PostMapping
    public ResponseEntity<OrderDetail> create(@RequestBody OrderDetail orderDetail) {
        log.info("POST: creating order detail");
        OrderDetail saved = this.orderDetailService.create(orderDetail);
        return ResponseEntity.status(201).body(saved);
    }

    @Operation(summary = "Delete an order detail")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE: order detail {}", id);
        this.orderDetailService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
