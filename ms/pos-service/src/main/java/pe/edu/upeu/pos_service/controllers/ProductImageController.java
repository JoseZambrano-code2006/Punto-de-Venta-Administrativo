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
import pe.edu.upeu.pos_service.dto.ProductImageMetaDto;
import pe.edu.upeu.pos_service.entity.ProductImage;
import pe.edu.upeu.pos_service.services.ProductImageService;

import java.util.List;

@RestController
@RequestMapping(path = "product-image")
@Tag(name = "Product image resources")
public class ProductImageController {

    private final ProductImageService productImageService;
    private final Logger log = LoggerFactory.getLogger(ProductImageController.class);

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @Operation(summary = "Upload a product image")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductImageMetaDto> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "name", required = false) String name) {
        log.info("POST: uploading product image {}", file.getOriginalFilename());
        ProductImageMetaDto saved = productImageService.upload(file, name);
        return ResponseEntity.status(201).body(saved);
    }

    @Operation(summary = "List product image metadata")
    @GetMapping
    public ResponseEntity<List<ProductImageMetaDto>> list() {
        log.info("GET: all product image metadata");
        return ResponseEntity.ok(productImageService.listMetadata());
    }

    @Operation(summary = "Get product image binary")
    @GetMapping(path = "{id}")
    public ResponseEntity<byte[]> getBinary(@PathVariable Long id) {
        log.info("GET: product image binary {}", id);
        ProductImage image = productImageService.readById(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getName() + "\"")
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .body(image.getData());
    }

    @Operation(summary = "Delete a product image")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE: product image {}", id);
        productImageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
