package io.github.semanticpie.pietunes.shared.spi.adapter.minio.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
@PropertySource("classpath:minio-spi.properties")
public class MinioConfiguration {

  @Value("${minio.endpoint}")
  private String endpoint;

  @Value("${minio.accessKey}")
  private String accessKey;

  @Value("${minio.secretKey}")
  private String secretKey;

  @Value("${minio.buckets.trackbucket}")
  public String trackBucketName;

  @Value("${minio.buckets.coverbucket}")
  public String coverBucketName;

  @Bean
  public MinioClient minioClient()
      throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
    MinioClient client = MinioClient.builder()
        .endpoint(endpoint)
        .credentials(accessKey, secretKey)
        .build();

    for (String bucket : List.of(trackBucketName, coverBucketName)) {
      boolean isBucketExist = client.bucketExists(
          BucketExistsArgs.builder().bucket(bucket).build());
      if (!isBucketExist) {
        client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
      }
    }

    return client;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void afterInitialization() {
    log.info("-------------------------");
    log.info("COVERS_BUCKET: {}", coverBucketName);
    log.info("TRACKS_BUCKET: {}", trackBucketName);
    log.info("MINIO_ENDPOINT: {}", endpoint);
    log.info("-------------------------\n");
  }

}

