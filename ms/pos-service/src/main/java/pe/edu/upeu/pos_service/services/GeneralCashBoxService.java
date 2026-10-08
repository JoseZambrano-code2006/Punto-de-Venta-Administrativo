package pe.edu.upeu.pos_service.services;

import pe.edu.upeu.pos_service.entity.GeneralCashBox;
import java.util.List;


public interface GeneralCashBoxService {

    GeneralCashBox openCashBox(GeneralCashBox cashBox);

    GeneralCashBox closeCashBox(Long id);

    GeneralCashBox readById(Long id);

    List<GeneralCashBox> readAll();

    GeneralCashBox getOpenCashBox();

}
