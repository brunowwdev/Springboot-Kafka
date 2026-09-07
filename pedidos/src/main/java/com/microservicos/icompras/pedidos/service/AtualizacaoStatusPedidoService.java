package com.microservicos.icompras.pedidos.service;

import com.microservicos.icompras.pedidos.Repository.PedidoRepository;
import com.microservicos.icompras.pedidos.model.Pedido;
import com.microservicos.icompras.pedidos.model.enums.StatusPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtualizacaoStatusPedidoService {

    private final PedidoRepository pedidoRepository;

    public void atualizarStatus(Long codigo, StatusPedido status, String urlNotaFiscal, String codigoRastreio) {


    }
}
