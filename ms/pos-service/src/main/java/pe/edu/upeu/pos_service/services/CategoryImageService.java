package pe.edu.upeu.pos_service.services;

import org.springframework.web.multipart.MultipartFile;
import pe.edu.upeu.pos_service.dto.CategoryImageMetaDto;
import pe.edu.upeu.pos_service.entity.ImagenCat;

import java.util.List;

public interface CategoryImageService {

    CategoryImageMetaDto upload(MultipartFile file, String name);

    List<CategoryImageMetaDto> listMetadata();

    ImagenCat readById(Long id);

    void delete(Long id);
}
