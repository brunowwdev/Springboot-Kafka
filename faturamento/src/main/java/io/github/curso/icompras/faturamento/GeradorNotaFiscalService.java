package io.github.curso.icompras.faturamento;

import io.github.curso.icompras.faturamento.bucket.BucketFile;
import io.github.curso.icompras.faturamento.bucket.BucketService;
import io.github.curso.icompras.faturamento.model.Pedido;
import io.github.curso.icompras.faturamento.publisher.FaturamentoPublisher;
import io.github.curso.icompras.faturamento.service.NotaFiscalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeradorNotaFiscalService {

    private final NotaFiscalService notaFiscalService;
    private final BucketService bucketService;
    private final FaturamentoPublisher publisher;

    public void gerar(Pedido pedido) {
        log.info("Gerada nota fiscal para o pedido {}", pedido.codigo());

        try {
            byte[] byteArray = notaFiscalService.getNota(pedido);

            String nomeArquivo = String.format("notafiscal_pedido_%d.pdf", pedido.codigo());

            var file = new BucketFile(nomeArquivo,
                    new ByteArrayInputStream(byteArray),
                    MediaType.APPLICATION_PDF,
                    byteArray.length);

            bucketService.upload(file);

            String url = bucketService.getUrl(nomeArquivo);
            publisher.publicar(pedido, url);

        } catch (Exception e){
            log.error(e.getMessage(),e);
        }

    }
}
