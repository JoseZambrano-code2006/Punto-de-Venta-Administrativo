package pe.edu.upeu.pos_service.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pe.edu.upeu.pos_service.entity.Category;
import pe.edu.upeu.pos_service.entity.ImagenCat;
import pe.edu.upeu.pos_service.repository.CategoryRepository;
import pe.edu.upeu.pos_service.repository.ImagenCatRepository;
import pe.edu.upeu.pos_service.repository.ProductRepository;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ImagenCatRepository imagenCatRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               ProductRepository productRepository,
                               ImagenCatRepository imagenCatRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.imagenCatRepository = imagenCatRepository;
    }

    @Override
    public Category create(Category category) {

        // Validar que no exista categoría duplicada
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("La categoría ya existe");
        }

        resolveImagenCat(category);
        return categoryRepository.save(category);
    }

    @Override
    public Category readById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + id));
    }

    @Override
    public Category update(Category category, Long id) {

        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + id));

        existing.setName(category.getName());
        existing.setImageUrl(category.getImageUrl());
        resolveImagenCatForUpdate(existing, category);

        return categoryRepository.save(existing);
    }

    @Override
    public void delete(Long id) {

        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + id));

        // Validar si existen productos relacionados
        if (productRepository.existsByCategoryId(id)) {
            throw new RuntimeException(
                    "No se puede eliminar la categoría porque tiene productos asociados"
            );
        }

        categoryRepository.delete(existing);
    }

    @Override
    public List<Category> readAll() {
        return categoryRepository.findAll();
    }

    private void resolveImagenCat(Category category) {
        if (category.getImagenCat() == null || category.getImagenCat().getId() == null) {
            category.setImagenCat(null);
            return;
        }

        Long imagenCatId = category.getImagenCat().getId();
        ImagenCat imagenCat = imagenCatRepository.findById(imagenCatId)
                .orElseThrow(() -> new RuntimeException("Imagen de categoría no encontrada con id: " + imagenCatId));
        category.setImagenCat(imagenCat);
    }

    private void resolveImagenCatForUpdate(Category existing, Category incoming) {
        if (incoming.getImagenCat() == null) {
            return;
        }

        if (incoming.getImagenCat().getId() == null) {
            existing.setImagenCat(null);
            return;
        }

        Long imagenCatId = incoming.getImagenCat().getId();
        ImagenCat imagenCat = imagenCatRepository.findById(imagenCatId)
                .orElseThrow(() -> new RuntimeException("Imagen de categoría no encontrada con id: " + imagenCatId));
        existing.setImagenCat(imagenCat);
    }
}
