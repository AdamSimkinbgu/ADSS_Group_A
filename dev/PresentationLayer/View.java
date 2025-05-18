package PresentationLayer;

import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public interface View {
   String readLine(String prompt);

   default String readLine() {
      return readLine("");
   }

   void showMessage(String msg);

   void showError(String err);

   void dispatchResponse(ServiceResponse<?> res);
}