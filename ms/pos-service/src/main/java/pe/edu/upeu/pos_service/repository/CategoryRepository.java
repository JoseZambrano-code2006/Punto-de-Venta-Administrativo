package pe.edu.upeu.pos_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.pos_service.entity.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Buscar categoría por nombre
    Optional<Category> findByName(String name);

    // Verificar si ya existe una categoría con ese nombre
    boolean existsByName(String name);

    boolean existsByImagenCatId(Long imagenCatId);
}
