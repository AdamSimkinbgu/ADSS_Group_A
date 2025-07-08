package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.AddressDTO;
import DTOs.SuppliersModuleDTOs.ContactInfoDTO;
import DTOs.SuppliersModuleDTOs.PaymentDetailsDTO;
import DTOs.SuppliersModuleDTOs.SupplierDTO;
import DTOs.SuppliersModuleDTOs.Enums.PaymentMethod;
import DTOs.SuppliersModuleDTOs.Enums.PaymentTerm;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SupplierAddController {
   @FXML
   private TextField nameField, taxField;
   @FXML
   private TextField streetField, cityField, buildingField;
   @FXML
   private CheckBox selfSupplyCheck;
   @FXML
   private FlowPane daysPane;
   @FXML
   private Spinner<Integer> leadDaysSpinner;
   @FXML
   private TextField bankField;
   @FXML
   private ComboBox<String> methodCombo, termCombo;
   @FXML
   private ListView<String> contactsList;
   @FXML
   private TextField newContactField;
   @FXML
   private Button addContactBtn, saveBtn, cancelBtn;

   private SupplierService supplierService;
   private final ObservableList<String> contacts = FXCollections.observableArrayList();
   private final EnumSet<DayOfWeek> selectedDays = EnumSet.noneOf(DayOfWeek.class);

   public void setService(SupplierService s) {
      this.supplierService = s;
   }

   @FXML
   public void initialize() {
      // Configure spinner
      leadDaysSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 365, 0));
      methodCombo.getItems().setAll("CASH", "CREDIT_CARD", "BANK_TRANSFER", "CASH_ON_DELIVERY");
      termCombo.getItems().setAll("N30", "N60", "N90", "PREPAID", "POSTPAID");
      // Populate contacts list
      contactsList.setItems(contacts);
      addContactBtn.setOnAction(e -> {
         String txt = newContactField.getText();
         if (txt != null && !txt.isBlank()) {
            contacts.add(txt.trim());
            newContactField.clear();
         }
      });

      // Supply days checkboxes
      for (DayOfWeek day : DayOfWeek.values()) {
         CheckBox cb = new CheckBox(day.name());
         cb.selectedProperty().addListener((obs, old, nw) -> {
            if (nw)
               selectedDays.add(day);
            else
               selectedDays.remove(day);
         });
         daysPane.getChildren().add(cb);
      }
      daysPane.setVisible(false);
      selfSupplyCheck.selectedProperty().addListener((obs, old, nw) -> daysPane.setVisible(nw));

      cancelBtn.setOnAction(e -> close());
      saveBtn.setOnAction(e -> onSave());
   }

   private void onSave() {
      if (nameField.getText().isBlank()) {
         alert("Name required");
         return;
      }
      if (taxField.getText().isBlank()) {
         alert("Tax number required");
         return;
      }
      if (bankField.getText().length() != 6) {
         alert("Bank account must be 6 digits");
         return;
      }
      if (methodCombo.getValue() == null) {
         alert("Select payment method");
         return;
      }
      if (termCombo.getValue() == null) {
         alert("Select payment term");
         return;
      }

      List<ContactInfoDTO> contactDTOs = new ArrayList<>();
      for (String entry : contacts) {
         // split into at most 3 parts
         String[] parts = entry.split(",", 3);
         if (parts.length != 3 ||
               parts[0].isBlank() ||
               parts[1].isBlank() ||
               parts[2].isBlank()) {
            alert("Invalid contact entry: \"" + entry +
                  "\"\nFormat must be Name,Email,Phone");
            return;
         }
         contactDTOs.add(new ContactInfoDTO(
               parts[0].trim(),
               parts[1].trim(),
               parts[2].trim()));
      }

      AddressDTO addr = new AddressDTO(
            streetField.getText(), cityField.getText(), buildingField.getText());
      PaymentDetailsDTO pay = new PaymentDetailsDTO(
            bankField.getText(),
            PaymentMethod.valueOf(methodCombo.getValue()),
            PaymentTerm.valueOf(termCombo.getValue()));
      SupplierDTO dto = new SupplierDTO(
            nameField.getText().trim(),
            taxField.getText().trim(),
            addr,
            selfSupplyCheck.isSelected(),
            EnumSet.copyOf(selectedDays),
            leadDaysSpinner.getValue(),
            pay,
            contactDTOs);
      ServiceResponse<?> resp = supplierService.createSupplier(dto);
      if (resp.isSuccess()) {
         close();
      } else {
         alert(resp.getErrors().toString());
      }
   }

   private void close() {
      ((Stage) cancelBtn.getScene().getWindow()).close();
   }

   private void alert(String msg) {
      Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
      a.showAndWait();
   }
}