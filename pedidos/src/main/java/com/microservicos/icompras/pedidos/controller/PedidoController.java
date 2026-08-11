package com.microservicos.icompras.pedidos.controller;

import com.microservicos.icompras.pedidos.controller.dto.AdicaoNovoPagamentoDTO;
import com.microservicos.icompras.pedidos.controller.dto.NovoPedidoDTO;
import com.microservicos.icompras.pedidos.controller.mappers.PedidoMapper;
import com.microservicos.icompras.pedidos.model.ErroResposta;
import com.microservicos.icompras.pedidos.model.exception.ItemNaoEncontradoException;
import com.microservicos.icompras.pedidos.model.exception.ValidationException;
import com.microservicos.icompras.pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoMapper pedidoMapper;

    @PostMapping
    public ResponseEntity<Object> criar(@RequestBody NovoPedidoDTO dto) {
        try{
            var pedido = pedidoMapper.map(dto);
            var novoPedido = pedidoService.criarPedido(pedido);
            return ResponseEntity.ok(novoPedido.getCodigo());
        }catch (ValidationException e){
            var erro = new ErroResposta("Erro validacao",e.getField(),e.getMessage());
            return ResponseEntity.badRequest().body(erro);
        }
    }

    @PostMapping("pagamentos")
    public ResponseEntity<Object> AdicionarNovoPagamento(@RequestBody AdicaoNovoPagamentoDTO dto) {
        try{
            pedidoService.adicionarNovoPagamento(dto.codigoPedido(), dto.dados(), dto.tipoPagamento());
            return ResponseEntity.noContent().build();
        } catch (ItemNaoEncontradoException e) {
            var erro = new ErroResposta("Item não encontrado","codigoPedido",e.getMessage());
            return  ResponseEntity.badRequest().body(erro);
        }

    }
}
