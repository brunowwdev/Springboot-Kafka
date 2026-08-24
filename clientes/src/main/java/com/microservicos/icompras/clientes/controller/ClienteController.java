package com.microservicos.icompras.clientes.controller;

import com.microservicos.icompras.clientes.model.Cliente;
import com.microservicos.icompras.clientes.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> salvar(@RequestBody Cliente cliente){
        return  ResponseEntity.ok(clienteService.salvar(cliente));
    }

    @GetMapping("{codigo}")
    public ResponseEntity<Cliente> obterPorCodigo(@PathVariable("codigo") Long codigo){
        return clienteService.obterPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
