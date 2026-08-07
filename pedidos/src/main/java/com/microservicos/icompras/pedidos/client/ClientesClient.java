package com.microservicos.icompras.pedidos.client;

import com.microservicos.icompras.pedidos.client.representation.ClienteRepresentation;
import com.microservicos.icompras.pedidos.client.representation.ProdutoRepresentation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clientes",url = "${icompras.pedidos.clients.clientes.url}")
public interface ClientesClient {

    @GetMapping("{codigo}")
    ResponseEntity<ClienteRepresentation> obterDados(@PathVariable("codigo") Long codigo);
}
