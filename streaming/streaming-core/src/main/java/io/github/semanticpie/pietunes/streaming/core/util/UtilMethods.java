package io.github.semanticpie.pietunes.streaming.core.util;

public class UtilMethods {

  public static String calculateContentLengthHeader(Range range, long fileSize) {
    return String.valueOf(range.getRangeEnd(fileSize) - range.getRangeStart() + 1);
  }

  public static String constructContentRangeHeader(Range range, long fileSize) {
    return "bytes " + range.getRangeStart() + "-" + range.getRangeEnd(fileSize) + "/" + fileSize;
  }
}
