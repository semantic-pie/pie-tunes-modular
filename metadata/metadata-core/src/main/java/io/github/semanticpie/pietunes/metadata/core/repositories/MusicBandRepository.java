package io.github.semanticpie.pietunes.metadata.core.repositories;

import io.github.semanticpie.pietunes.metadata.core.models.MusicBand;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface MusicBandRepository extends ReactiveNeo4jRepository<MusicBand, UUID> {

    Mono<MusicBand> findMusicBandByName(String name);

    @Query("""
           MERGE (b:Band {name: :#{#musicBand.name}})
           ON CREATE SET b.uuid = toString(randomUUID()), b.version = 0
           RETURN b
           """)
    Mono<MusicBand> persist(@Param("musicBand") MusicBand musicBand);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(b:Band)
            WITH COUNT(b) AS total
            RETURN total
            """)
    Mono<Long> findTotalLikedBands(String userUuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicBand:Band)
            RETURN musicBand{
                .description,
                .name, .uuid,
                .version,
                __nodeLabels__: labels(musicBand),
                __elementId__: id(musicBand)}
            :#{orderBy(#pageable)}
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicBand> findAllLikedBands(String userUuid, Pageable pageable);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(b:Band)
            WHERE toLower(b.name) CONTAINS toLower($searchQuery)
            WITH COUNT(b) AS total
            RETURN total
            """)
    Mono<Long> findTotalLikedBandsByTitle(String searchQuery, String userUuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicBand:Band)
            WHERE toLower(musicBand.name) CONTAINS toLower($searchQuery)
            RETURN musicBand{
                .description,
                .name, .uuid,
                .version,
                __nodeLabels__: labels(musicBand),
                __elementId__: id(musicBand)}
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicBand> findAllLikedBandsByTitle(String searchQuery, String userUuid, Pageable pageable);

    @Query("""
            MATCH (musicBand:Band)
            RETURN musicBand{
             .name,
             .uuid,
             .description,
             __nodeLabels__: labels(musicBand),
             __elementId__: id(musicBand)
             }
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicBand> findAllBands(Pageable pageable);

    @Query("""
            MATCH (musicBand:Band {uuid: $bandUuid})
            RETURN musicBand{
             .yearOfRecord,
             .name,
             .uuid,
             .description,
             .version,
             __nodeLabels__: labels(musicBand),
             __elementId__: id(musicBand),
             Band_HAS_ALBUM_Album: [(musicBand)-[:HAS_ALBUM]->(musicBand_albums:Album) | musicBand_albums{
                .yearOfRecord,
                .name,
                .uuid,
                .description,
                .version,
                __nodeLabels__: labels(musicBand_albums),
                __elementId__: id(musicBand_albums)
             }]
             }
            """)
    Mono<MusicBand> findMusicBandByUuid(String bandUuid);
}
