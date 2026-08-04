package com.microservicos.icompras.pedidos.controller.dto;

import com.microservicos.icompras.pedidos.model.enums.TipoPagamento;

public record DadosPagamentoDTO(String dados, TipoPagamento tipoPagamento) {
}
