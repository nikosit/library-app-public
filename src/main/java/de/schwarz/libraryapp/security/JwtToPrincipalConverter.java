package de.schwarz.libraryapp.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtToPrincipalConverter {

    private static final String ROLES = "roles";
    private static final String EMAIL = "email";


    /**
     * @param jwt
     * @return
     */
    public UserPrincipal convert(DecodedJWT jwt) {
        var roles = getClaimOrEmptyList(jwt).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return UserPrincipal.builder()
                .userId(Long.parseLong(jwt.getSubject()))
                .email(jwt.getClaim(EMAIL).asString())
                .authorities(roles)
                .build();
    }

    private List<String> getClaimOrEmptyList(DecodedJWT jwt) {
        if (jwt.getClaim(ROLES).isNull()) {
            return List.of();
        }

        return jwt.getClaim("roles").asList(String.class);
    }
}
