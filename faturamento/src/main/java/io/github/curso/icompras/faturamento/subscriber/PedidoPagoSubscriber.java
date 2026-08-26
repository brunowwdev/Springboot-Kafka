package io.github.curso.icompras.faturamento.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.curso.icompras.faturamento.GeradorNotaFiscalService;
import io.github.curso.icompras.faturamento.mapper.PedidoMapper;
import io.github.curso.icompras.faturamento.model.Pedido;
import io.github.curso.icompras.faturamento.subscriber.representation.DetalhePedidoRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoPagoSubscriber {

    private final ObjectMapper objectMapper;
    private final GeradorNotaFiscalService service;
    private final PedidoMapper pedidoMapper;

    @KafkaListener(groupId = "icompras-faturamento",topics = "${icompras.config.kafka.topics.pedidos-pagos}")
    public void listen(String json){
        try{
            log.info("Recebendo pedido para faturamento {}", json);
            var representation = objectMapper.readValue(json, DetalhePedidoRepresentation.class);
            Pedido pedido = pedidoMapper.map(representation);
            service.gerar(pedido);
        } catch (Exception e) {
            log.error("Erro na consumacao do topico de pedidos pagos");
        }
    }
}
