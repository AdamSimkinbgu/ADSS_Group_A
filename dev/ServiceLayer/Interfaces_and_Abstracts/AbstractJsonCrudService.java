// File: service/AbstractJsonCrudService.java
package ServiceLayer.Interfaces_and_Abstracts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ServiceLayer.Response;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Base class providing JSON persistence via Jackson.
 * Implements CrudService<T,ID> by loading/saving a List<T> from disk.
 *
 * @param <T>  Entity type
 * @param <ID> Identifier type
 */
public abstract class AbstractJsonCrudService<T, ID> implements ICrudService<T, UUID> {
   private final File file;
   private final TypeReference<List<T>> typeRef;
   private final ObjectMapper mapper = new ObjectMapper();
   protected List<T> cache = new ArrayList<>();

   protected AbstractJsonCrudService(String filePath, TypeReference<List<T>> typeRef) {
      this.file = new File(filePath);
      this.typeRef = typeRef;
      load();
   }

   /** Load JSON → cache */
   protected void load() {
      if (!file.exists())
         return;
      try {
         cache = mapper.readValue(file, typeRef);
      } catch (IOException e) {
         throw new RuntimeException("Failed to load data from " + file.getName(), e);
      }
   }

   /** Save cache → JSON */
   protected void save() {
      try {
         mapper.writerWithDefaultPrettyPrinter().writeValue(file, cache);
      } catch (IOException e) {
         throw new RuntimeException("Failed to save data to " + file.getName(), e);
      }
   }

   @Override
   public Response<List<T>> getAll() {
      return Response.ok(cache);
   }

   @Override
   public Response<T> findById(UUID id) {
      return cache.stream()
            .filter(e -> Objects.equals(getId(e), id))
            .findFirst()
            .map(e -> Response.ok(e))
            .orElse(Response.error("Entity with ID: " + id + " not found."));
   }

   @Override
   public Response<UUID> create(T entity) {
      if (getId(entity) == null) {
         setId(entity, generateId());
         cache.add(entity);
         save();
         return Response.ok(getId(entity));
      }
      return Response.error("Failed to create entity: ID already exists.");
   }

   @Override
   public Response<String> update(T updated) {
      UUID id = getId(updated);
      for (int i = 0; i < cache.size(); i++) {
         if (Objects.equals(getId(cache.get(i)), id)) {
            cache.set(i, updated);
            save();
            return Response.ok("Entity with ID: " + id + " updated successfully.");
         }
      }
      return Response.error("Failed to update entity with ID: " + id);
   }

   @Override
   public Response<String> delete(UUID id) {
      boolean removed = cache.removeIf(e -> Objects.equals(getId(e), id));
      if (removed) {
         save();
         return Response.ok("Entity with ID: " + id + " deleted successfully.");
      }
      return Response.error("Failed to delete entity with ID: " + id);
   }

   // Subclasses must implement these three:
   protected abstract UUID getId(T entity);

   protected abstract void setId(T entity, UUID id);

   protected abstract UUID generateId();
}