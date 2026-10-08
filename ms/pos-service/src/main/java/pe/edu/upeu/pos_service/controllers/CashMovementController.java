package pe.edu.upeu.pos_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.pos_service.entity.CashMovement;
import pe.edu.upeu.pos_service.services.CashMovementService;

import java.util.List;

@RestController
@RequestMapping(path = "cash-movement")
@Tag(name = "Cash Movement resources")
public class CashMovementController {

    private final CashMovementService cashMovementService;
    private final Logger log = LoggerFactory.getLogger(CashMovementController.class);

    public CashMovementController(CashMovementService cashMovementService) {
        this.cashMovementService = cashMovementService;
    }

    @Operation(summary = "Get all cash movements")
    @GetMapping
    public ResponseEntity<List<CashMovement>> getAll() {
        log.info("GET: all cash movements");
        return ResponseEntity.ok(this.cashMovementService.readAll());
    }

    @Operation(summary = "Get a cash movement by ID")
    @GetMapping(path = "{id}")
    public ResponseEntity<CashMovement> get(@PathVariable Long id) {
        log.info("GET: cash movement {}", id);
        return ResponseEntity.ok(this.cashMovementService.readById(id));
    }

    @Operation(summary = "Get movements by cash box")
    @GetMapping(path = "cash-box/{cashBoxId}")
    public ResponseEntity<List<CashMovement>> getByCashBox(@PathVariable Long cashBoxId) {
        log.info("GET: movements for cash box {}", cashBoxId);
        return ResponseEntity.ok(this.cashMovementService.readByCashBox(cashBoxId));
    }

    @Operation(summary = "Create a new cash movement (INCOME / EXPENSE)")
    @PostMapping
    public ResponseEntity<CashMovement> create(@RequestBody CashMovement cashMovement) {
        log.info("POST: creating cash movement {}", cashMovement.getType());
        CashMovement saved = this.cashMovementService.create(cashMovement);
        return ResponseEntity.status(201).body(saved);
    }

    @Operation(summary = "Delete a cash movement")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE: cash movement {}", id);
        this.cashMovementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
