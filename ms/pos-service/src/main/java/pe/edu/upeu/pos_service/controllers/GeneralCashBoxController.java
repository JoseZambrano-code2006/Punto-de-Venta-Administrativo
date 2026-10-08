package pe.edu.upeu.pos_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.pos_service.entity.GeneralCashBox;
import pe.edu.upeu.pos_service.services.GeneralCashBoxService;

import java.util.List;

@RestController
@RequestMapping(path = "cash-box")
@Tag(name = "Cash Box resources")
public class GeneralCashBoxController {

    private final GeneralCashBoxService cashBoxService;
    private final Logger log = LoggerFactory.getLogger(GeneralCashBoxController.class);

    public GeneralCashBoxController(GeneralCashBoxService cashBoxService) {
        this.cashBoxService = cashBoxService;
    }

    @Operation(summary = "Get all cash boxes")
    @GetMapping
    public ResponseEntity<List<GeneralCashBox>> getAll() {
        log.info("GET: all cash boxes");
        return ResponseEntity.ok(this.cashBoxService.readAll());
    }

    @Operation(summary = "Get a cash box by ID")
    @GetMapping(path = "{id}")
    public ResponseEntity<GeneralCashBox> get(@PathVariable Long id) {
        log.info("GET: cash box {}", id);
        return ResponseEntity.ok(this.cashBoxService.readById(id));
    }

    @Operation(summary = "Get the open cash box")
    @GetMapping(path = "open")
    public ResponseEntity<GeneralCashBox> getOpen() {
        log.info("GET: open cash box");
        return ResponseEntity.ok(this.cashBoxService.getOpenCashBox());
    }

    @Operation(summary = "Open a new cash box")
    @PostMapping(path = "open")
    public ResponseEntity<GeneralCashBox> open(@RequestBody GeneralCashBox cashBox) {
        log.info("POST: opening cash box");
        GeneralCashBox opened = this.cashBoxService.openCashBox(cashBox);
        return ResponseEntity.status(201).body(opened);
    }

    @Operation(summary = "Close a cash box")
    @PutMapping(path = "close/{id}")
    public ResponseEntity<GeneralCashBox> close(@PathVariable Long id) {
        log.info("PUT: closing cash box {}", id);
        return ResponseEntity.ok(this.cashBoxService.closeCashBox(id));
    }
}
