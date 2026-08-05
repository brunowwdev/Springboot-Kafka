package com.microservicos.icompras.pedidos.service;

import com.microservicos.icompras.pedidos.Repository.ItemPedidoRepository;
import com.microservicos.icompras.pedidos.Repository.PedidoRepository;
import com.microservicos.icompras.pedidos.client.ServicoBancarioClient;
import com.microservicos.icompras.pedidos.controller.dto.NovoPedidoDTO;
import com.microservicos.icompras.pedidos.model.Pedido;
import com.microservicos.icompras.pedidos.validator.PedidoValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private  final ItemPedidoRepository itemPedidoRepository;
    private final PedidoValidator validator;
    private final ServicoBancarioClient servicoBancarioClient;

    @Transactional
    public Pedido criarPedido(Pedido pedido) {
        validator.validar(pedido);
        realizarPersistencia(pedido);
        enviarSolicitacaoPagamento(pedido);
        return pedido;
    }

    private void enviarSolicitacaoPagamento(Pedido pedido) {
        var chavePagamento = servicoBancarioClient.solicitarPagamento(pedido);
        pedido.setChavePagamento(chavePagamento);
    }

    private void realizarPersistencia(Pedido pedido) {
        pedidoRepository.save(pedido);
        itemPedidoRepository.saveAll(pedido.getItens());
    }

}
