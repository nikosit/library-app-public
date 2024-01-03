package de.schwarz.libraryapp.book.resource;


import de.schwarz.libraryapp.book.domain.dto.BookDto;
import de.schwarz.libraryapp.book.domain.dto.BookRequest;
import de.schwarz.libraryapp.book.service.BookService;
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
public class BookResourceV1 {

    private final BookService bookService;


    @Operation(tags = "Get all books", summary = "Getting all books from library", description = "Process gets all books from online library database without restrictions.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = BookDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @GetMapping("/v1/books")
    public ResponseEntity<?> detectAllBooks() {
        // Call service
        List<BookDto> books = bookService.detectAllBooks();
        log.info("Count of books detected: {}...", books.size());
        // Prepare and return response
        return ResponseEntity
                .ok()
                .body(books);
    }

    @Operation(tags = "Get all books author", summary = "Getting all books by the given author from library", description = "Process gets all books from online library database, by the given author.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = BookDto.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = HttpClientErrorException.BadRequest.class)), description = "Bad Request<br/><br/>* Author is empty."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @GetMapping("/v1/books/author")
    public ResponseEntity<?> detectBooksFromAuthor(@RequestParam(value = "author", required = false) String author) {
        // Validate request param
        bookService.validateRequestParamAuthor(author);
        // Call service
        List<BookDto> books = bookService.detectBooksByAuthor(author);
        log.info("Count of books detected: {} by author: {}...", books.size(), author);
        // Prepare and return response
        return ResponseEntity
                .ok()
                .body(books);
    }

    @Operation(tags = "Get all books category", summary = "Getting all books by the given category from library", description = "Process gets all books from online library database, by the given category.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = BookDto.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = HttpClientErrorException.BadRequest.class)), description = "Bad Request<br/><br/>* Category is empty."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @GetMapping("/v1/books/category")
    public ResponseEntity<?> detectBooksInCategory(@RequestParam(value = "category", required = false) String category) {
        // Validate request param
        bookService.validateRequestParamCategory(category);
        // Call service
        List<BookDto> books = bookService.detectBooksByCategory(category);
        log.info("Count of books detected: {} by category: {}...", books.size(), category);
        // Prepare and return response
        return ResponseEntity
                .ok()
                .body(books);
    }

    @Operation(tags = "Detect book", summary = "Detects a book from the online library", description = "Detects a book from the library database, by the given book id.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = BookRequest.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = HttpClientErrorException.BadRequest.class)), description = "Bad Request<br/><br/>* Book id is empty."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @GetMapping("/v1/book/id")
    public ResponseEntity<?> detectBook(@RequestParam(value = "bookId", required = false) Long bookId) {
        // Validate request param
        bookService.validateRequestParamBookId(bookId);
        // Call service
        BookDto book = bookService.detectBook(bookId);
        log.info("Book with id: {}, detected...", book.getBookId());
        // Prepare and return response
        return ResponseEntity
                .ok()
                .body(book);
    }

    @Operation(tags = "Create update book", summary = "Creates or updates a book in the online library", description = "Process creates or updates a book in library database, by the given request.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = BookRequest.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = HttpClientErrorException.BadRequest.class)), description = "Bad Request<br/><br/>* Author is empty.<br/>* Title is empty.<b/>* Publisher is empty.<b/>* Publishing Year is empty.<b/>* Category is empty.<b/>"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @PostMapping("/v1/book")
    public ResponseEntity<?> saveBook(@RequestBody(required = false) BookRequest request) {
        // Validate request param
        bookService.validateRequestParams(request);
        // Call service
        BookDto book = bookService.createOrUpdateBook(request);
        log.info("Book created or updated from author: {}...", book.getAuthor());
        // Prepare and return response
        return ResponseEntity
                .ok()
                .body(book);
    }

    @Operation(tags = "Remove book", summary = "Removes a book from the online library", description = "Process removes a book from the library database, by the given book id.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = BookRequest.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = HttpClientErrorException.BadRequest.class)), description = "Bad Request<br/><br/>* Book id is empty."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @DeleteMapping("/v1/book/id")
    public ResponseEntity<?> removeBook(@RequestParam(value = "bookId", required = false) Long bookId) {
        // Validate request param
        bookService.validateRequestParamBookId(bookId);
        // Call service
        bookService.removeBook(bookId);
        log.info("Book with id: {}, removed...", bookId);
        // Prepare and return response
        return ResponseEntity
                .ok()
                .body(bookId);
    }
}
