package com.microservicos.icompras.pedidos.service;

import com.microservicos.icompras.pedidos.Repository.PedidoRepository;
import com.microservicos.icompras.pedidos.model.enums.StatusPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AtualizacaoStatusPedidoService {

    private final PedidoRepository pedidoRepository;

    @Transactional
    public void atualizarStatus(Long codigo, StatusPedido status, String urlNotaFiscal, String codigoRastreio) {

        pedidoRepository.findById(codigo).ifPresent( pedido -> {
            pedido.setStatus(status);
            pedido.setUrlNotaFiscal(urlNotaFiscal);
            pedido.setCodigoRastreio(codigoRastreio);
        } );
    }
}
