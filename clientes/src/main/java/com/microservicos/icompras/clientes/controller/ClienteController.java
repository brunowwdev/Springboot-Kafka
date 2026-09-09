package com.microservicos.icompras.clientes.controller;

import com.microservicos.icompras.clientes.model.Cliente;
import com.microservicos.icompras.clientes.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @DeleteMapping("{codigo}")
    public ResponseEntity<Void> deleter(@PathVariable("codigo") Long codigo){
        var cliente = clienteService.obterPorCodigo(codigo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente Inexistente"
                ));
        clienteService.deletar(cliente);
        return ResponseEntity.noContent().build();
    }
}
