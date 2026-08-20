package com.microservicos.icompras.pedidos.service;

import com.microservicos.icompras.pedidos.Repository.ItemPedidoRepository;
import com.microservicos.icompras.pedidos.Repository.PedidoRepository;
import com.microservicos.icompras.pedidos.client.ClientesClient;
import com.microservicos.icompras.pedidos.client.ProdutosClient;
import com.microservicos.icompras.pedidos.client.ServicoBancarioClient;
import com.microservicos.icompras.pedidos.model.DadosPagamento;
import com.microservicos.icompras.pedidos.model.ItemPedido;
import com.microservicos.icompras.pedidos.model.Pedido;
import com.microservicos.icompras.pedidos.model.enums.StatusPedido;
import com.microservicos.icompras.pedidos.model.enums.TipoPagamento;
import com.microservicos.icompras.pedidos.model.exception.ItemNaoEncontradoException;
import com.microservicos.icompras.pedidos.publisher.PagamentoPublisher;
import com.microservicos.icompras.pedidos.validator.PedidoValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private  final ItemPedidoRepository itemPedidoRepository;
    private final PedidoValidator validator;
    private final ServicoBancarioClient servicoBancarioClient;
    private final ClientesClient apiClients;
    private final ProdutosClient apiProdutos;
    private final PagamentoPublisher pagamentoPublisher;

    @Transactional
    public Pedido criarPedido(Pedido pedido) {
        validator.validar(pedido);
        realizarPersistencia(pedido);
        enviarSolicitacaoPagamento(pedido);
        return pedido;
    }

    private void enviarSolicitacaoPagamento(Pedido pedido) {
        var chavePagamento = servicoBancarioClient.solicitarPagamento(pedido);
        pedido.setChavePagamento(chavePagamento);
    }

    private void realizarPersistencia(Pedido pedido) {
        pedidoRepository.save(pedido);
        itemPedidoRepository.saveAll(pedido.getItens());
    }

    public void atualizarStatusPagamento(Long codigoPedido, String chavePagamento, boolean sucesso, String observacoes) {
        var pedidoEncontrado = pedidoRepository.findByCodigoAndChavePagamento(codigoPedido, chavePagamento);
        if (pedidoEncontrado.isEmpty()){
            var msg = String.format("Pedido não encontrado para o código %d e chave pagamento %s", codigoPedido,chavePagamento);
            log.error(msg);
            return;
        }
        Pedido pedido = pedidoEncontrado.get();
        if(sucesso){
            prepararPrepararPedidoPago(pedido);

        } else  {
            pedido.setStatus(StatusPedido.ERRO_PAGAMENTO);
            pedido.setObservacoes(observacoes);
        }
        pedidoRepository.save(pedido);
    }

    private void prepararPrepararPedidoPago(Pedido pedido) {
        pedido.setStatus(StatusPedido.PAGO);
        carregarDadosCliente(pedido);
        carregarItensPedido(pedido);
        pagamentoPublisher.publicar(pedido);
    }

    @Transactional
    public void adicionarNovoPagamento(Long codigoPedido, String dadosCartao, TipoPagamento tipoPagamento) {
        var pedidoEncontrado = pedidoRepository.findById(codigoPedido);

        if(pedidoEncontrado.isEmpty()){
            throw new ItemNaoEncontradoException("Pedido não encontrado para o código informado");
        }

        var pedido = pedidoEncontrado.get();

        DadosPagamento dadosPagamento = new DadosPagamento();
        dadosPagamento.setTipoPagamento(tipoPagamento);
        dadosPagamento.setDados(dadosCartao);

        pedido.setDadosPagamento(dadosPagamento);
        pedido.setStatus(StatusPedido.REALIZADO);
        pedido.setObservacoes("Novo pagamento realizado, aguardando novo processamento.");

        String novaChavePagamento = servicoBancarioClient.solicitarPagamento(pedido);
        pedido.setChavePagamento(novaChavePagamento);

        pedidoRepository.save(pedido);
    }

    public Optional<Pedido> carregarDadosCompletosPedido(Long codigoPedido) {
        Optional<Pedido> pedido =  pedidoRepository.findById(codigoPedido);
        pedido.ifPresent(this::carregarDadosCliente);
        pedido.ifPresent(this::carregarItensPedido);
        return pedido;
    }

    private void carregarDadosCliente(Pedido pedido) {
        Long codigoCliente = pedido.getCodigoCliente();
        var response = apiClients.obterDados(codigoCliente);
        pedido.setDadosCliente(response.getBody());
    }

    private void carregarItensPedido(Pedido pedido) {
        List<ItemPedido> itens = itemPedidoRepository.findByPedido(pedido);
        pedido.setItens(itens);
        pedido.getItens().forEach(this::carregarDadosProduto); //para cada elemento do itemPedido ele vai chamar a funcao carregarDadosProduto
    }

    private void carregarDadosProduto(ItemPedido item) {
        Long codigoProduto = item.getCodigoProduto();
        var response = apiProdutos.obterDados(codigoProduto);
        item.setNome(response.getBody().nome());
    }
}
