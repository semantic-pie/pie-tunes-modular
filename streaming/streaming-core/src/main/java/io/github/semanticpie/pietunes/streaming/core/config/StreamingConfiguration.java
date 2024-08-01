package io.github.semanticpie.pietunes.streaming.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
public class StreamingConfiguration {

  @Value("${streaming.default-chunk-size}")
  public Integer defaultChunkSize;

  @Value("${streaming.initial-chunk-size}")
  public Integer initialChunkSize;

  @EventListener(ApplicationReadyEvent.class)
  public void afterInitialization() {
    log.info("-------------------------");
    log.info("DEFAULT_CHUNK_SIZE: {}", defaultChunkSize);
    log.info("INITIAL_CHUNK_SIZE: {}", initialChunkSize);
    log.info("-------------------------\n");
  }
}
