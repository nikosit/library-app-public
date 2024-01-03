package de.schwarz.libraryapp.category.domain;


import de.schwarz.libraryapp.category.domain.dto.CategoryDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CategoryRepository {

    @PersistenceContext
    private EntityManager em;


    /**
     * @return
     */
    public List<CategoryDto> findAll() {
        String sql = "SELECT c.id, c.description, count(b.id) count_books FROM library.category c "
                + "LEFT JOIN library.book b ON(c.id=b.category_id) "
                + "GROUP BY c.id, c.description "
                + "ORDER BY c.id";

        @SuppressWarnings("unchecked")
        List<Object[]> result = em.createNativeQuery(sql)
                .getResultList();

        return result.stream()
                .map(obj -> new CategoryDto(((Number) obj[0]).longValue(), (String) obj[1], ((Number) obj[2]).longValue()))
                .toList();
    }

    /**
     * @param description
     * @return
     */
    public List<CategoryDto> findByDescription(String description) {
        String sql = "SELECT c.id, c.description, count(b.id) count_books "
                + "FROM library.category c LEFT JOIN library.book b ON(c.id=b.category_id) "
                + "WHERE UPPER(c.description) LIKE '%' || :description || '%'"
                + "GROUP BY c.id, c.description "
                + "ORDER BY c.id";

        @SuppressWarnings("unchecked")
        List<Object[]> result = em.createNativeQuery(sql)
                .setParameter("description", new TypedParameterValue<>(StandardBasicTypes.STRING, description.toUpperCase()))
                .getResultList();

        return result.stream()
                .map(obj -> new CategoryDto(((Number) obj[0]).longValue(), (String) obj[1], ((Number) obj[2]).longValue()))
                .toList();
    }

    /**
     * @param description
     * @return
     */
    public Optional<CategoryDto> findByDescriptionStrict(String description) {
        try {
            String sql = "SELECT c.id, c.description, count(b.id) count_books "
                    + "FROM library.category c LEFT JOIN library.book b ON(c.id=b.category_id) "
                    + "WHERE UPPER(c.description) = :description "
                    + "GROUP BY c.id, c.description "
                    + "ORDER BY c.id";

            Object[] result = (Object[]) em.createNativeQuery(sql)
                    .setParameter("description", new TypedParameterValue<>(StandardBasicTypes.STRING, description.toUpperCase()))
                    .getSingleResult();

            return Optional.of(new CategoryDto(((Number) result[0]).longValue(), (String) result[1], ((Number) result[2]).longValue()));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * @param categoryId
     * @return
     */
    public Optional<CategoryDto> findById(Long categoryId) {
        try {
            String sql = "SELECT c.id, c.description, count(b.id) count_books "
                    + "FROM library.category c LEFT JOIN library.book b ON(c.id=b.category_id) "
                    + "WHERE c.id = :categoryId "
                    + "GROUP BY c.id, c.description "
                    + "ORDER BY c.id";

            Tuple result = (Tuple) em.createNativeQuery(sql, Tuple.class)
                    .setParameter("categoryId", new TypedParameterValue<>(StandardBasicTypes.LONG, categoryId))
                    .getSingleResult();

            return Optional.of(new CategoryDto(result.get("id", Long.class), result.get("description", String.class), result.get("count_books", Long.class)));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * @param category
     * @return
     */
    public int save(CategoryDto category) {
        if (ObjectUtils.isEmpty(category.getCategoryId())) {
            String sql = "INSERT INTO library.category(description) "
                    + "VALUES(:description)";

            return em.createNativeQuery(sql)
                    .setParameter("description", new TypedParameterValue<>(StandardBasicTypes.STRING, category.getDescription()))
                    .executeUpdate();
        } else {
            String sql = "UPDATE library.category c "
                    + "SET c.description = :description "
                    + "SET c.updated_on = now() "
                    + "WHERE c.id = :categoryId";

            return em.createNativeQuery(sql)
                    .setParameter("categoryId", new TypedParameterValue<>(StandardBasicTypes.LONG, category.getCategoryId()))
                    .setParameter("description", new TypedParameterValue<>(StandardBasicTypes.STRING, category.getDescription()))
                    .executeUpdate();
        }
    }

    /**
     * @param categoryId
     */
    public int deleteById(Long categoryId) {
        String sql = "DELETE FROM library.category c "
                + "WHERE c.id = :categoryId";

        return em.createNativeQuery(sql)
                .setParameter("categoryId", new TypedParameterValue<>(StandardBasicTypes.LONG, categoryId))
                .executeUpdate();
    }
}