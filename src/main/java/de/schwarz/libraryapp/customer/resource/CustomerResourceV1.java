package de.schwarz.libraryapp.customer.resource;


import de.schwarz.libraryapp.customer.domain.dto.CustomerDto;
import de.schwarz.libraryapp.customer.domain.dto.CustomerRequest;
import de.schwarz.libraryapp.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class CustomerResourceV1 {

    private final CustomerService customerService;


    @Operation(tags = "Get all customers", summary = "Getting all customers from library", description = "Process gets all customers from online library database without restrictions.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CustomerDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @GetMapping("/v1/customers")
    public ResponseEntity<?> detectAllCustomers() {
        // Call service
        List<CustomerDto> customers = customerService.detectAllCustomers();
        log.info("Count of customers detected: {}...", customers.size());
        // Prepare and return response
        return ResponseEntity
                .ok()
                .body(customers);
    }

    @Operation(tags = "Get a customer email", summary = "Getting a customer by the given email from library", description = "Process gets a customer from online library database, by the given email.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CustomerDto.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = HttpClientErrorException.BadRequest.class)), description = "Bad Request<br/><br/>* Email is empty.<br/>* Email is invalid."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @GetMapping("/v1/customer/email")
    public ResponseEntity<?> detectCustomerFromEmail(@RequestParam(value = "email", required = false) String email) {
        // Validate request param
        customerService.validateRequestParamEmail(email);
        // Call service
        CustomerDto customer = customerService.detectCustomerByEmail(email);
        log.info("Customer detected by email: {}...", customer.getEmail());
        // Prepare and return response
        return ResponseEntity
                .ok()
                .body(customer);
    }

    @Operation(tags = "Detect customer", summary = "Detects a customer from the online library", description = "Detects a customer from the library database, by the given customer id.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CustomerRequest.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = HttpClientErrorException.BadRequest.class)), description = "Bad Request<br/><br/>* Customer id is empty."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @GetMapping("/v1/customer/id")
    public ResponseEntity<?> detectCustomer(@RequestParam(value = "customerId", required = false) Long customerId) {
        // Validate request param
        customerService.validateRequestParamCustomerId(customerId);
        // Call service
        CustomerDto customer = customerService.detectCustomer(customerId);
        log.info("Customer with id: {}, detected...", customer.getCustomerId());
        // Prepare and return response
        return ResponseEntity
                .ok()
                .body(customer);
    }

    @Operation(tags = "Create update customer", summary = "Creates or updates a customer in the online library", description = "Process creates or updates a customer in library database, by the given request.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CustomerRequest.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = HttpClientErrorException.BadRequest.class)), description = "Bad Request<br/><br/>* Name is empty.<br/>* Email is empty.<br/>* Email is invalid.<b/>* Password is empty."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @PostMapping("/v1/customer")
    public ResponseEntity<?> saveCustomer(@RequestBody(required = false) CustomerRequest request) {
        // Validate request param
        customerService.validateRequestParams(request);
        // Call service
        CustomerDto customer = customerService.createOrUpdateCustomer(request);
        log.info("Customer created or updated with name: {}...", customer.getName());
        // Prepare and return response
        return ResponseEntity
                .ok()
                .body(customer);
    }

    @Operation(tags = "Remove customer", summary = "Removes a customer from the online library", description = "Process removes a customer from the library database, by the given customer id.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CustomerRequest.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = HttpClientErrorException.BadRequest.class)), description = "Bad Request<br/><br/>* Customer id is empty."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @DeleteMapping("/v1/customer/id")
    public ResponseEntity<?> removeCustomer(@RequestParam(value = "customerId", required = false) Long customerId) {
        // Validate request param
        customerService.validateRequestParamCustomerId(customerId);
        // Call service
        customerService.removeCustomer(customerId);
        log.info("Customer with id: {}, removed...", customerId);
        // Prepare and return response
        return ResponseEntity
                .ok()
                .body(customerId);
    }
}
