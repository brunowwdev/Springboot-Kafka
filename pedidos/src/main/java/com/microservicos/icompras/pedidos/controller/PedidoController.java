package com.microservicos.icompras.pedidos.controller;

import com.microservicos.icompras.pedidos.controller.dto.AdicaoNovoPagamentoDTO;
import com.microservicos.icompras.pedidos.controller.dto.NovoPedidoDTO;
import com.microservicos.icompras.pedidos.controller.mappers.PedidoMapper;
import com.microservicos.icompras.pedidos.model.ErroResposta;
import com.microservicos.icompras.pedidos.model.exception.ItemNaoEncontradoException;
import com.microservicos.icompras.pedidos.model.exception.ValidationException;
import com.microservicos.icompras.pedidos.publisher.DetalhePedidoMapper;
import com.microservicos.icompras.pedidos.publisher.representation.DetalhePedidoRepresentation;
import com.microservicos.icompras.pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoMapper pedidoMapper;
    private final DetalhePedidoMapper detalhePedidoMapper;

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

    @GetMapping("{codigo}")
    public ResponseEntity<DetalhePedidoRepresentation> obterDetalhesPedido(@PathVariable Long codigo){
        return pedidoService
                .carregarDadosCompletosPedido(codigo)//vai tentar obter os detalhes do pedido
                .map(detalhePedidoMapper::map) //obtendo ele vai mapear para o detalhe pedido representation
                .map(ResponseEntity::ok) //depois vai retornar no response entity com codigo ok
                .orElseGet(() -> ResponseEntity.notFound().build()); //senao houver o pedido vai retornar um response entity not found
    }
}
