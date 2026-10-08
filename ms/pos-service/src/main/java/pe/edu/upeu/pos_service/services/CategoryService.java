package pe.edu.upeu.pos_service.services;

import pe.edu.upeu.pos_service.entity.Category;
import java.util.List;

public interface CategoryService {

    Category create(Category category);

    Category readById(Long id);

    Category update(Category category, Long id);

    void delete(Long id);

    List<Category> readAll();

}
