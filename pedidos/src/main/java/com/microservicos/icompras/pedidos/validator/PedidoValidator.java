package com.microservicos.icompras.pedidos.validator;

import com.microservicos.icompras.pedidos.client.ProdutosClient;
import com.microservicos.icompras.pedidos.client.representation.ProdutoRepresentation;
import com.microservicos.icompras.pedidos.model.Pedido;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class PedidoValidator {

    private final ProdutosClient produtosClient;

    public void validar(Pedido pedido){
        List<Long> codigosProdutos = pedido.getItens().stream().map(i -> i.getCodigoProduto()).toList();
        codigosProdutos.forEach(codigoProduto -> {
            ResponseEntity<ProdutoRepresentation> response = produtosClient.obterDados(codigoProduto);
            ProdutoRepresentation produto = response.getBody();
        });
    }
}
