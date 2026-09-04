package io.github.curso.icompras.faturamento.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.curso.icompras.faturamento.model.Pedido;
import io.github.curso.icompras.faturamento.publisher.representation.AtualizacaoStatusPedido;
import io.github.curso.icompras.faturamento.publisher.representation.StatusPedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FaturamentoPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${icompras.config.kafka.topics.pedidos-faturados}")
    private String topico;

    public void publicar(Pedido pedido,String urlNotaFiscal) {
        try {
            var representation = new AtualizacaoStatusPedido(pedido.codigo(), StatusPedido.FATURADO, urlNotaFiscal );

            String json = objectMapper.writeValueAsString(representation);

            kafkaTemplate.send(topico, "dados", json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }


    }
}
