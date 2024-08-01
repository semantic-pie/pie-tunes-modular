package io.github.semanticpie.pietunes.streaming.core.service;

import io.github.semanticpie.pietunes.streaming.core.exception.ObjectNotFoundException;
import io.github.semanticpie.pietunes.streaming.core.util.Range;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackStreamingService {

  private final MinioClient minioClient;

  @Value("${minio.buckets.trackbucket}")
  public String trackBucketName;

  @Value("${minio.buckets.coverbucket}")
  public String coverBucketName;


  public GetObjectResponse getTrackFileById(String id, long offset, long length) {
    try {
      return minioClient.getObject(
          GetObjectArgs.builder()
              .bucket(trackBucketName)
              .object(id)
              .offset(offset)
              .length(length)
              .build());
    } catch (Exception ex) {
      String msg = String.format("Track '%s' not found", id);
      log.warn(msg);
      throw new ObjectNotFoundException(msg);
    }
  }

  public StatObjectResponse getTrackFileStatById(String id) {
    try {
      return minioClient.statObject(StatObjectArgs.builder()
          .bucket(trackBucketName)
          .object(id)
          .build());
    } catch (Exception ex) {
      String msg = String.format("Cover '%s' not found", id);
      log.warn(msg);
      throw new ObjectNotFoundException(msg);
    }
  }

  public byte[] readChunk(String id, Range range, long fileSize) {
    long startPosition = range.getRangeStart();
    long endPosition = range.getRangeEnd(fileSize);
    int chunkSize = (int) (endPosition - startPosition + 1);
    try (var inputStream = getTrackFileById(id, startPosition, chunkSize)) {
      return inputStream.readAllBytes();
    } catch (Exception exception) {
      log.error("Exception occurred when trying to read file with ID = {}", id);
      throw new RuntimeException(exception);
    }
  }


}
