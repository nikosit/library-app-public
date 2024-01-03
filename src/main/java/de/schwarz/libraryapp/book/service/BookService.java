package de.schwarz.libraryapp.book.service;


import de.schwarz.libraryapp.book.domain.BookRepository;
import de.schwarz.libraryapp.book.domain.dto.BookDto;
import de.schwarz.libraryapp.book.domain.dto.BookRequest;
import de.schwarz.libraryapp.book.domain.entity.Book;
import de.schwarz.libraryapp.category.domain.dto.CategoryDto;
import de.schwarz.libraryapp.category.service.CategoryService;
import de.schwarz.libraryapp.exception.NoContentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookService {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String ERROR_BOOKS_ALL = "error.books.all";
    public static final String ERROR_BOOKS_AUTHOR = "error.books.author";
    public static final String ERROR_BOOKS_CATEGORY = "error.books.category";
    public static final String ERROR_BOOKS_ID = "error.books.id";
    public static final String ERROR_BOOKS_SAVE = "error.books.save";
    public static final String ERROR_BOOK_AUTHOR_EMPTY = "error.book.author.empty";
    public static final String ERROR_BOOK_CATEGORY_EMPTY = "error.book.category.empty";
    public static final String ERROR_BOOK_ID_EMPTY = "error.book.id.empty";
    public static final String ERROR_BOOK_REQUEST_EMPTY = "error.book.request.empty";
    public static final String ERROR_BOOK_REQUEST_AUTHOR_EMPTY = "error.book.request.author.empty";
    public static final String ERROR_BOOK_REQUEST_TITLE_EMPTY = "error.book.request.title.empty";
    public static final String ERROR_BOOK_REQUEST_PUBLISHER_EMPTY = "error.book.request.publisher.empty";
    public static final String ERROR_BOOK_REQUEST_PUBLISHING_YEAR_EMPTY = "error.book.request.publishing_year.empty";
    public static final String ERROR_BOOK_REQUEST_CATEGORY_EMPTY = "error.book.request.category.empty";


    private final BookRepository bookRepository;
    private final CategoryService categoryService;


    /**
     * Validates request param author
     *
     * @param author
     */
    public void validateRequestParamAuthor(final String author) {
        if (!StringUtils.hasText(author)) {
            throw new IllegalArgumentException(ERROR_BOOK_AUTHOR_EMPTY);
        }
    }

    /**
     * Validates request param category
     *
     * @param category
     */
    public void validateRequestParamCategory(final String category) {
        if (!StringUtils.hasText(category)) {
            throw new IllegalArgumentException(ERROR_BOOK_CATEGORY_EMPTY);
        }
    }

    /**
     * Validates request param book id.
     *
     * @param bookId
     */
    public void validateRequestParamBookId(Long bookId) {
        if (ObjectUtils.isEmpty(bookId)) {
            throw new IllegalArgumentException(ERROR_BOOK_ID_EMPTY);
        }
    }

    /**
     * Validates request params
     *
     * @param request
     */
    public void validateRequestParams(final BookRequest request) {
        if (ObjectUtils.isEmpty(request)) {
            throw new IllegalArgumentException(ERROR_BOOK_REQUEST_EMPTY);
        }

        if (!StringUtils.hasText(request.getAuthor())) {
            throw new IllegalArgumentException(ERROR_BOOK_REQUEST_AUTHOR_EMPTY);
        }

        if (!StringUtils.hasText(request.getTitle())) {
            throw new IllegalArgumentException(ERROR_BOOK_REQUEST_TITLE_EMPTY);
        }

        if (!StringUtils.hasText(request.getPublisher())) {
            throw new IllegalArgumentException(ERROR_BOOK_REQUEST_PUBLISHER_EMPTY);
        }

        if (ObjectUtils.isEmpty(request.getPublishingYear())) {
            throw new IllegalArgumentException(ERROR_BOOK_REQUEST_PUBLISHING_YEAR_EMPTY);
        }

        if (!StringUtils.hasText(request.getCategory())) {
            throw new IllegalArgumentException(ERROR_BOOK_REQUEST_CATEGORY_EMPTY);
        }
    }

    /**
     * Detects all books by the given author
     *
     * @return
     */
    @Transactional
    public List<BookDto> detectAllBooks() {
        try {
            var books = bookRepository.findAll();
            if (books.isEmpty()) {
                throw new NoContentException();
            }

            return books.stream()
                    .map(this::createBookDto)
                    .toList();
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during detecting all books...", e);
            throw new InternalError(ERROR_BOOKS_ALL);
        }
    }


    /**
     * Detects all books by the given author
     *
     * @param author
     * @return
     */
    @Transactional
    public List<BookDto> detectBooksByAuthor(String author) {
        try {
            var books = bookRepository.findByAuthor(author.trim());
            if (books.isEmpty()) {
                throw new NoContentException();
            }

            return books.stream()
                    .map(this::createBookDto)
                    .toList();
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during detecting books by author: {}", author, e);
            throw new InternalError(ERROR_BOOKS_AUTHOR);
        }
    }

    /**
     * Detects all books by the given category
     *
     * @param category
     * @return
     */
    @Transactional
    public List<BookDto> detectBooksByCategory(String category) {
        try {
            var books = bookRepository.findByCategory(category);
            if (books.isEmpty()) {
                throw new NoContentException();
            }

            return books.stream()
                    .map(this::createBookDto)
                    .toList();
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during detecting books by category id: {}", category, e);
            throw new InternalError(ERROR_BOOKS_CATEGORY);
        }
    }

    /**
     * Detects a book by the given book id.
     *
     * @param bookId
     * @return
     */
    @Transactional
    public BookDto detectBook(Long bookId) {
        try {
            var book = bookRepository.findById(bookId);
            if (book.isEmpty()) {
                throw new NoContentException();
            }

            return createBookDto(book.get());
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during detecting book by book id: {}", bookId, e);
            throw new InternalError(ERROR_BOOKS_ID);
        }
    }

    /**
     * Creates or updates a book in online library
     *
     * @param bookRequest
     * @return
     */
    @Transactional
    public BookDto createOrUpdateBook(BookRequest bookRequest) {
        try {
            CategoryDto category = categoryService.detectCategoryByDescription(bookRequest.getCategory());
            Book book = createBookEntityFromRequest(bookRequest);
            book.setCategoryId(category.getCategoryId());

            var bookNew = bookRepository.save(book);
            BookDto bookCreatedOrUpdated = createBookDto(bookNew);
            bookCreatedOrUpdated.setCategoryDescription(category.getDescription());

            return bookCreatedOrUpdated;
        } catch (DataIntegrityViolationException | NoContentException e) {
            rollback();
            if (e instanceof NoContentException) {
                log.error("Category: {} not found...", bookRequest.getCategory());
            } else {
                log.error("Exception during creating book for the author: {}", bookRequest.getAuthor(), e);
            }

            throw new InternalError(ERROR_BOOKS_SAVE);
        }
    }

    /**
     * Removes a book for the given book id.
     *
     * @param bookId
     * @return
     */
    @Transactional
    public void removeBook(Long bookId) {
        try {
            bookRepository.deleteById(bookId);
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during removing book for the book id: {}", bookId, e);
            throw new InternalError(ERROR_BOOKS_ID);
        }
    }

    /**
     * Creates from entity book the dto.
     *
     * @param book
     * @return
     */
    protected BookDto createBookDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setBookId(book.getId());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setTitle(book.getTitle());
        bookDto.setPublisher(book.getPublisher());
        bookDto.setPublishingYear(String.valueOf(book.getPublishingYear().getYear()));

        return bookDto;
    }

    /**
     * Creates an entity from request.
     *
     * @param bookRequest
     * @return
     */
    protected Book createBookEntityFromRequest(BookRequest bookRequest) {
        Book book = new Book();
        book.setId(bookRequest.getBookId());
        book.setAuthor(bookRequest.getAuthor());
        book.setTitle(bookRequest.getTitle());
        book.setPublisher(bookRequest.getPublisher());
        book.setPublishingYear(bookRequest.getPublishingYear());
        book.setUpdatedOn(!ObjectUtils.isEmpty(book.getId()) ? LocalDateTime.now() : null);

        return book;
    }

    /**
     * For testing purposes refactored.
     */
    protected void rollback() {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}
