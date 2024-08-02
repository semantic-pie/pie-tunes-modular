package io.github.semanticpie.pietunes.authorization.repositories;

import io.github.semanticpie.pietunes.authorization.model.entity.UserSql;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserH2Repository extends R2dbcRepository<UserSql, UUID> {

    Mono<UserSql> findUserSqlByEmail(String email);
}
