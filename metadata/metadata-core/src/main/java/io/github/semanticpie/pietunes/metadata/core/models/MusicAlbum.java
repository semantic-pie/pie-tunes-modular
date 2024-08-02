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
import org.springframework.lang.Nullable;

@Node("Album")
@RequiredArgsConstructor
@Getter
@Setter
public class MusicAlbum {

    @Id
    @GeneratedValue
    private UUID uuid;

    @Version
    @JsonIgnore
    private Long version;

    private String name;

    @Nullable
    private String description;

    @Nullable
    private int yearOfRecord;

    @Nullable
    @Relationship(type = "CONTAINS", direction = Relationship.Direction.OUTGOING)
    private Set<MusicTrack> tracks;

    @Relationship(type = "HAS_ALBUM", direction = Relationship.Direction.INCOMING)
    @JsonProperty("band")
    private MusicBand musicBand;

    public MusicAlbum(UUID id, String name, @Nullable String description, int yearOfRecord) {
        this.uuid = id;
        this.name = name;
        this.description = description;
        this.yearOfRecord = yearOfRecord;
    }
}
