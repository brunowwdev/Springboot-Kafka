package io.github.curso.icompras.faturamento.publisher.representation;


public record AtualizacaoStatusPedido(
        Long codigo, StatusPedido status, String urlNotaFiscal) {
}
