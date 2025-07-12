package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.SupplierProductDTO;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class ProductFormController {
   @FXML
   private Label titleLabel;
   @FXML
   private TextField catalogField, nameField, priceField, weightField, manufacturerField;
   @FXML
   private Spinner<Integer> expiresSpinner;
   @FXML
   private Button saveBtn, cancelBtn;

   private boolean creating;
   private SupplierProductDTO existing;
   private int supplierId;
   private SupplierService service;

   public void init(boolean creating,
         SupplierProductDTO existing,
         int supplierId,
         SupplierService service) {
      this.creating = creating;
      this.existing = existing;
      this.supplierId = supplierId;
      this.service = service;

      // spinner 0–365 days
      expiresSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 365, 0));

      if (!creating && existing != null) {
         titleLabel.setText("Edit Product");
         catalogField.setText(existing.getSupplierCatalogNumber());
         nameField.setText(existing.getName());
         priceField.setText(existing.getPrice().toString());
         weightField.setText(existing.getWeight().toString());
         expiresSpinner.getValueFactory().setValue(existing.getExpiresInDays());
         manufacturerField.setText(existing.getManufacturerName());
      } else {
         titleLabel.setText("Add Product");
      }

      cancelBtn.setOnAction(e -> ((Stage) cancelBtn.getScene().getWindow()).close());
      saveBtn.setOnAction(e -> onSave());
   }

   private void onSave() {
      // validation
      if (catalogField.getText().isBlank()
            || nameField.getText().isBlank()
            || priceField.getText().isBlank()
            || weightField.getText().isBlank()
            || manufacturerField.getText().isBlank()) {
         new Alert(Alert.AlertType.ERROR, "All fields are required").showAndWait();
         return;
      }

      BigDecimal price, weight;
      try {
         price = new BigDecimal(priceField.getText());
         weight = new BigDecimal(weightField.getText());
      } catch (NumberFormatException ex) {
         new Alert(Alert.AlertType.ERROR, "Price and Weight must be numbers").showAndWait();
         return;
      }

      SupplierProductDTO dto = new SupplierProductDTO(
            supplierId,
            creating ? -1 : existing.getProductId(),
            catalogField.getText().trim(),
            nameField.getText().trim(),
            price,
            weight,
            expiresSpinner.getValue(),
            manufacturerField.getText().trim());

      ServiceResponse<?> resp;
      if (creating) {
         resp = service.addProductToSupplier(dto, supplierId);
      } else {
         resp = service.updateProduct(dto, supplierId);
      }

      if (resp.isSuccess()) {
         ((Stage) saveBtn.getScene().getWindow()).close();
      } else {
         new Alert(Alert.AlertType.ERROR, String.join("\n", resp.getErrors()))
               .showAndWait();
      }
   }
}