package io.github.curso.icompras.faturamento.model;

import java.math.BigDecimal;

public record ItemPedido(Long codigo, String descricao,
                         BigDecimal valorUnitario,Integer quantidade) {

    public BigDecimal getTotal() {
        return BigDecimal.valueOf(this.quantidade).multiply(this.valorUnitario);
    }
}
