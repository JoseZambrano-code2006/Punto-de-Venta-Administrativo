package pe.edu.upeu.pos_service.services;

import pe.edu.upeu.pos_service.entity.Account;

import java.util.List;

public interface AccountService {

    Account create(Account account);

    Account readById(Long id);

    Account update(Account account, Long id);

    void delete(Long id);

    List<Account> readAll();

    Account findByMail(String mail);
}
