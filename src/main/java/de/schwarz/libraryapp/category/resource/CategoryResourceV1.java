package de.schwarz.libraryapp.category.resource;


import de.schwarz.libraryapp.category.domain.dto.CategoryDto;
import de.schwarz.libraryapp.category.domain.dto.CategoryRequest;
import de.schwarz.libraryapp.category.service.CategoryService;
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
public class CategoryResourceV1 {

    private final CategoryService categoryService;


    @Operation(tags = "Get all categories", summary = "Getting all categories from library", description = "Process gets all categories from online library database without restrictions.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CategoryDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @GetMapping("/v1/categories")
    public ResponseEntity<?> detectAllCategories() {
        // Call service
        List<CategoryDto> categories = categoryService.detectAllCategories();
        log.info("Count of categories detected: {}...", categories.size());
        // Prepare and return response
        return ResponseEntity
                .ok()
                .body(categories);
    }

    @Operation(tags = "Get all categories description", summary = "Getting all categories by the given description from library", description = "Process gets all categories from online library database, by the given description.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CategoryDto.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = HttpClientErrorException.BadRequest.class)), description = "Bad Request<br/><br/>* Category description is empty."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @GetMapping("/v1/categories/description")
    public ResponseEntity<?> detectCategoriesFromDescription(@RequestParam(value = "description", required = false) String description) {
        // Validate request param
        categoryService.validateRequestParamDescription(description);
        // Call service
        List<CategoryDto> categories = categoryService.detectCategoriesByDescription(description);
        log.info("Count of categories detected: {} by description: {}...", categories.size(), description);
        // Prepare and return response
        return ResponseEntity
                .ok()
                .body(categories);
    }

    @Operation(tags = "Get category description", summary = "Getting a specific category by the given description from library", description = "Process gets a specific category from online library database, by the given description.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CategoryDto.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = HttpClientErrorException.BadRequest.class)), description = "Bad Request<br/><br/>* Category description is empty."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @GetMapping("/v1/category/description")
    public ResponseEntity<?> detectCategoryFromDescription(@RequestParam(value = "description", required = false) String description) {
        // Validate request param
        categoryService.validateRequestParamDescription(description);
        // Call service
        CategoryDto category = categoryService.detectCategoryByDescription(description);
        log.info("Category detected by description: {}...", description);
        // Prepare and return response
        return ResponseEntity
                .ok()
                .body(category);
    }

    @Operation(tags = "Detect category", summary = "Detects a category from the online library", description = "Detects a category from the library database, by the given category id.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CategoryRequest.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = HttpClientErrorException.BadRequest.class)), description = "Bad Request<br/><br/>* Category id is empty."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @GetMapping("/v1/category/id")
    public ResponseEntity<?> detectCategory(@RequestParam(value = "categoryId", required = false) Long categoryId) {
        // Validate request param
        categoryService.validateRequestParamCategoryId(categoryId);
        // Call service
        CategoryDto category = categoryService.detectCategory(categoryId);
        log.info("Category with id: {}, detected...", category.getCategoryId());
        // Prepare and return response
        return ResponseEntity
                .ok()
                .body(category);
    }

    @Operation(tags = "Create update category", summary = "Creates or updates a category in the online library", description = "Process creates or updates a category in library database, by the given request.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CategoryRequest.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = HttpClientErrorException.BadRequest.class)), description = "Bad Request<br/><br/>* Category description is empty."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @PostMapping("/v1/category")
    public ResponseEntity<?> saveCategory(@RequestBody(required = false) CategoryRequest request) {
        // Validate request param
        categoryService.validateRequestParams(request);
        // Call service
        var categoryNewOrUpdated = categoryService.createOrUpdateCategory(request);
        log.info("Category created or updated: {}...", categoryNewOrUpdated > 0);
        // Prepare and return response
        return ResponseEntity
                .ok()
                .build();
    }

    @Operation(tags = "Remove category", summary = "Removes a category from the online library", description = "Process removes a category from the library database, by the given category id.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CategoryRequest.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = HttpClientErrorException.BadRequest.class)), description = "Bad Request<br/><br/>* Category id is empty."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal error")})
    @SecurityRequirement(name = "http_secure")
    @DeleteMapping("/v1/category/id")
    public ResponseEntity<?> removeCategory(@RequestParam(value = "categoryId", required = false) Long categoryId) {
        // Validate request param
        categoryService.validateRequestParamCategoryId(categoryId);
        // Call service
        var categoryRemoved = categoryService.removeCategory(categoryId);
        log.info("Category removed: {}...", categoryRemoved > 0);
        // Prepare and return response
        return ResponseEntity
                .ok()
                .build();
    }
}
