package pe.edu.upeu.pos_service.services;

import org.springframework.web.multipart.MultipartFile;
import pe.edu.upeu.pos_service.dto.ProductImageMetaDto;
import pe.edu.upeu.pos_service.entity.ProductImage;

import java.util.List;

public interface ProductImageService {

    ProductImageMetaDto upload(MultipartFile file, String name);

    List<ProductImageMetaDto> listMetadata();

    ProductImage readById(Long id);

    void delete(Long id);
}
