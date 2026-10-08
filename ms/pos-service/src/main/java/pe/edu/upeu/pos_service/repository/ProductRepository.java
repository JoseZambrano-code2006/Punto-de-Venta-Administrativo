package pe.edu.upeu.pos_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.pos_service.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Buscar productos por nombre (búsqueda exacta)
    Optional<Product> findByName(String name);

    // Verificar si existe un producto con ese nombre
    boolean existsByName(String name);

    //Verificar si existe un producto en una categoria
    boolean existsByCategoryId(Long categoryId);

    // Listar productos por categoría
    List<Product> findByCategoryId(Long categoryId);

    // Buscar productos que contengan texto en el nombre (tipo buscador)
    List<Product> findByNameContainingIgnoreCase(String name);

    boolean existsByImageId(Long imageId);
}
