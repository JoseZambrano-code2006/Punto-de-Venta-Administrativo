package pe.edu.upeu.pos_service.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.upeu.pos_service.dto.ProductImageMetaDto;
import pe.edu.upeu.pos_service.entity.ProductImage;
import pe.edu.upeu.pos_service.repository.ProductImageRepository;
import pe.edu.upeu.pos_service.repository.ProductRepository;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private static final long MAX_SIZE_BYTES = 2 * 1024 * 1024;

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    public ProductImageServiceImpl(ProductImageRepository productImageRepository,
                                   ProductRepository productRepository) {
        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
    }

    @Override
    public ProductImageMetaDto upload(MultipartFile file, String name) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("El archivo de imagen es obligatorio");
        }

        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new RuntimeException("La imagen no puede superar 2 MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Solo se permiten archivos de imagen");
        }

        ProductImage image = new ProductImage();
        image.setName((name != null && !name.isBlank()) ? name.trim() : file.getOriginalFilename());
        image.setContentType(contentType);

        try {
            image.setData(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo de imagen", e);
        }

        ProductImage saved = productImageRepository.save(image);
        return toMeta(saved);
    }

    @Override
    public List<ProductImageMetaDto> listMetadata() {
        return productImageRepository.findAll().stream()
                .map(this::toMeta)
                .toList();
    }

    @Override
    public ProductImage readById(Long id) {
        return productImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + id));
    }

    @Override
    public void delete(Long id) {
        if (productRepository.existsByImageId(id)) {
            throw new RuntimeException("No se puede eliminar la imagen porque está asociada a productos");
        }

        ProductImage image = readById(id);
        productImageRepository.delete(image);
    }

    private ProductImageMetaDto toMeta(ProductImage image) {
        return new ProductImageMetaDto(
                image.getId(),
                image.getName(),
                image.getContentType(),
                image.getCreationDate()
        );
    }
}
