package ServiceLayer.Interfaces_and_Abstracts;

import java.util.List;

;

/**
 * Generic CRUD contract, where each call yields a JSON‐friendly Response.
 *
 * @param <T>  the domain type (e.g. Supplier)
 * @param <ID> the identifier type (e.g. UUID)
 */
public interface ICrudService<T, UUID> {
    /** List every T in the system. */
    ServiceResponse<List<T>> getAll();

    /** Look up a single T by its ID. */
    ServiceResponse<T> findById(UUID id);

    /** Create a new T; returns the new ID on success. */
    ServiceResponse<UUID> create(T entity);

    /** Update an existing T; no data returns if successful. */
    ServiceResponse<String> update(T entity);

    /** Delete by ID; no data returns if successful. */
    ServiceResponse<String> delete(UUID id);
}