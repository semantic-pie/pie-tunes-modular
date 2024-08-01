package io.github.semanticpie.pietunes.metadata.core.services.jwt;

import java.time.Duration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JwtTokenProperties {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.lifetime}")
    private Duration jwtLifeTime;
}
