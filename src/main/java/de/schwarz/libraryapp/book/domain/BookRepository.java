package de.schwarz.libraryapp.book.domain;


import de.schwarz.libraryapp.book.domain.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = "SELECT b.* FROM library.book b WHERE b.author LIKE '%' || :author || '%'", nativeQuery = true)
    List<Book> findByAuthor(@Param(value = "author") String author);

    @Query(value = "SELECT b.* FROM library.book b JOIN library.category c ON (b.category_id = c.id) WHERE c.description = :category ORDER BY b.publishing_year DESC", nativeQuery = true)
    List<Book> findByCategory(@Param(value = "category") String category);
}