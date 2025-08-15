package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.SupplierProductDTO;
import DomainLayer.SystemFactory;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.math.BigDecimal;

public class SupplierProductEditController {

   @FXML
   private TextField catalogField;
   @FXML
   private TextField nameField;
   @FXML
   private TextField priceField;
   @FXML
   private TextField weightField;
   @FXML
   private TextField expiresField;
   @FXML
   private TextField manufacturerField;

   private SystemFactory systemFactory = new SystemFactory();
   private SupplierService supplierService = systemFactory.getSupplierModule().getSupplierService();
   private int supplierId;
   private int productId;

   /** Call init() with existing DTO before showing */
   public void init(SupplierProductDTO existing) {
      this.supplierId = existing.getSupplierId();
      this.productId = existing.getProductId();
      catalogField.setText(existing.getSupplierCatalogNumber());
      nameField.setText(existing.getName());
      priceField.setText(existing.getPrice().toString());
      weightField.setText(existing.getWeight().toString());
      expiresField.setText(String.valueOf(existing.getExpiresInDays()));
      manufacturerField.setText(existing.getManufacturerName());
   }

   @FXML
   private void onSave() {
      try {
         var dto = new SupplierProductDTO(
               supplierId,
               productId,
               catalogField.getText().trim(),
               nameField.getText().trim(),
               new BigDecimal(priceField.getText().trim()),
               new BigDecimal(weightField.getText().trim()),
               Integer.parseInt(expiresField.getText().trim()),
               manufacturerField.getText().trim());

         var resp = supplierService.updateProduct(dto, supplierId);
         if (resp.isSuccess()) {
            closeDialog();
         } else {
            showError(String.join("\n", resp.getErrors()));
         }
      } catch (Exception e) {
         showError("Invalid input: " + e.getMessage());
      }
   }

   @FXML
   private void onCancel() {
      closeDialog();
   }

   private void closeDialog() {
      Stage stage = (Stage) catalogField.getScene().getWindow();
      stage.close();
   }

   private void showError(String msg) {
      new Alert(Alert.AlertType.ERROR, msg).showAndWait();
   }
}