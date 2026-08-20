package com.microservicos.icompras.pedidos.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicos.icompras.pedidos.model.Pedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PagamentoPublisher {

    private final DetalhePedidoMapper mapper;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${icompras.config.kafka.topics.pedidos-pagos}")
    private String topico;

    //Pega um Pedido pago, transformar os dados em JSON e publicar esse JSON em um tópico do Kafka.
    public void publicar(Pedido pedido){
        log.info("Publicando pedido pago {}", pedido.getCodigo());

        try{
            var representation = mapper.map(pedido);
            var json = objectMapper.writeValueAsString(representation);
            kafkaTemplate.send(topico,"dados", json);
        } catch (JsonProcessingException e) {
            log.error("Erro ao processar json {}", e.getMessage());
        } catch (RuntimeException e) {
            log.error("Erro tecnico ao publicar no topico de pedidos {}", e.getMessage());
        }
    }
}
