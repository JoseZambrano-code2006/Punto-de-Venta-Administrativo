package pe.edu.upeu.ms_clientes.service;

import pe.edu.upeu.ms_clientes.entity.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteService {

    Optional<Cliente> obtenerPorId(Long id);
    List<Cliente> listarTodos();
    Cliente guardar(Cliente cliente);


    Optional<Cliente> obtenerPorDocumento(String numeroDocumento);
    List<Cliente> obtenerPorNombre(String nombre);
}
