package PresentationLayer;

import java.util.List;

public interface View {
   void showMessage(String msg);

   void showError(String msg);

   <T> void dispatchResponse(String rawJson, Class<T> valueType);

   void showOptions(String title, List<String> options);

   String readLine();

   String readLine(String message);
}