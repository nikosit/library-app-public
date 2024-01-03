package de.schwarz.libraryapp.book.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.schwarz.libraryapp.book.domain.dto.BookDto;
import de.schwarz.libraryapp.book.domain.dto.BookRequest;
import de.schwarz.libraryapp.book.service.BookService;
import de.schwarz.libraryapp.exception.NoContentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static de.schwarz.libraryapp.book.service.BookService.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = {BookResourceV1.class})
@ActiveProfiles(value = "dev")
class BookResourceV1Test {

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("Resource for detecting all books => successful")
    void detectAllBooks1() {
        try {
            // Setup
            final BookDto book = createBookDto();
            final List<BookDto> books = List.of(book);
            // Mocking the services
            when(bookService.detectAllBooks()).thenReturn(books);

            // Run the test
            mockMvc.perform(get("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.[0].bookId").value(books.get(0).getBookId()))
                    .andExpect(jsonPath("$.[0].author").value(books.get(0).getAuthor()))
                    .andExpect(jsonPath("$.[0].title").value(books.get(0).getTitle()))
                    .andExpect(jsonPath("$.[0].publisher").value(books.get(0).getPublisher()))
                    .andExpect(jsonPath("$.[0].publishingYear").value(books.get(0).getPublishingYear()))
                    .andExpect(jsonPath("$.[0].category").value(books.get(0).getCategoryDescription()));

            // Verify
            verify(bookService, times(1)).detectAllBooks();
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all books => successful - no content")
    void detectAllBooks2() {
        try {
            // Setup
            // Mocking the services
            when(bookService.detectAllBooks()).thenThrow(NoContentException.class);

            // Run the test
            mockMvc.perform(get("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verify
            verify(bookService, times(1)).detectAllBooks();
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all books => internal server error")
    void detectAllBooks3() {
        try {
            // Setup
            // Mocking the services
            when(bookService.detectAllBooks()).thenThrow(new InternalError(ERROR_BOOKS_ALL));

            // Run the test
            mockMvc.perform(get("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(ERROR_BOOKS_ALL));

            // Verify
            verify(bookService, times(1)).detectAllBooks();
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    /*@Test
    @DisplayName("Resource for detecting all books => error - unauthorized")
    void detectAllBooks4() {
        try {
            // Run the test
            mockMvc.perform(get("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all books => error - forbidden")
    void detectAllBooks5() {
        try {
            // Run the test
            mockMvc.perform(get("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }*/

    @Test
    @DisplayName("Resource for detecting all books for the given author => successful")
    void detectBooksFromAuthor1() {
        try {
            // Setup
            final BookDto book = createBookDto();
            final List<BookDto> books = List.of(book);
            // Mocking the services
            doNothing().when(bookService).validateRequestParamAuthor(book.getAuthor());
            when(bookService.detectBooksByAuthor(book.getAuthor())).thenReturn(books);

            // Run the test
            mockMvc.perform(get("/api/v1/books/author")
                            .param("author", book.getAuthor())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.[0].bookId").value(books.get(0).getBookId()))
                    .andExpect(jsonPath("$.[0].author").value(books.get(0).getAuthor()))
                    .andExpect(jsonPath("$.[0].title").value(books.get(0).getTitle()))
                    .andExpect(jsonPath("$.[0].publisher").value(books.get(0).getPublisher()))
                    .andExpect(jsonPath("$.[0].publishingYear").value(books.get(0).getPublishingYear()))
                    .andExpect(jsonPath("$.[0].category").value(books.get(0).getCategoryDescription()));

            // Verify
            verify(bookService, times(1)).validateRequestParamAuthor(book.getAuthor());
            verify(bookService, times(1)).detectBooksByAuthor(book.getAuthor());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all books for the given author => successful - no content")
    void detectBooksFromAuthor2() {
        try {
            // Setup
            final String author = "Harry Ken";
            // Mocking the services
            doNothing().when(bookService).validateRequestParamAuthor(author);
            when(bookService.detectBooksByAuthor(author)).thenThrow(NoContentException.class);

            // Run the test
            mockMvc.perform(get("/api/v1/books/author")
                            .param("author", author)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verify
            verify(bookService, times(1)).validateRequestParamAuthor(author);
            verify(bookService, times(1)).detectBooksByAuthor(author);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all books for the given author => error - bad request")
    void detectBooksFromAuthor3() {
        try {
            // Setup
            final String author = null;
            // Mocking the services
            doThrow(new IllegalArgumentException(ERROR_BOOK_AUTHOR_EMPTY)).when(bookService).validateRequestParamAuthor(author);

            // Run the test
            mockMvc.perform(get("/api/v1/books/author")
                            .param("author", author)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(ERROR_BOOK_AUTHOR_EMPTY));

            // Verify
            verify(bookService, times(1)).validateRequestParamAuthor(author);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all books for the given author => internal server error")
    void detectBooksFromAuthor4() {
        try {
            // Setup
            final String author = "Harry Ken";
            // Mocking the services
            doNothing().when(bookService).validateRequestParamAuthor(author);
            when(bookService.detectBooksByAuthor(author)).thenThrow(new InternalError(ERROR_BOOKS_AUTHOR));

            // Run the test
            mockMvc.perform(get("/api/v1/books/author")
                            .param("author", author)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(ERROR_BOOKS_AUTHOR));

            // Verify
            verify(bookService, times(1)).validateRequestParamAuthor(author);
            verify(bookService, times(1)).detectBooksByAuthor(author);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    /*@Test
    @DisplayName("Resource for detecting all books for the given author => error - unauthorized")
    void detectBooksFromAuthor5() {
        try {
            // Setup
            final String author = "Harry Ken";

            // Run the test
            mockMvc.perform(get("/api/v1/books/author")
                            .param("author", author)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all books for the given author => error - forbidden")
    void detectBooksFromAuthor6() {
        try {
            // Setup
            final String author = "Harry Ken";
            // Run the test
            mockMvc.perform(get("/api/v1/books/author")
                            .param("author", author)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }*/

    @Test
    @DisplayName("Resource for detecting all books for the given category => successful")
    void detectBooksInCategory1() {
        try {
            // Setup
            final BookDto book = createBookDto();
            final List<BookDto> books = List.of(book);
            // Mocking the services
            doNothing().when(bookService).validateRequestParamCategory(book.getCategoryDescription());
            when(bookService.detectBooksByCategory(book.getCategoryDescription())).thenReturn(books);

            // Run the test
            mockMvc.perform(get("/api/v1/books/category")
                            .param("category", book.getCategoryDescription())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.[0].bookId").value(books.get(0).getBookId()))
                    .andExpect(jsonPath("$.[0].author").value(books.get(0).getAuthor()))
                    .andExpect(jsonPath("$.[0].title").value(books.get(0).getTitle()))
                    .andExpect(jsonPath("$.[0].publisher").value(books.get(0).getPublisher()))
                    .andExpect(jsonPath("$.[0].publishingYear").value(books.get(0).getPublishingYear()))
                    .andExpect(jsonPath("$.[0].category").value(books.get(0).getCategoryDescription()));

            // Verify
            verify(bookService, times(1)).validateRequestParamCategory(book.getCategoryDescription());
            verify(bookService, times(1)).detectBooksByCategory(book.getCategoryDescription());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all books for the given category => successful - no content")
    void detectBooksInCategory2() {
        try {
            // Setup
            final String category = "Sci-Fi";
            // Mocking the services
            doNothing().when(bookService).validateRequestParamCategory(category);
            when(bookService.detectBooksByCategory(category)).thenThrow(NoContentException.class);

            // Run the test
            mockMvc.perform(get("/api/v1/books/category")
                            .param("category", category)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verify
            verify(bookService, times(1)).validateRequestParamCategory(category);
            verify(bookService, times(1)).detectBooksByCategory(category);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all books for the given category => error - bad request")
    void detectBooksInCategory3() {
        try {
            // Setup
            final String category = "";
            // Mocking the services
            doThrow(new IllegalArgumentException(ERROR_BOOK_CATEGORY_EMPTY)).when(bookService).validateRequestParamCategory(category);

            // Run the test
            mockMvc.perform(get("/api/v1/books/category")
                            .param("category", category)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(ERROR_BOOK_CATEGORY_EMPTY));

            // Verify
            verify(bookService, times(1)).validateRequestParamCategory(category);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all books for the given category => internal server error")
    void detectBooksInCategory4() {
        try {
            // Setup
            final String category = "Sci-Fi";
            // Mocking the services
            doNothing().when(bookService).validateRequestParamCategory(category);
            when(bookService.detectBooksByCategory(category)).thenThrow(new InternalError(ERROR_BOOKS_CATEGORY));

            // Run the test
            mockMvc.perform(get("/api/v1/books/category")
                            .param("category", category)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(ERROR_BOOKS_CATEGORY));

            // Verify
            verify(bookService, times(1)).validateRequestParamCategory(category);
            verify(bookService, times(1)).detectBooksByCategory(category);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    /*@Test
    @DisplayName("Resource for detecting all books for the given category => error - unauthorized")
    void detectBooksInCategory5() {
        try {
            // Setup
            final String category = "Sci-Fi";

            // Run the test
            mockMvc.perform(get("/api/v1/books/category")
                            .param("category", category)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all books for the given category => error - forbidden")
    void detectBooksInCategory6() {
        try {
            // Setup
            final String category = "Sci-Fi";

            // Run the test
            mockMvc.perform(get("/api/v1/books/category")
                            .param("category", category)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }*/

    @Test
    @DisplayName("Resource for detecting a book by the given book id => successful")
    void detectBook1() {
        try {
            // Setup
            final BookDto book = createBookDto();
            final Long bookId = book.getBookId();
            // Mocking the services
            doNothing().when(bookService).validateRequestParamBookId(bookId);
            when(bookService.detectBook(bookId)).thenReturn(book);

            // Run the test
            mockMvc.perform(get("/api/v1/book/id")
                            .param("bookId", String.valueOf(bookId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.bookId").value(book.getBookId()))
                    .andExpect(jsonPath("$.author").value(book.getAuthor()))
                    .andExpect(jsonPath("$.title").value(book.getTitle()))
                    .andExpect(jsonPath("$.publisher").value(book.getPublisher()))
                    .andExpect(jsonPath("$.publishingYear").value(book.getPublishingYear()))
                    .andExpect(jsonPath("$.category").value(book.getCategoryDescription()));

            // Verify
            verify(bookService, times(1)).validateRequestParamBookId(bookId);
            verify(bookService, times(1)).detectBook(bookId);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting a book by the given book id => successful - no content")
    void detectBook2() {
        try {
            // Setup
            final Long bookId = 99999L;
            // Mocking the services
            doNothing().when(bookService).validateRequestParamBookId(bookId);
            when(bookService.detectBook(bookId)).thenThrow(NoContentException.class);

            // Run the test
            mockMvc.perform(get("/api/v1/book/id")
                            .param("bookId", String.valueOf(bookId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verify
            verify(bookService, times(1)).validateRequestParamBookId(bookId);
            verify(bookService, times(1)).detectBook(bookId);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting a book by the given book id => error - bad request")
    void detectBook3() {
        try {
            // Setup
            final Long bookId = null;
            // Mocking the services
            doThrow(new IllegalArgumentException(ERROR_BOOK_ID_EMPTY)).when(bookService).validateRequestParamBookId(bookId);

            // Run the test
            mockMvc.perform(get("/api/v1/book/id")
                            .param("bookId", (String) null)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(ERROR_BOOK_ID_EMPTY));

            // Verify
            verify(bookService, times(1)).validateRequestParamBookId(bookId);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting a book by the given book id => internal server error")
    void detectBook4() {
        try {
            // Setup
            final Long bookId = -99999L;
            // Mocking the services
            doNothing().when(bookService).validateRequestParamBookId(bookId);
            when(bookService.detectBook(bookId)).thenThrow(new InternalError(ERROR_BOOKS_ID));

            // Run the test
            mockMvc.perform(get("/api/v1/book/id")
                            .param("bookId", String.valueOf(bookId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(ERROR_BOOKS_ID));

            // Verify
            verify(bookService, times(1)).validateRequestParamBookId(bookId);
            verify(bookService, times(1)).detectBook(bookId);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    /*@Test
    @DisplayName("Resource for detecting a book by the given book id => error - not authorized")
    void detectBook5() {
        try {
            // Setup
            final Long bookId = 99999L;
            // Run the test
            mockMvc.perform(get("/api/v1/book/id")
                            .param("bookId", String.valueOf(bookId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting a book by the given book id => error - forbidden")
    void detectBook6() {
        try {
            // Setup
            final Long bookId = 99999L;
            // Run the test
            mockMvc.perform(get("/api/v1/book/id")
                            .param("bookId", String.valueOf(bookId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }*/

    @Test
    @DisplayName("Resource for create or update a book => successful")
    void saveBook1() {
        try {
            // Setup
            final BookDto book = createBookDto();
            final BookRequest request = createBookRequest(book);
            // Mocking the services
            doNothing().when(bookService).validateRequestParams(request);
            when(bookService.createOrUpdateBook(request)).thenReturn(book);

            // Run the test
            mockMvc.perform(post("/api/v1/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bookId").value(book.getBookId()))
                    .andExpect(jsonPath("$.author").value(book.getAuthor()))
                    .andExpect(jsonPath("$.title").value(book.getTitle()))
                    .andExpect(jsonPath("$.publisher").value(book.getPublisher()))
                    .andExpect(jsonPath("$.publishingYear").value(book.getPublishingYear()))
                    .andExpect(jsonPath("$.category").value(book.getCategoryDescription()));

            // Verify
            verify(bookService, times(1)).validateRequestParams(request);
            verify(bookService, times(1)).createOrUpdateBook(request);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for create or update a book => successful - no content")
    void saveBook2() {
        try {
            // Setup
            final BookDto book = createBookDto();
            final BookRequest request = createBookRequest(book);
            // Mocking the services
            doNothing().when(bookService).validateRequestParams(request);
            when(bookService.createOrUpdateBook(request)).thenThrow(NoContentException.class);

            // Run the test
            mockMvc.perform(post("/api/v1/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            // Verify
            verify(bookService, times(1)).validateRequestParams(request);
            verify(bookService, times(1)).createOrUpdateBook(request);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for create or update a book => error - bad request - empty request")
    void saveBook3() {
        try {
            // Setup
            final BookRequest request = null;
            // Mocking the services
            doThrow(new IllegalArgumentException(ERROR_BOOK_REQUEST_EMPTY)).when(bookService).validateRequestParams(request);

            // Run the test
            mockMvc.perform(post("/api/v1/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(ERROR_BOOK_REQUEST_EMPTY));

            // Verify
            verify(bookService, times(1)).validateRequestParams(request);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for create or update a book => error - bad request - empty publisher")
    void saveBook4() {
        try {
            // Setup
            final BookDto book = createBookDto();
            book.setPublisher(null);
            final BookRequest request = createBookRequest(book);
            // Mocking the services
            doThrow(new IllegalArgumentException(ERROR_BOOK_REQUEST_PUBLISHER_EMPTY)).when(bookService).validateRequestParams(request);

            // Run the test
            mockMvc.perform(post("/api/v1/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(ERROR_BOOK_REQUEST_PUBLISHER_EMPTY));

            // Verify
            verify(bookService, times(1)).validateRequestParams(request);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for create or update a book => internal server error")
    void saveBook5() {
        try {
            // Setup
            final BookDto book = createBookDto();
            final BookRequest request = createBookRequest(book);
            // Mocking the services
            doNothing().when(bookService).validateRequestParams(request);
            when(bookService.createOrUpdateBook(request)).thenThrow(new InternalError(ERROR_BOOKS_SAVE));

            // Run the test
            mockMvc.perform(post("/api/v1/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(ERROR_BOOKS_SAVE));

            // Verify
            verify(bookService, times(1)).validateRequestParams(request);
            verify(bookService, times(1)).createOrUpdateBook(request);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    /*@Test
    @DisplayName("Resource for create or update a book => error - not authorized")
    void saveBook6() {
        try {
            // Setup
            final BookDto book = createBookDto();
            final BookRequest request = createBookRequest(book);

            // Run the test
            mockMvc.perform(post("/api/v1/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for create or update a book => error - forbidden")
    void saveBook7() {
        try {
            // Setup
            final BookDto book = createBookDto();
            final BookRequest request = createBookRequest(book);

            // Run the test
            mockMvc.perform(post("/api/v1/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }*/

    @Test
    @DisplayName("Resource for removing a book for the given book id => successful")
    void removeBook1() {
        try {
            // Setup
            final Long bookId = 52L;
            // Mocking the services
            doNothing().when(bookService).validateRequestParamBookId(bookId);
            doNothing().when(bookService).removeBook(bookId);

            // Run the test
            mockMvc.perform(delete("/api/v1/book/id")
                            .param("bookId", String.valueOf(bookId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(String.valueOf(bookId)));

            // Verify
            verify(bookService, times(1)).validateRequestParamBookId(bookId);
            verify(bookService, times(1)).removeBook(bookId);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for removing a book for the given book id => successful - no content")
    void removeBook2() {
        try {
            // Setup
            final Long bookId = 99999L;
            // Mocking the services
            doNothing().when(bookService).validateRequestParamBookId(bookId);
            doThrow(NoContentException.class).when(bookService).removeBook(bookId);

            // Run the test
            mockMvc.perform(delete("/api/v1/book/id")
                            .param("bookId", String.valueOf(bookId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verify
            verify(bookService, times(1)).validateRequestParamBookId(bookId);
            verify(bookService, times(1)).removeBook(bookId);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for removing a book for the given book id => error - bad request")
    void removeBook3() {
        try {
            // Setup
            final Long bookId = null;
            // Mocking the services
            doThrow(new IllegalArgumentException(ERROR_BOOK_ID_EMPTY)).when(bookService).validateRequestParamBookId(bookId);

            // Run the test
            mockMvc.perform(delete("/api/v1/book/id")
                            .param("bookId", (String) null)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(ERROR_BOOK_ID_EMPTY));

            // Verify
            verify(bookService, times(1)).validateRequestParamBookId(bookId);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for removing a book for the given book id => internal server error")
    void removeBook4() {
        try {
            // Setup
            final Long bookId = -99999L;
            // Mocking the services
            doNothing().when(bookService).validateRequestParamBookId(bookId);
            doThrow(new InternalError(ERROR_BOOKS_ID)).when(bookService).removeBook(bookId);

            // Run the test
            mockMvc.perform(delete("/api/v1/book/id")
                            .param("bookId", String.valueOf(bookId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(ERROR_BOOKS_ID));

            // Verify
            verify(bookService, times(1)).validateRequestParamBookId(bookId);
            verify(bookService, times(1)).removeBook(bookId);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    /*@Test
    @DisplayName("Resource for removing a book for the given book id => error - not authorized")
    void removeBook5() {
        try {
            // Setup
            final Long bookId = 99999L;
            // Run the test
            mockMvc.perform(delete("/api/v1/book/id")
                            .param("bookId", String.valueOf(bookId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for removing a book for the given book id => error - forbidden")
    void removeBook6() {
        try {
            // Setup
            final Long bookId = 99999L;
            // Run the test
            mockMvc.perform(delete("/api/v1/book/id")
                            .param("bookId", String.valueOf(bookId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }*/

    private BookDto createBookDto() {
        BookDto book = new BookDto();
        book.setBookId(1L);
        book.setAuthor("Steven King");
        book.setTitle("New Year's Eve");
        book.setPublisher("Books Online AG");
        book.setPublishingYear("2023-12-31");
        book.setCategoryDescription("Horror");

        return book;
    }

    private BookRequest createBookRequest(BookDto bookDto) {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setBookId(bookDto.getBookId());
        bookRequest.setAuthor(bookDto.getAuthor());
        bookRequest.setTitle(bookDto.getTitle());
        bookRequest.setPublisher(bookDto.getPublisher());
        bookRequest.setPublishingYear(LocalDate.parse(bookDto.getPublishingYear(), DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        bookRequest.setCategory(bookDto.getCategoryDescription());

        return bookRequest;
    }
}