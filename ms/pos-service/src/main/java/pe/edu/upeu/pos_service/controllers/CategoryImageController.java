package pe.edu.upeu.pos_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.upeu.pos_service.dto.CategoryImageMetaDto;
import pe.edu.upeu.pos_service.entity.ImagenCat;
import pe.edu.upeu.pos_service.services.CategoryImageService;

import java.util.List;

@RestController
@RequestMapping(path = "category-image")
@Tag(name = "Category image resources")
public class CategoryImageController {

    private final CategoryImageService categoryImageService;
    private final Logger log = LoggerFactory.getLogger(CategoryImageController.class);

    public CategoryImageController(CategoryImageService categoryImageService) {
        this.categoryImageService = categoryImageService;
    }

    @Operation(summary = "Upload a category image")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryImageMetaDto> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "name", required = false) String name) {
        log.info("POST: uploading category image {}", file.getOriginalFilename());
        CategoryImageMetaDto saved = categoryImageService.upload(file, name);
        return ResponseEntity.status(201).body(saved);
    }

    @Operation(summary = "List category image metadata")
    @GetMapping
    public ResponseEntity<List<CategoryImageMetaDto>> list() {
        log.info("GET: all category image metadata");
        return ResponseEntity.ok(categoryImageService.listMetadata());
    }

    @Operation(summary = "Get category image binary")
    @GetMapping(path = "{id}")
    public ResponseEntity<byte[]> getBinary(@PathVariable Long id) {
        log.info("GET: category image binary {}", id);
        ImagenCat image = categoryImageService.readById(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getName() + "\"")
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .body(image.getData());
    }

    @Operation(summary = "Delete a category image")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE: category image {}", id);
        categoryImageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
