package com.microservicos.icompras.pedidos.subcriber.representation;

import com.microservicos.icompras.pedidos.model.enums.StatusPedido;

public record AtualizacaoStatusPedidoRepresentation(
        Long codigo, StatusPedido status, String urlNotaFiscal,String codigoRastreio
) {
}
