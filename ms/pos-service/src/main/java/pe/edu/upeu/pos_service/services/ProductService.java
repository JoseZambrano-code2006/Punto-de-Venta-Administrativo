package pe.edu.upeu.pos_service.services;

import pe.edu.upeu.pos_service.entity.Product;
import java.util.List;

public interface ProductService {

    Product create(Product product);

    Product readById(Long id);

    Product update(Product product, Long id);

    void delete(Long id);
    List<Product> readAll();

    List<Product> readByCategory(Long categoryId);

    List<Product> searchByName(String name);
}