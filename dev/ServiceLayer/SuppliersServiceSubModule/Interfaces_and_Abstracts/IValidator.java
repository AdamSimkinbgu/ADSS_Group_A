// service/validation/Validator.java
package ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts;

public interface IValidator<T> {
    public ServiceResponse<?> validateCreateDTO(T target);

    public ServiceResponse<?> validateUpdateDTO(T target);

    public ServiceResponse<?> validateRemoveDTO(int id);

    public ServiceResponse<?> validateGetDTO(int id);
}