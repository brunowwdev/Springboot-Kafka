package io.github.curso.icompras.faturamento.bucket;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.github.curso.icompras.faturamento.config.props.MinioProps;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class BucketService {

    private final MinioClient minioClient;
    private final MinioProps props;

    public void upload(BucketFile file) {
        try{
            var object = PutObjectArgs
                    .builder()
                    .bucket(props.getBucketName())
                    .object(file.name())
                    .stream(file.is(), file.size(), -1)
                    .contentType(file.type().toString())
                    .build();
            minioClient.putObject(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getUrl(String fileName){
        try{
            var object = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(props.getBucketName())
                    .object(fileName)
                    .expiry(1, TimeUnit.HOURS)
                    .build();
            return minioClient.getPresignedObjectUrl(object);

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
