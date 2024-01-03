package de.schwarz.libraryapp.book.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDto {
    private Long bookId;
    private String author;
    private String title;
    private String publisher;
    private String publishingYear;
    @JsonProperty(value = "category")
    private String categoryDescription;
}
