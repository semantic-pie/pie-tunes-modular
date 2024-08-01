package io.github.semanticpie.pietunes.streaming.core.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BaseErrorInfo {

  private LocalDateTime timestamp;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private String url;
  private int status;
  private String message;

  private BaseErrorInfo() {
    timestamp = LocalDateTime.now();
  }

  public BaseErrorInfo(int status, String url, String message) {
    this();
    this.status = status;
    this.url = url;
    this.message = message;

  }
}