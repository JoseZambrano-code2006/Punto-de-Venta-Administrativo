package pe.edu.upeu.ms_clientes.service;

import org.springframework.stereotype.Service;
import pe.edu.upeu.ms_clientes.entity.Cliente;
import pe.edu.upeu.ms_clientes.repository.ClienteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService{

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public Optional<Cliente> obtenerPorDocumento(String numeroDocumento) {
        return clienteRepository.findByNumeroDocumento(numeroDocumento);
    }

    @Override
    public List<Cliente> obtenerPorNombre(String nombre) {
        return clienteRepository.findByNombreCompletoContainingIgnoreCase(nombre);
    }
}
