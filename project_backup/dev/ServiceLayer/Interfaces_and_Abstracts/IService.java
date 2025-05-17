package ServiceLayer.Interfaces_and_Abstracts;

public interface IService {
   ServiceResponse<?> execute(String serviceOption, String JsonDTO);
}