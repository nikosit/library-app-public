package de.schwarz.libraryapp.customer.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerDto {
    private Long customerId;
    private String name;
    private String email;
    private String password;
}
