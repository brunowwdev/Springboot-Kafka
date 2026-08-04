package com.microservicos.icompras.pedidos.controller.mappers;

import com.microservicos.icompras.pedidos.controller.dto.ItemPedidoDTO;
import com.microservicos.icompras.pedidos.model.ItemPedido;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {

    ItemPedido map(ItemPedidoDTO dto);
}
