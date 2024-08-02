package io.github.semanticpie.pietunes.metadata.core.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Track")
@RequiredArgsConstructor
@Getter
@Setter
public class MusicTrack {

    @Id
    @GeneratedValue
    private UUID uuid;

    @Version
    @JsonIgnore
    private Long version;

    private String title;

    private String releaseYear;

    private Integer bitrate;

    private Long lengthInMilliseconds;

    public MusicTrack(UUID uuid, String title, String releaseYear, Integer bitrate,
        Long lengthInMilliseconds) {
        this.uuid = uuid;
        this.title = title;
        this.releaseYear = releaseYear;
        this.bitrate = bitrate;
        this.lengthInMilliseconds = lengthInMilliseconds;
    }

    @Relationship(type = "IN_GENRE", direction = Relationship.Direction.OUTGOING)
    private Set<MusicGenre> genres;

    @Relationship(type = "HAS_TRACK", direction = Relationship.Direction.INCOMING)
    @JsonProperty("band")
    private MusicBand musicBand;

    @Relationship(type = "CONTAINS", direction = Relationship.Direction.INCOMING)
    @JsonProperty("album")
    private MusicAlbum musicAlbum;
}
