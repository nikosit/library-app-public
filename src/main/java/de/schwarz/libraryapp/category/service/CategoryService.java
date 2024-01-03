package de.schwarz.libraryapp.category.service;


import de.schwarz.libraryapp.category.domain.CategoryRepository;
import de.schwarz.libraryapp.category.domain.dto.CategoryDto;
import de.schwarz.libraryapp.category.domain.dto.CategoryRequest;
import de.schwarz.libraryapp.exception.NoContentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryService {

    public static final String ERROR_CATEGORIES_ALL = "error.categories.all";
    public static final String ERROR_CATEGORIES_DESCRIPTION = "error.categories.description";
    public static final String ERROR_CATEGORY_DESCRIPTION = "error.category.description";
    public static final String ERROR_CATEGORIES_ID = "error.categories.id";
    public static final String ERROR_CATEGORIES_SAVE = "error.categories.save";
    public static final String ERROR_CATEGORIES_DESCRIPTION_EMPTY = "error.category.description.empty";
    public static final String ERROR_CATEGORIES_ID_EMPTY = "error.category.id.empty";
    public static final String ERROR_CATEGORIES_REQUEST_EMPTY = "error.category.request.empty";
    public static final String ERROR_CATEGORIES_REQUEST_DESCRIPTION_EMPTY = "error.category.request.description.empty";


    private final CategoryRepository categoryRepository;


    /**
     * Validates request param description
     *
     * @param description
     */
    public void validateRequestParamDescription(final String description) {
        if (!StringUtils.hasText(description)) {
            throw new IllegalArgumentException(ERROR_CATEGORIES_DESCRIPTION_EMPTY);
        }
    }

    /**
     * Validates request param book id.
     *
     * @param bookId
     */
    public void validateRequestParamCategoryId(Long bookId) {
        if (ObjectUtils.isEmpty(bookId)) {
            throw new IllegalArgumentException(ERROR_CATEGORIES_ID_EMPTY);
        }
    }

    /**
     * Validates request params
     *
     * @param request
     */
    public void validateRequestParams(final CategoryRequest request) {
        if (ObjectUtils.isEmpty(request)) {
            throw new IllegalArgumentException(ERROR_CATEGORIES_REQUEST_EMPTY);
        }

        if (!StringUtils.hasText(request.getDescription())) {
            throw new IllegalArgumentException(ERROR_CATEGORIES_REQUEST_DESCRIPTION_EMPTY);
        }
    }

    /**
     * Detects all categories by the given author
     *
     * @return
     */
    @Transactional
    public List<CategoryDto> detectAllCategories() {
        try {
            var categories = categoryRepository.findAll();
            if (categories.isEmpty()) {
                throw new NoContentException();
            }

            return categories;
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during detecting all categories...", e);
            throw new InternalError(ERROR_CATEGORIES_ALL);
        }
    }


    /**
     * Detects all categories by the given description
     *
     * @param description
     * @return
     */
    @Transactional
    public List<CategoryDto> detectCategoriesByDescription(String description) {
        try {
            var categories = categoryRepository.findByDescription(description.trim());
            if (categories.isEmpty()) {
                throw new NoContentException();
            }

            return categories;
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during detecting categories by description: {}", description, e);
            throw new InternalError(ERROR_CATEGORIES_DESCRIPTION);
        }
    }

    /**
     * Detects a specific category by the given description
     *
     * @param description
     * @return
     */
    public CategoryDto detectCategoryByDescription(String description) {
        try {
            var category = categoryRepository.findByDescriptionStrict(description.trim());
            if (category.isEmpty()) {
                throw new NoContentException();
            }

            return category.get();
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during detecting category by description: {}", description, e);
            throw new InternalError(ERROR_CATEGORY_DESCRIPTION);
        }
    }

    /**
     * Detects a category by the given category id.
     *
     * @param categoryId
     * @return
     */
    @Transactional
    public CategoryDto detectCategory(Long categoryId) {
        try {
            var category = categoryRepository.findById(categoryId);
            if (category.isEmpty()) {
                throw new NoContentException();
            }

            return category.get();
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during detecting category by category id: {}", categoryId, e);
            throw new InternalError(ERROR_CATEGORIES_ID);
        }
    }

    /**
     * Creates or updates a category in online library
     *
     * @param categoryRequest
     * @return
     */
    @Transactional
    public Integer createOrUpdateCategory(CategoryRequest categoryRequest) {
        try {
            CategoryDto category = createCategoryFromRequest(categoryRequest);
            return categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during creating category with the description: {}", categoryRequest.getDescription(), e);
            throw new InternalError(ERROR_CATEGORIES_SAVE);
        }
    }

    /**
     * Removes a category for the given category id.
     *
     * @param categoryId
     * @return
     */
    @Transactional
    public Integer removeCategory(Long categoryId) {
        try {
            return categoryRepository.deleteById(categoryId);
        } catch (DataIntegrityViolationException e) {
            rollback();
            log.error("Exception during removing category for the category id: {}", categoryId, e);
            throw new InternalError(ERROR_CATEGORIES_ID);
        }
    }

    /**
     * Creates an entity from request.
     *
     * @param categoryRequest
     * @return
     */
    protected CategoryDto createCategoryFromRequest(CategoryRequest categoryRequest) {
        CategoryDto category = new CategoryDto();
        category.setCategoryId(categoryRequest.getCategoryId());
        category.setDescription(categoryRequest.getDescription());

        return category;
    }

    /**
     * For testing purposes refactored.
     */
    protected void rollback() {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}
