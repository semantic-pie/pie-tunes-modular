package io.github.semanticpie.pietunes.streaming.core.service;

import io.github.semanticpie.pietunes.streaming.core.exception.ObjectNotFoundException;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackCoverService {

  private final MinioClient minioClient;

  @Value("${minio.buckets.coverbucket}")
  public String coverBucketName;

  public GetObjectResponse getTrackCoverById(String id) {
    try {
      return minioClient.getObject(
          GetObjectArgs.builder()
              .bucket(coverBucketName)
              .object(id)
              .build());
    } catch (Exception ex) {
      String msg = String.format("Cover '%s' not found", id);
      log.warn(msg);
      throw new ObjectNotFoundException(msg);
    }
  }
}
