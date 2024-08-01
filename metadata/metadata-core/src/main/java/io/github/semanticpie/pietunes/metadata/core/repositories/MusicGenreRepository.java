package io.github.semanticpie.pietunes.metadata.core.repositories;

import io.github.semanticpie.pietunes.metadata.core.models.MusicGenre;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;


public interface MusicGenreRepository extends ReactiveNeo4jRepository<MusicGenre, String> {
    @Query("""
            MERGE (g:Genre {name: :#{#musicGenre.name}})
            ON CREATE SET g.version = 0
            RETURN g
            """)
    Mono<MusicGenre> persist(@Param("musicGenre") MusicGenre musicGenre);

    Mono<MusicGenre> findMusicGenreByName(String name);
}
