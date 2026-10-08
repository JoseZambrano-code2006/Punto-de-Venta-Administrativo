package pe.edu.upeu.ms_clientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.ms_clientes.entity.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Búsqueda exacta por documento (DNI o RUC)
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);

    // Búsqueda parcial por nombre (Ignorando mayúsculas y minúsculas)
    List<Cliente> findByNombreCompletoContainingIgnoreCase(String nombreCompleto);

}
