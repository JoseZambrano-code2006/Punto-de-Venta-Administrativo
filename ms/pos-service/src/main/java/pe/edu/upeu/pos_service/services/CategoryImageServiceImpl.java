package pe.edu.upeu.pos_service.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.upeu.pos_service.dto.CategoryImageMetaDto;
import pe.edu.upeu.pos_service.entity.ImagenCat;
import pe.edu.upeu.pos_service.repository.CategoryRepository;
import pe.edu.upeu.pos_service.repository.ImagenCatRepository;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class CategoryImageServiceImpl implements CategoryImageService {

    private static final long MAX_SIZE_BYTES = 2 * 1024 * 1024;

    private final ImagenCatRepository imagenCatRepository;
    private final CategoryRepository categoryRepository;

    public CategoryImageServiceImpl(ImagenCatRepository imagenCatRepository,
                                    CategoryRepository categoryRepository) {
        this.imagenCatRepository = imagenCatRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryImageMetaDto upload(MultipartFile file, String name) {
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

        ImagenCat image = new ImagenCat();
        image.setName((name != null && !name.isBlank()) ? name.trim() : file.getOriginalFilename());
        image.setContentType(contentType);

        try {
            image.setData(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo de imagen", e);
        }

        ImagenCat saved = imagenCatRepository.save(image);
        return toMeta(saved);
    }

    @Override
    public List<CategoryImageMetaDto> listMetadata() {
        return imagenCatRepository.findAll().stream()
                .map(this::toMeta)
                .toList();
    }

    @Override
    public ImagenCat readById(Long id) {
        return imagenCatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen de categoría no encontrada con id: " + id));
    }

    @Override
    public void delete(Long id) {
        if (categoryRepository.existsByImagenCatId(id)) {
            throw new RuntimeException("No se puede eliminar la imagen porque está asociada a categorías");
        }

        ImagenCat image = readById(id);
        imagenCatRepository.delete(image);
    }

    private CategoryImageMetaDto toMeta(ImagenCat image) {
        return new CategoryImageMetaDto(
                image.getId(),
                image.getName(),
                image.getContentType(),
                image.getCreationDate()
        );
    }
}
