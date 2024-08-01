package io.github.semanticpie.pietunes.metadata.core.models;


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

@Node("Playlist")
@RequiredArgsConstructor
@Getter
@Setter
public class Playlist {

    @Id
    @GeneratedValue
    private UUID uuid;

    @Version
    private Long version;

    private String name;

    @Relationship(type = "CONTAINS", direction = Relationship.Direction.OUTGOING)
    private Set<MusicTrack> tracks;

}
