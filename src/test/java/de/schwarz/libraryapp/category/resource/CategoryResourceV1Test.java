package de.schwarz.libraryapp.category.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.schwarz.libraryapp.category.domain.dto.CategoryDto;
import de.schwarz.libraryapp.category.domain.dto.CategoryRequest;
import de.schwarz.libraryapp.category.service.CategoryService;
import de.schwarz.libraryapp.exception.NoContentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static de.schwarz.libraryapp.book.service.BookService.ERROR_BOOKS_ID;
import static de.schwarz.libraryapp.book.service.BookService.ERROR_BOOK_ID_EMPTY;
import static de.schwarz.libraryapp.category.service.CategoryService.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {CategoryResourceV1.class})
@TestPropertySource(properties = {"APP_MODE=dev"})
class CategoryResourceV1Test {

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("Resource for detecting all categories => successful")
    void detectAllCategories1() {
        try {
            // Setup
            final CategoryDto category = createCategoryDto();
            final List<CategoryDto> categories = List.of(category);
            // Mocking the services
            when(categoryService.detectAllCategories()).thenReturn(categories);

            // Run the test
            mockMvc.perform(get("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.[0].categoryId").value(categories.get(0).getCategoryId()))
                    .andExpect(jsonPath("$.[0].description").value(categories.get(0).getDescription()))
                    .andExpect(jsonPath("$.[0].booksCount").value(categories.get(0).getBooksCount()));

            // Verify
            verify(categoryService, times(1)).detectAllCategories();
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all categories => successful - no content")
    void detectAllCategories2() {
        try {
            // Setup
            // Mocking the services
            when(categoryService.detectAllCategories()).thenThrow(NoContentException.class);

            // Run the test
            mockMvc.perform(get("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verify
            verify(categoryService, times(1)).detectAllCategories();
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all categories => internal server error")
    void detectAllCategories3() {
        try {
            // Setup
            // Mocking the services
            when(categoryService.detectAllCategories()).thenThrow(new InternalError(ERROR_CATEGORIES_ALL));

            // Run the test
            mockMvc.perform(get("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(ERROR_CATEGORIES_ALL));

            // Verify
            verify(categoryService, times(1)).detectAllCategories();
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    /*@Test
    @DisplayName("Resource for detecting all categories => error - unauthorized")
    void detectAllCategories4() {
        try {
            // Run the test
            mockMvc.perform(get("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all categories => error - forbidden")
    void detectAllCategories5() {
        try {
            // Run the test
            mockMvc.perform(get("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }*/

    @Test
    @DisplayName("Resource for detecting all categories for the given description => successful")
    void detectCategoriesFromDescription1() {
        try {
            // Setup
            final CategoryDto category = createCategoryDto();
            final List<CategoryDto> categories = List.of(category);
            // Mocking the services
            doNothing().when(categoryService).validateRequestParamDescription(category.getDescription());
            when(categoryService.detectCategoriesByDescription(category.getDescription())).thenReturn(categories);

            // Run the test
            mockMvc.perform(get("/api/v1/categories/description")
                            .param("description", category.getDescription())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.[0].categoryId").value(categories.get(0).getCategoryId()))
                    .andExpect(jsonPath("$.[0].description").value(categories.get(0).getDescription()))
                    .andExpect(jsonPath("$.[0].booksCount").value(categories.get(0).getBooksCount()));

            // Verify
            verify(categoryService, times(1)).validateRequestParamDescription(category.getDescription());
            verify(categoryService, times(1)).detectCategoriesByDescription(category.getDescription());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all categories for the given description => successful - no content")
    void detectCategoriesFromDescription2() {
        try {
            // Setup
            final String description = "Horror";
            // Mocking the services
            doNothing().when(categoryService).validateRequestParamDescription(description);
            when(categoryService.detectCategoriesByDescription(description)).thenThrow(NoContentException.class);

            // Run the test
            mockMvc.perform(get("/api/v1/categories/description")
                            .param("description", description)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verify
            verify(categoryService, times(1)).validateRequestParamDescription(description);
            verify(categoryService, times(1)).detectCategoriesByDescription(description);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all categories for the given description => error - bad request")
    void detectCategoriesFromDescription3() {
        try {
            // Setup
            final String description = null;
            // Mocking the services
            doThrow(new IllegalArgumentException(ERROR_CATEGORIES_DESCRIPTION_EMPTY)).when(categoryService).validateRequestParamDescription(description);

            // Run the test
            mockMvc.perform(get("/api/v1/categories/description")
                            .param("description", description)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(ERROR_CATEGORIES_DESCRIPTION_EMPTY));

            // Verify
            verify(categoryService, times(1)).validateRequestParamDescription(description);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all categories for the given description => internal server error")
    void detectCategoriesFromDescription4() {
        try {
            // Setup
            final String description = "Horror";
            // Mocking the services
            doNothing().when(categoryService).validateRequestParamDescription(description);
            when(categoryService.detectCategoriesByDescription(description)).thenThrow(new InternalError(ERROR_CATEGORIES_DESCRIPTION));

            // Run the test
            mockMvc.perform(get("/api/v1/categories/description")
                            .param("description", description)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(ERROR_CATEGORIES_DESCRIPTION));

            // Verify
            verify(categoryService, times(1)).validateRequestParamDescription(description);
            verify(categoryService, times(1)).detectCategoriesByDescription(description);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    /*@Test
    @DisplayName("Resource for detecting all categories for the given description => error - unauthorized")
    void detectCategoriesFromDescription5() {
        try {
            // Setup
            final String description = "Horror";

            // Run the test
            mockMvc.perform(get("/api/v1/categories/description")
                            .param("description", description)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting all categories for the given description => error - forbidden")
    void detectCategoriesFromDescription6() {
        try {
            // Setup
            final String description = "Horror";

            // Run the test
            mockMvc.perform(get("/api/v1/categories/description")
                            .param("description", description)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }*/

    @Test
    @DisplayName("Resource for detecting a category for the given description => successful")
    void detectCategoryFromDescription1() {
        try {
            // Setup
            final CategoryDto category = createCategoryDto();
            // Mocking the services
            doNothing().when(categoryService).validateRequestParamDescription(category.getDescription());
            when(categoryService.detectCategoryByDescription(category.getDescription())).thenReturn(category);

            // Run the test
            mockMvc.perform(get("/api/v1/category/description")
                            .param("description", category.getDescription())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.categoryId").value(category.getCategoryId()))
                    .andExpect(jsonPath("$.description").value(category.getDescription()))
                    .andExpect(jsonPath("$.booksCount").value(category.getBooksCount()));

            // Verify
            verify(categoryService, times(1)).validateRequestParamDescription(category.getDescription());
            verify(categoryService, times(1)).detectCategoryByDescription(category.getDescription());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting a category for the given description => successful - no content")
    void detectCategoryFromDescription2() {
        try {
            // Setup
            final String description = "Horror";
            // Mocking the services
            doNothing().when(categoryService).validateRequestParamDescription(description);
            when(categoryService.detectCategoryByDescription(description)).thenThrow(NoContentException.class);

            // Run the test
            mockMvc.perform(get("/api/v1/category/description")
                            .param("description", description)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verify
            verify(categoryService, times(1)).validateRequestParamDescription(description);
            verify(categoryService, times(1)).detectCategoryByDescription(description);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting a category for the given description => error - bad request")
    void detectCategoryFromDescription3() {
        try {
            // Setup
            final String description = null;
            // Mocking the services
            doThrow(new IllegalArgumentException(ERROR_CATEGORIES_DESCRIPTION_EMPTY)).when(categoryService).validateRequestParamDescription(description);

            // Run the test
            mockMvc.perform(get("/api/v1/category/description")
                            .param("description", description)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(ERROR_CATEGORIES_DESCRIPTION_EMPTY));

            // Verify
            verify(categoryService, times(1)).validateRequestParamDescription(description);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting a category for the given description => internal server error")
    void detectCategoryFromDescription4() {
        try {
            // Setup
            final String description = "Horror";
            // Mocking the services
            doNothing().when(categoryService).validateRequestParamDescription(description);
            when(categoryService.detectCategoryByDescription(description)).thenThrow(new InternalError(ERROR_CATEGORY_DESCRIPTION));

            // Run the test
            mockMvc.perform(get("/api/v1/category/description")
                            .param("description", description)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(ERROR_CATEGORY_DESCRIPTION));

            // Verify
            verify(categoryService, times(1)).validateRequestParamDescription(description);
            verify(categoryService, times(1)).detectCategoryByDescription(description);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    /*@Test
    @DisplayName("Resource for detecting a category for the given description => error - unauthorized")
    void detectCategoriesFromDescription5() {
        try {
            // Setup
            final String description = "Horror";

            // Run the test
            mockMvc.perform(get("/api/v1/category/description")
                            .param("description", description)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting a category for the given description => error - forbidden")
    void detectCategoriesFromDescription6() {
        try {
            // Setup
            final String description = "Horror";

            // Run the test
            mockMvc.perform(get("/api/v1/category/description")
                            .param("description", description)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }*/

    @Test
    @DisplayName("Resource for detecting a category by the given category id => successful")
    void detectCategory1() {
        try {
            // Setup
            final CategoryDto category = createCategoryDto();
            // Mocking the services
            doNothing().when(categoryService).validateRequestParamCategoryId(category.getCategoryId());
            when(categoryService.detectCategory(category.getCategoryId())).thenReturn(category);

            // Run the test
            mockMvc.perform(get("/api/v1/category/id")
                            .param("categoryId", String.valueOf(category.getCategoryId()))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.categoryId").value(category.getCategoryId()))
                    .andExpect(jsonPath("$.description").value(category.getDescription()))
                    .andExpect(jsonPath("$.booksCount").value(category.getBooksCount()));

            // Verify
            verify(categoryService, times(1)).validateRequestParamCategoryId(category.getCategoryId());
            verify(categoryService, times(1)).detectCategory(category.getCategoryId());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting a category by the given category id => successful - no content")
    void detectCategory2() {
        try {
            // Setup
            final Long categoryId = 99999L;
            // Mocking the services
            doNothing().when(categoryService).validateRequestParamCategoryId(categoryId);
            when(categoryService.detectCategory(categoryId)).thenThrow(NoContentException.class);

            // Run the test
            mockMvc.perform(get("/api/v1/category/id")
                            .param("categoryId", String.valueOf(categoryId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verify
            verify(categoryService, times(1)).validateRequestParamCategoryId(categoryId);
            verify(categoryService, times(1)).detectCategory(categoryId);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting a category by the given category id => error - bad request")
    void detectCategory3() {
        try {
            // Setup
            final Long categoryId = null;
            // Mocking the services
            doThrow(new IllegalArgumentException(ERROR_BOOK_ID_EMPTY)).when(categoryService).validateRequestParamCategoryId(categoryId);

            // Run the test
            mockMvc.perform(get("/api/v1/category/id")
                            .param("categoryId", (String) null)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(ERROR_BOOK_ID_EMPTY));

            // Verify
            verify(categoryService, times(1)).validateRequestParamCategoryId(categoryId);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting a category by the given category id => internal server error")
    void detectCategory4() {
        try {
            // Setup
            final Long categoryId = -99999L;
            // Mocking the services
            doNothing().when(categoryService).validateRequestParamCategoryId(categoryId);
            when(categoryService.detectCategory(categoryId)).thenThrow(new InternalError(ERROR_BOOKS_ID));

            // Run the test
            mockMvc.perform(get("/api/v1/category/id")
                            .param("categoryId", String.valueOf(categoryId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(ERROR_BOOKS_ID));

            // Verify
            verify(categoryService, times(1)).validateRequestParamCategoryId(categoryId);
            verify(categoryService, times(1)).detectCategory(categoryId);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    /*@Test
    @DisplayName("Resource for detecting a category by the given category id => error - not authorized")
    void detectCategory5() {
        try {
            // Setup
            final Long categoryId = 99999L;
            // Run the test
            mockMvc.perform(get("/api/v1/category/id")
                            .param("categoryId", String.valueOf(categoryId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for detecting a category by the given category id => error - forbidden")
    void detectCategory6() {
        try {
            // Setup
            final Long categoryId = 99999L;
            // Run the test
            mockMvc.perform(get("/api/v1/category/id")
                            .param("categoryId", String.valueOf(categoryId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }*/

    @Test
    @DisplayName("Resource for create or update a category => successful")
    void saveCategory1() {
        try {
            // Setup
            final CategoryDto category = createCategoryDto();
            final CategoryRequest request = createCategoryRequest(category);
            // Mocking the services
            doNothing().when(categoryService).validateRequestParams(request);
            when(categoryService.createOrUpdateCategory(request)).thenReturn(1);

            // Run the test
            mockMvc.perform(post("/api/v1/category")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            // Verify
            verify(categoryService, times(1)).validateRequestParams(request);
            verify(categoryService, times(1)).createOrUpdateCategory(request);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for create or update a category => successful - no content")
    void saveCategory2() {
        try {
            // Setup
            final CategoryDto category = createCategoryDto();
            final CategoryRequest request = createCategoryRequest(category);
            // Mocking the services
            doNothing().when(categoryService).validateRequestParams(request);
            when(categoryService.createOrUpdateCategory(request)).thenThrow(NoContentException.class);

            // Run the test
            mockMvc.perform(post("/api/v1/category")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            // Verify
            verify(categoryService, times(1)).validateRequestParams(request);
            verify(categoryService, times(1)).createOrUpdateCategory(request);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for create or update a category => error - bad request - empty request")
    void saveCategory3() {
        try {
            // Setup
            final CategoryRequest request = null;
            // Mocking the services
            doThrow(new IllegalArgumentException(ERROR_CATEGORIES_REQUEST_EMPTY)).when(categoryService).validateRequestParams(request);

            // Run the test
            mockMvc.perform(post("/api/v1/category")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(ERROR_CATEGORIES_REQUEST_EMPTY));

            // Verify
            verify(categoryService, times(1)).validateRequestParams(request);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for create or update a category => error - bad request - empty description")
    void saveCategory4() {
        try {
            // Setup
            final CategoryDto category = createCategoryDto();
            category.setDescription(null);
            final CategoryRequest request = createCategoryRequest(category);
            // Mocking the services
            doThrow(new IllegalArgumentException(ERROR_CATEGORIES_REQUEST_DESCRIPTION_EMPTY)).when(categoryService).validateRequestParams(request);

            // Run the test
            mockMvc.perform(post("/api/v1/category")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(ERROR_CATEGORIES_REQUEST_DESCRIPTION_EMPTY));

            // Verify
            verify(categoryService, times(1)).validateRequestParams(request);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for create or update a category => internal server error")
    void saveCategory5() {
        try {
            // Setup
            final CategoryDto category = createCategoryDto();
            final CategoryRequest request = createCategoryRequest(category);
            // Mocking the services
            doNothing().when(categoryService).validateRequestParams(request);
            when(categoryService.createOrUpdateCategory(request)).thenThrow(new InternalError(ERROR_CATEGORIES_SAVE));

            // Run the test
            mockMvc.perform(post("/api/v1/category")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(ERROR_CATEGORIES_SAVE));

            // Verify
            verify(categoryService, times(1)).validateRequestParams(request);
            verify(categoryService, times(1)).createOrUpdateCategory(request);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    /*@Test
    @DisplayName("Resource for create or update a category => error - not authorized")
    void saveCategory6() {
        try {
            // Setup
            final CategoryDto category = createCategoryDto();
            final CategoryRequest request = createCategoryRequest(category);

            // Run the test
            mockMvc.perform(post("/api/v1/category")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for create or update a category => error - forbidden")
    void saveCategory7() {
        try {
            // Setup
            final CategoryDto category = createCategoryDto();
            final CategoryRequest request = createCategoryRequest(category);

            // Run the test
            mockMvc.perform(post("/api/v1/category")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }*/

    @Test
    @DisplayName("Resource for removing a category for the given category id => successful")
    void removeCategory1() {
        try {
            // Setup
            final Long categoryId = 52L;
            // Mocking the services
            doNothing().when(categoryService).validateRequestParamCategoryId(categoryId);
            when(categoryService.removeCategory(categoryId)).thenReturn(1);

            // Run the test
            mockMvc.perform(delete("/api/v1/category/id")
                            .param("categoryId", String.valueOf(categoryId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Verify
            verify(categoryService, times(1)).validateRequestParamCategoryId(categoryId);
            verify(categoryService, times(1)).removeCategory(categoryId);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for removing a category for the given category id => successful - no content")
    void removeCategory2() {
        try {
            // Setup
            final Long categoryId = 99999L;
            // Mocking the services
            doNothing().when(categoryService).validateRequestParamCategoryId(categoryId);
            doThrow(NoContentException.class).when(categoryService).removeCategory(categoryId);

            // Run the test
            mockMvc.perform(delete("/api/v1/category/id")
                            .param("categoryId", String.valueOf(categoryId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verify
            verify(categoryService, times(1)).validateRequestParamCategoryId(categoryId);
            verify(categoryService, times(1)).removeCategory(categoryId);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for removing a category for the given category id => error - bad request")
    void removeCategory3() {
        try {
            // Setup
            final Long categoryId = null;
            // Mocking the services
            doThrow(new IllegalArgumentException(ERROR_CATEGORIES_ID_EMPTY)).when(categoryService).validateRequestParamCategoryId(categoryId);

            // Run the test
            mockMvc.perform(delete("/api/v1/category/id")
                            .param("categoryId", (String) null)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(ERROR_CATEGORIES_ID_EMPTY));

            // Verify
            verify(categoryService, times(1)).validateRequestParamCategoryId(categoryId);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for removing a category for the given category id => internal server error")
    void removeCategory4() {
        try {
            // Setup
            final Long categoryId = -99999L;
            // Mocking the services
            doNothing().when(categoryService).validateRequestParamCategoryId(categoryId);
            doThrow(new InternalError(ERROR_CATEGORIES_ID)).when(categoryService).removeCategory(categoryId);

            // Run the test
            mockMvc.perform(delete("/api/v1/category/id")
                            .param("categoryId", String.valueOf(categoryId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(ERROR_CATEGORIES_ID));

            // Verify
            verify(categoryService, times(1)).validateRequestParamCategoryId(categoryId);
            verify(categoryService, times(1)).removeCategory(categoryId);
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    /*@Test
    @DisplayName("Resource for removing a category for the given category id => error - not authorized")
    void removeCategory5() {
        try {
            // Setup
            final Long categoryId = 99999L;
            // Run the test
            mockMvc.perform(delete("/api/v1/category/id")
                            .param("categoryId", String.valueOf(categoryId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }

    @Test
    @DisplayName("Resource for removing a category for the given category id => error - forbidden")
    void removeCategory6() {
        try {
            // Setup
            final Long categoryId = 52L;
            // Run the test
            mockMvc.perform(delete("/api/v1/category/id")
                            .param("categoryId", String.valueOf(categoryId))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            fail("Test failed because of => \nStacktrace: ", e);
        }
    }*/

    private CategoryDto createCategoryDto() {
        CategoryDto category = new CategoryDto();
        category.setCategoryId(1L);
        category.setDescription("Horror");
        category.setBooksCount(1L);

        return category;
    }

    private CategoryRequest createCategoryRequest(CategoryDto categoryDto) {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setCategoryId(categoryDto.getCategoryId());
        categoryRequest.setDescription(categoryDto.getDescription());

        return categoryRequest;
    }
}