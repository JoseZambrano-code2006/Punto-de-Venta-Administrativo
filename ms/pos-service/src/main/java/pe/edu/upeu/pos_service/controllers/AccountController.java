package pe.edu.upeu.pos_service.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.pos_service.entity.Account;
import pe.edu.upeu.pos_service.services.AccountService;

import java.util.List;

@RestController
@RequestMapping(path = "account")
@Tag(name = "Account resources")
public class AccountController {

    private final AccountService accountService;
    private final Logger log = LoggerFactory.getLogger(AccountController.class);

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Get all accounts")
    @GetMapping
    public ResponseEntity<List<Account>> getAll() {
        log.info("GET: all accounts");
        return ResponseEntity.ok(this.accountService.readAll());
    }

    @Operation(summary = "Get an account by ID")
    @GetMapping(path = "{id}")
    public ResponseEntity<Account> get(@PathVariable Long id) {
        log.info("GET: account {}", id);
        return ResponseEntity.ok(this.accountService.readById(id));
    }

    @Operation(summary = "Get an account by mail")
    @GetMapping(path = "mail/{mail}")
    public ResponseEntity<Account> getByMail(@PathVariable String mail) {
        log.info("GET: account by mail {}", mail);
        return ResponseEntity.ok(this.accountService.findByMail(mail));
    }

    @Operation(summary = "Create a new account")
    @PostMapping
    public ResponseEntity<Account> create(@RequestBody Account account) {
        log.info("POST: creating account {}", account.getMail());
        Account savedAccount = this.accountService.create(account);
        return ResponseEntity.status(201).body(savedAccount);
    }

    @Operation(summary = "Update an account")
    @PutMapping(path = "{id}")
    public ResponseEntity<Account> put(@RequestBody Account account, @PathVariable Long id) {
        log.info("PUT: updating account {}", id);
        return ResponseEntity.ok(this.accountService.update(account, id));
    }

    @Operation(summary = "Delete an account")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE: account {}", id);
        this.accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
