package com.microservicos.icompras.pedidos.validator;

import com.microservicos.icompras.pedidos.client.ClientesClient;
import com.microservicos.icompras.pedidos.client.ProdutosClient;
import com.microservicos.icompras.pedidos.client.representation.ClienteRepresentation;
import com.microservicos.icompras.pedidos.client.representation.ProdutoRepresentation;
import com.microservicos.icompras.pedidos.model.ItemPedido;
import com.microservicos.icompras.pedidos.model.Pedido;
import com.microservicos.icompras.pedidos.model.exception.ValidationException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoValidator {

    private final ProdutosClient produtosClient;
    private final ClientesClient clientesClient;

    public void validar(Pedido pedido){
        Long codigoCliente = pedido.getCodigoCliente();
        validarCliente(codigoCliente);
        pedido.getItens().forEach(this::validarItem);
    }

    private void validarCliente(Long codigoCliente){
        try {
            ResponseEntity<ClienteRepresentation> response = clientesClient.obterDados(codigoCliente);
            ClienteRepresentation cliente = response.getBody();
            log.info("Cliente de codigo {} encontrado: {}", cliente.codigo(),cliente.nome());
        }catch (FeignException.NotFound e){
            var message = String.format("Cliente de codigo %d não encontrado", codigoCliente);
            throw new ValidationException("codigoCliente",message);
        }
    }

    private void validarItem(ItemPedido item){
        try{
            var response = produtosClient.obterDados(item.getCodigoProduto());
            ProdutoRepresentation produto = response.getBody();
            log.info("Produto de codigo {} encontrado: {}", produto.codigo(),produto.nome());
        } catch (FeignException.NotFound e){
            var message = String.format("Produto de codigo %d não encontrado", item.getCodigoProduto());
            throw new ValidationException("codigoProduto",message);
        }
    }
}
