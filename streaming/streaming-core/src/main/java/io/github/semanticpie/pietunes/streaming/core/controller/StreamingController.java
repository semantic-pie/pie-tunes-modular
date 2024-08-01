package io.github.semanticpie.pietunes.streaming.core.controller;

import io.github.semanticpie.pietunes.streaming.core.config.StreamingConfiguration;
import io.github.semanticpie.pietunes.streaming.core.service.TrackCoverService;
import io.github.semanticpie.pietunes.streaming.core.service.TrackStreamingService;
import io.github.semanticpie.pietunes.streaming.core.util.Range;
import io.github.semanticpie.pietunes.streaming.core.util.UtilMethods;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StreamingController {

  private final StreamingConfiguration streamingConfiguration;

  private final TrackStreamingService trackStreamingService;

  private final TrackCoverService trackCoverService;

  @GetMapping("/api/v1/track/cover/{id}")
  public ResponseEntity<InputStreamResource> getCover(@PathVariable String id) {
    final var cover = trackCoverService.getTrackCoverById(id);
    final var lengthInBytes = Integer.parseInt(
        Objects.requireNonNull(cover.headers().get("Content-Length")));
    final var contentType = cover.headers().get("Content-Type");

    assert contentType != null;
    return ResponseEntity.ok().contentLength(lengthInBytes)
        .header("Cache-Control", "public, max-age=86400")
        .contentType(MediaType.parseMediaType(contentType)).body(new InputStreamResource(cover));
  }

  @GetMapping("/api/v1/track/{id}")
  public ResponseEntity<byte[]> getTrack(@PathVariable(value = "id") String id,
      @RequestHeader(value = "Range", required = false) String rangeHeaderValue) {

    Range range = Range.parseHttpRangeString(rangeHeaderValue,
        streamingConfiguration.defaultChunkSize, streamingConfiguration.initialChunkSize);

    var stat = trackStreamingService.getTrackFileStatById(id);
    var chunk = trackStreamingService.readChunk(id, range, stat.size());

    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
        .header(HttpHeaders.CONTENT_TYPE, stat.contentType()).header(HttpHeaders.CONTENT_LENGTH,
            UtilMethods.calculateContentLengthHeader(range, stat.size()))
        .header(HttpHeaders.CONTENT_RANGE,
            UtilMethods.constructContentRangeHeader(range, stat.size())).body(chunk);
  }


}
