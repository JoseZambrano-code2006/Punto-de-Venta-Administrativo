package pe.edu.upeu.pos_service.services;

import org.springframework.stereotype.Service;
import pe.edu.upeu.pos_service.entity.Account;
import pe.edu.upeu.pos_service.repository.AccountRepository;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account create(Account account) {

        // Validamos que el correo no se repita
        if (accountRepository.existsByMail(account.getMail())) {
            throw new RuntimeException("Ya existe una cuenta con ese correo");
        }

        return accountRepository.save(account);
    }

    @Override
    public Account readById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con id: " + id));
    }

    @Override
    public Account update(Account account, Long id) {

        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con id: " + id));

        existingAccount.setName(account.getName());
        existingAccount.setMail(account.getMail());
        if (account.getPassword() != null && !account.getPassword().isBlank()) {
            existingAccount.setPassword(account.getPassword());
        }

        return accountRepository.save(existingAccount);
    }

    @Override
    public void delete(Long id) {

        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con id: " + id));

        accountRepository.delete(existingAccount);
    }

    @Override
    public List<Account> readAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findByMail(String mail) {
        return accountRepository.findByMail(mail)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con correo: " + mail));
    }
}
