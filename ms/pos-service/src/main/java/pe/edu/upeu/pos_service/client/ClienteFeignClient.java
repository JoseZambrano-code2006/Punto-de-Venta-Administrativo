package pe.edu.upeu.pos_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.upeu.pos_service.dto.ClienteDTO;

@FeignClient(name = "ms-clientes", path = "/clientes-service/api/clientes")
public interface ClienteFeignClient {

    @GetMapping("/{id}")
    ResponseEntity<ClienteDTO> obtenerClientePorId(@PathVariable("id") Long id);
}