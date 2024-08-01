package io.github.semanticpie.pietunes.metadata.core.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("User")
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserNeo4j {

    private static final int INITIAL_GENRE_WEIGHT = 5;

    @Id
    @NonNull
    private UUID uuid;

    @Version
    private Long version;

    @Relationship(type = "PREFERS_GENRE", direction = Relationship.Direction.OUTGOING)
    private Set<PreferredGenre> preferredGenres;

    @Relationship(type = "LIKES", direction = Relationship.Direction.OUTGOING)
    private Set<MusicTrack> likedTracks;

    @Relationship(type = "HAS_PLAYLIST", direction = Relationship.Direction.OUTGOING)
    private List<Playlist> playlists;

    public void addPreferredGenre(MusicGenre genre) {
        if (preferredGenres == null) {
            preferredGenres = new HashSet<>();
        }
        var genreRelation = new PreferredGenre(genre, INITIAL_GENRE_WEIGHT);
        preferredGenres.add(genreRelation);
    }



}
