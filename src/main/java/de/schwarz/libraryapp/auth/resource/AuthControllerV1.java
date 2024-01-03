package de.schwarz.libraryapp.auth.resource;

import de.schwarz.libraryapp.auth.domain.LoginRequest;
import de.schwarz.libraryapp.auth.domain.LoginResponse;
import de.schwarz.libraryapp.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/auth", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class AuthControllerV1 {

    private final AuthService authService;


    @Operation(tags = "Auth login", summary = "Authentication check over login", description = "Process authenticates customers from online library database during login.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LoginRequest.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @PostMapping("/v1/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginRequest request) {
        // Call service
        LoginResponse response = authService.login(request.getEmail(), request.getPassword());
        log.info("Response Token detected: {}...", response.getToken());
        // Prepare and return response
        return ResponseEntity
                .ok(response);
    }
}
