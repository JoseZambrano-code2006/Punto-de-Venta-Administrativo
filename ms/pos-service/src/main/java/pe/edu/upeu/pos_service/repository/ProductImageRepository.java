package pe.edu.upeu.pos_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.pos_service.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
