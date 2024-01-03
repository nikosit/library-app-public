package de.schwarz.libraryapp.book.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookRequest {
    private Long bookId;
    private String author;
    private String title;
    private String publisher;
    private LocalDate publishingYear;
    private String category;
}
