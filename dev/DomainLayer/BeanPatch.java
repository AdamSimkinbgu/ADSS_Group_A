package DomainLayer;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Null-safe “copy only if changed”.
 *
 * @param <S> source (usually the DTO)
 * @param <T> target (domain/entity)
 * @param <V> value type of the property
 */
public final class BeanPatch<S, T, V> {

   private final Function<S, V> getterFromDto;
   private final Function<T, V> getterFromEntity;
   private final BiConsumer<T, V> setterOnEntity;

   private BeanPatch(Function<S, V> dtoGetter,
         Function<T, V> entityGetter,
         BiConsumer<T, V> entitySetter) {
      this.getterFromDto = dtoGetter;
      this.getterFromEntity = entityGetter;
      this.setterOnEntity = entitySetter;
   }

   /** Factory method for nicer type inference */
   public static <S, T, V> BeanPatch<S, T, V> of(
         Function<S, V> dtoGetter,
         Function<T, V> entityGetter,
         BiConsumer<T, V> entitySetter) {
      return new BeanPatch<>(dtoGetter, entityGetter, entitySetter);
   }

   /** Apply patch for THIS single property */
   public void apply(S source, T target) {
      V incoming = getterFromDto.apply(source);
      if (incoming != null && !Objects.equals(incoming, getterFromEntity.apply(target))) {
         setterOnEntity.accept(target, incoming);
      }
   }
}