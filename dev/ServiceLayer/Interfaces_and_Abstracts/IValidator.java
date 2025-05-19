// service/validation/Validator.java
package ServiceLayer.Interfaces_and_Abstracts;

public interface IValidator<T> {
    public ServiceResponse<?> validate(T target);
}