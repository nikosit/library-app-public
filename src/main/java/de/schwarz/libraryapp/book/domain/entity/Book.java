package de.schwarz.libraryapp.book.domain.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "book", schema = "library")
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "library.book_id_seq")
    @SequenceGenerator(name = "library.book_id_seq", sequenceName = "library.book_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "author", length = 30, nullable = false)
    private String author;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "publisher", length = 50, nullable = false)
    private String publisher;

    @Column(name = "publishing_year", nullable = false)
    private LocalDate publishingYear;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;
}