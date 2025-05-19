package ServiceLayer;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public abstract class BaseService {
   protected ObjectMapper objectMapper;

   public BaseService() {
      this.objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
   }

   public ServiceResponse<String> commandDoesNotExist(String data) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

}
