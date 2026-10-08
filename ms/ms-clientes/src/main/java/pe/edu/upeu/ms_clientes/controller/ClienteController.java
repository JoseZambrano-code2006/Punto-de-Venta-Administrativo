package pe.edu.upeu.ms_clientes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.ms_clientes.entity.Cliente;
import pe.edu.upeu.ms_clientes.service.ClienteService;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    // Inyección de dependencias mediante el constructor
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable Long id) {
        return clienteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        return ResponseEntity.ok(clienteService.guardar(cliente));
    }

    // Endpoint para buscar por Documento (Ej: GET /api/clientes/documento/72345678)
    @GetMapping("/documento/{numero}")
    public ResponseEntity<Cliente> obtenerPorDocumento(@PathVariable String numero) {
        return clienteService.obtenerPorDocumento(numero)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para buscar por Nombre parcial (Ej: GET /api/clientes/buscar?nombre=juan)
    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarPorNombre(@RequestParam String nombre) {
        List<Cliente> clientes = clienteService.obtenerPorNombre(nombre);
        if(clientes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(clientes);
    }
}