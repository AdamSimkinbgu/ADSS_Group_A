package PresentationLayer;

import java.util.List;

public interface View {
   String readLine(String prompt);

   default String readLine() {
      return readLine("");
   }

   void showMessage(String msg);

   void showError(String err);
}