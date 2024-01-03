package de.schwarz.libraryapp.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties("security.jwt")
public class JwtProperties {
    private String secretKey;
    private Duration tokenDuration;
}