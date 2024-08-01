package io.github.semanticpie.pietunes.metadata.core.repositories.globalSearch;

import io.github.semanticpie.pietunes.metadata.core.models.dtos.domain.MusicBandDto;
import java.util.UUID;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BandSearchRepository extends ReactiveNeo4jRepository<MusicBandDto, UUID> {

    @Query("""
            MATCH (musicBandDto:Band)
            WHERE toLower(musicBandDto.name) CONTAINS toLower($searchQuery)
            OPTIONAL MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicBandDto:Band)
            WITH musicBandDto, COUNT(r) AS isLiked
            RETURN musicBandDto{
                .uuid,
                .name,
                .description,
                isLiked: CASE WHEN isLiked > 0 THEN true ELSE false END
            }
            LIMIT 4
            """)
    Flux<MusicBandDto> findAllByName(String userUuid, String searchQuery);
}
