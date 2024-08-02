package io.github.semanticpie.pietunes.uploader.utils;

import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicTrackDto;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.inner.InnerAlbumDto;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.inner.InnerBandDto;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

@Component
public class TrackMetadataParser {

  private static final Logger log = LoggerFactory.getLogger(TrackMetadataParser.class);

  public TrackMetadataParseResult parse(FilePart file) {
    try {
      File trackFile = File.createTempFile(file.filename(), ".tmp");

      file.transferTo(trackFile).block();
      Mp3File mp3file = new Mp3File(trackFile);

      ID3v2 id3v2 = mp3file.getId3v2Tag();

      MusicTrackDto musicTrack = parseMusicTrack(mp3file, id3v2);
      InnerAlbumDto musicAlbum = parseMusicAlbum(id3v2);
      InnerBandDto musicBand = parseMusicBand(id3v2);
      Set<String> musicGenre = parseMusicGenre(id3v2);

      musicTrack.setMusicAlbum(musicAlbum);
      musicTrack.setMusicBand(musicBand);
      musicTrack.setGenres(musicGenre);

      byte[] cover = id3v2.getAlbumImage();
      String coverMimeType = id3v2.getAlbumImageMimeType();

      return TrackMetadataParseResult.builder()
          .musicTrack(musicTrack)
          .cover(cover)
          .coverMimeType(coverMimeType).build();
    } catch (UnsupportedTagException | InvalidDataException | IOException e) {
      e.printStackTrace();
      throw new CantParseTrackMetadataException("kek");
    }
  }

  private MusicTrackDto parseMusicTrack(Mp3File mp3file, ID3v2 id3v2) {
    MusicTrackDto musicTrack = new MusicTrackDto();

    musicTrack.setTitle(id3v2.getTitle());
    musicTrack.setBitrate(mp3file.getBitrate());
    musicTrack.setLengthInMilliseconds(mp3file.getLengthInMilliseconds());
    musicTrack.setReleaseYear(id3v2.getYear());

    return musicTrack;
  }

  private InnerAlbumDto parseMusicAlbum(ID3v2 id3v2) {
    return new InnerAlbumDto(null, id3v2.getAlbum(), null, 1988);
  }

  private InnerBandDto parseMusicBand(ID3v2 id3v2) {
    return new InnerBandDto(null, id3v2.getArtist(), null);
  }

  private Set<String> parseMusicGenre(ID3v2 id3v2) {
    var genres = id3v2.getGenreDescription();
      if (genres == null || genres.isEmpty()) {
          return Set.of();
      }
    return splitGenres(genres).collect(Collectors.toSet());
  }

  private Stream<String> splitGenres(String rowGenres) {
    return Arrays.stream(rowGenres.split("/")).map(this::unifySpaces).map(String::toLowerCase);
  }

  private String unifySpaces(String genre) {
    return genre
        .replace(' ', '-')
        .replace('_', '-');
  }
}
