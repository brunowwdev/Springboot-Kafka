package com.microservicos.icompras.pedidos.controller;

import com.microservicos.icompras.pedidos.Repository.PedidoRepository;
import com.microservicos.icompras.pedidos.controller.dto.NovoPedidoDTO;
import com.microservicos.icompras.pedidos.controller.mappers.PedidoMapper;
import com.microservicos.icompras.pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoMapper pedidoMapper;

    @PostMapping
    public ResponseEntity<Object> criar(@RequestBody NovoPedidoDTO dto) {
        var pedido = pedidoMapper.map(dto);
        var novoPedido = pedidoService.criarPedido(pedido);
        return ResponseEntity.ok(novoPedido.getCodigo());
    }
}
