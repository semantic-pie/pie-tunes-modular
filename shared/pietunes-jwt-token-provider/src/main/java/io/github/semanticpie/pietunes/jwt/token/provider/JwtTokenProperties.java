package io.github.semanticpie.pietunes.jwt.token.provider;

import java.time.Duration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Getter
@PropertySource("classpath:jwt-provider.properties")
public class JwtTokenProperties {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.lifetime}")
    private Duration jwtLifeTime;
}
