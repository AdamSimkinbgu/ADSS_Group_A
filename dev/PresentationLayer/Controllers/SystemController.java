package PresentationLayer.Controllers;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import PresentationLayer.AbstractController;
import PresentationLayer.View;
import ServiceLayer.SystemService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

/**
 * Controller for System-level commands.
 */
public class SystemController extends AbstractController {
   private String DATA_PATH;

   public SystemController(String data, SystemService systemService, View view) {
      super(view, systemService);
      this.implemented = true;
      File f = new File(data);
      if (!f.exists()) {
         view.showError("Can't find data.json in " + f.getAbsolutePath());
      } else {
         DATA_PATH = f.getAbsolutePath();
         view.showMessage("Data path: " + DATA_PATH);
      }
   }

   @Override
   public List<String> showMenu() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'showMenu'");
   }
}
