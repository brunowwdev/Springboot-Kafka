package com.microservicos.icompras.pedidos.controller.mappers;

import com.microservicos.icompras.pedidos.controller.dto.ItemPedidoDTO;
import com.microservicos.icompras.pedidos.controller.dto.NovoPedidoDTO;
import com.microservicos.icompras.pedidos.model.ItemPedido;
import com.microservicos.icompras.pedidos.model.Pedido;
import com.microservicos.icompras.pedidos.model.enums.StatusPedido;
import org.jspecify.annotations.NonNull;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    ItemPedidoMapper ITEM_PEDIDO_MAPPER = Mappers.getMapper(ItemPedidoMapper.class);


    // Ele lê a lista itens do DTO, converte cada item para a entidade ItemPedido
    // usando o método mapItens e coloca a nova lista no atributo itens do Pedido.

    @Mapping(source = "itens",target = "itens", qualifiedByName = "mapItens")
    @Mapping(source = "dadosPagamento", target = "dadosPagamento")
    Pedido map(NovoPedidoDTO dto);

    @Named("mapItens")
    default List<ItemPedido> mapItens(List<ItemPedidoDTO> dtos) {
        return dtos.stream().map(ITEM_PEDIDO_MAPPER::map).toList();
    }

    @AfterMapping
    default void  afterMapping(@MappingTarget Pedido pedido) {
        pedido.setStatus(StatusPedido.REALIZADO);
        pedido.setDataPedido(LocalDateTime.now());

        var total = calcularTotal(pedido);

        pedido.setTotal(total);

        pedido.getItens().forEach(item -> item.setPedido(pedido));
    }

    private static @NonNull BigDecimal calcularTotal(Pedido pedido) {
        return pedido.getItens().stream().map(item ->
                item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()))
        ).reduce(BigDecimal.ZERO, BigDecimal::add).abs();
    }
}
