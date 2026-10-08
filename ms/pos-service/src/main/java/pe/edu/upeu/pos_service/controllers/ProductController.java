package pe.edu.upeu.pos_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.pos_service.entity.Product;
import pe.edu.upeu.pos_service.services.ProductService;

import java.util.List;

@RestController
@RequestMapping(path = "product")
@Tag(name = "Product resources")
public class ProductController {

    private final ProductService productService;
    private final Logger log = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get all products")
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        log.info("GET: all products");
        return ResponseEntity.ok(this.productService.readAll());
    }

    @Operation(summary = "Get a product by ID")
    @GetMapping(path = "{id}")
    public ResponseEntity<Product> get(@PathVariable Long id) {
        log.info("GET: product {}", id);
        return ResponseEntity.ok(this.productService.readById(id));
    }

    @Operation(summary = "Get products by category")
    @GetMapping(path = "category/{categoryId}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable Long categoryId) {
        log.info("GET: products by category {}", categoryId);
        return ResponseEntity.ok(this.productService.readByCategory(categoryId));
    }

    @Operation(summary = "Search products by name")
    @GetMapping(path = "search")
    public ResponseEntity<List<Product>> searchByName(@RequestParam String name) {
        log.info("GET: searching products by name {}", name);
        return ResponseEntity.ok(this.productService.searchByName(name));
    }

    @Operation(summary = "Create a new product")
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        log.info("POST: creating product {}", product.getName());
        Product savedProduct = this.productService.create(product);
        return ResponseEntity.status(201).body(savedProduct);
    }

    @Operation(summary = "Update a product")
    @PutMapping(path = "{id}")
    public ResponseEntity<Product> put(@RequestBody Product product, @PathVariable Long id) {
        log.info("PUT: updating product {}", id);
        return ResponseEntity.ok(this.productService.update(product, id));
    }

    @Operation(summary = "Delete a product")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE: product {}", id);
        this.productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}