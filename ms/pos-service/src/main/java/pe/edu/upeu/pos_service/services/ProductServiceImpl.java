package pe.edu.upeu.pos_service.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pe.edu.upeu.pos_service.entity.Category;
import pe.edu.upeu.pos_service.entity.Product;
import pe.edu.upeu.pos_service.entity.ProductImage;
import pe.edu.upeu.pos_service.repository.CategoryRepository;
import pe.edu.upeu.pos_service.repository.OrderDetailRepository;
import pe.edu.upeu.pos_service.repository.ProductImageRepository;
import pe.edu.upeu.pos_service.repository.ProductRepository;

import java.util.List;


@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductImageRepository productImageRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              OrderDetailRepository orderDetailRepository,
                              ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productImageRepository = productImageRepository;
    }

    @Override
    public Product create(Product product) {

        // Validar que no exista un producto con el mismo nombre
        if (productRepository.existsByName(product.getName())) {
            throw new RuntimeException("Ya existe un producto con ese nombre");
        }

        // Validar que la categoría exista
        Long categoryId = product.getCategory().getId();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoryId));

        product.setCategory(category);
        resolveImage(product);

        return productRepository.save(product);
    }

    @Override
    public Product readById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }

    @Override
    public Product update(Product product, Long id) {

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        // Validar categoría si viene en el request
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Long categoryId = product.getCategory().getId();

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoryId));

            existing.setCategory(category);
        }

        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setImageUrl(product.getImageUrl());
        resolveImageForUpdate(existing, product);

        return productRepository.save(existing);
    }

    @Override
    public void delete(Long id) {

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        if (orderDetailRepository.existsByProductId(id)) {
            throw new RuntimeException("No se puede eliminar el producto porque está asociado a órdenes");
        }

        productRepository.delete(existing);
    }

    @Override
    public List<Product> readAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> readByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    private void resolveImage(Product product) {
        if (product.getImage() == null || product.getImage().getId() == null) {
            product.setImage(null);
            return;
        }

        Long imageId = product.getImage().getId();
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + imageId));
        product.setImage(image);
    }

    private void resolveImageForUpdate(Product existing, Product incoming) {
        if (incoming.getImage() == null) {
            return;
        }

        if (incoming.getImage().getId() == null) {
            existing.setImage(null);
            return;
        }

        Long imageId = incoming.getImage().getId();
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + imageId));
        existing.setImage(image);
    }
}