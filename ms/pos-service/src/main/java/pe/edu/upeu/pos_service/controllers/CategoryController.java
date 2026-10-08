package pe.edu.upeu.pos_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.pos_service.entity.Category;
import pe.edu.upeu.pos_service.services.CategoryService;

import java.util.List;

@RestController
@RequestMapping(path = "category")
@Tag(name = "Category resources")
public class CategoryController {

    private final CategoryService categoryService;
    private final Logger log = LoggerFactory.getLogger(CategoryController.class);

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Get all categories")
    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        log.info("GET: all categories");
        return ResponseEntity.ok(this.categoryService.readAll());
    }

    @Operation(summary = "Get a category by ID")
    @GetMapping(path = "{id}")
    public ResponseEntity<Category> get(@PathVariable Long id) {
        log.info("GET: category {}", id);
        return ResponseEntity.ok(this.categoryService.readById(id));
    }

    @Operation(summary = "Create a new category")
    @PostMapping
    public ResponseEntity<Category> create(@RequestBody Category category) {
        log.info("POST: creating category {}", category.getName());
        Category savedCategory = this.categoryService.create(category);
        return ResponseEntity.status(201).body(savedCategory);
    }

    @Operation(summary = "Update a category")
    @PutMapping(path = "{id}")
    public ResponseEntity<Category> put(@RequestBody Category category, @PathVariable Long id) {
        log.info("PUT: updating category {}", id);
        return ResponseEntity.ok(this.categoryService.update(category, id));
    }

    @Operation(summary = "Delete a category")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE: category {}", id);
        this.categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
