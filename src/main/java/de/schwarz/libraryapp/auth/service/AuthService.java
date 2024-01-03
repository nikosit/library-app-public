package de.schwarz.libraryapp.auth.service;

import de.schwarz.libraryapp.auth.domain.LoginResponse;
import de.schwarz.libraryapp.security.JwtIssuer;
import de.schwarz.libraryapp.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    public static final String ERROR_AUTH_CUSTOMER = "error.auth.customer";


    private final JwtIssuer jwtIssuer;
    private final AuthenticationManager authenticationManager;


    /**
     * Authentication of customer over login process.
     *
     * @param email
     * @param password
     * @return
     */
    public LoginResponse login(final String email, final String password) {
        try {
            var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            var principal = (UserPrincipal) authentication.getPrincipal();
            var token = jwtIssuer.issue(JwtIssuer.Request.builder()
                    .userId(principal.getUserId())
                    .email(principal.getEmail())
                    .roles(principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                    .build());

            return LoginResponse.builder()
                    .token(token)
                    .build();
        } catch (Exception e) {
            log.error("Error during login process with email: {}...", email, e);
            throw new InternalError(ERROR_AUTH_CUSTOMER);
        }
    }
}
