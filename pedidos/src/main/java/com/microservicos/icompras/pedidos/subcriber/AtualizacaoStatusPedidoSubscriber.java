package com.microservicos.icompras.pedidos.subcriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicos.icompras.pedidos.service.AtualizacaoStatusPedidoService;
import com.microservicos.icompras.pedidos.subcriber.representation.AtualizacaoStatusPedidoRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AtualizacaoStatusPedidoSubscriber {

    private final AtualizacaoStatusPedidoService service;
    private final ObjectMapper mapper;

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}",
            topics = {"${icompras.config.kafka.topics.pedidos-faturados}",
                    "${icompras.config.kafka.topics.pedidos-enviados}"
            })
    public void receberAtualizacao(String json){
        log.info("Recebendo atualizacao de status: {}", json);
        try {
            var atualizacaoStatus = mapper.readValue(json, AtualizacaoStatusPedidoRepresentation.class);
            service.atualizarStatus(atualizacaoStatus.codigo(),atualizacaoStatus.status(),atualizacaoStatus.urlNotaFiscal(),atualizacaoStatus.codigoRastreio());
        } catch (Exception e) {
            log.error("Erro ao atualizar status pedido", e.getMessage());
        }


    }
}
