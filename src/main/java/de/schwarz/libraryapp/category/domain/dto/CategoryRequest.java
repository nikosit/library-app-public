package de.schwarz.libraryapp.category.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryRequest {
    private Long categoryId;
    private String description;
}
