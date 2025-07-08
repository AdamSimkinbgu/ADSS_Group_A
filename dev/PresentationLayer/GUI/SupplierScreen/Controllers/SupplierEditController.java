package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.AddressDTO;
import DTOs.SuppliersModuleDTOs.ContactInfoDTO;
import DTOs.SuppliersModuleDTOs.PaymentDetailsDTO;
import DTOs.SuppliersModuleDTOs.SupplierDTO;
import DTOs.SuppliersModuleDTOs.Enums.PaymentMethod;
import DTOs.SuppliersModuleDTOs.Enums.PaymentTerm;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.AgreementService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.util.EnumSet;

public class SupplierEditController {
   // --- FXML-injected fields ---
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

   // --- injected services & DTO ---
   private SupplierService supplierService;
   private AgreementService agreementService;
   private SupplierDTO supplier;

   // --- local state ---
   private final ObservableList<String> contacts = FXCollections.observableArrayList();
   private final EnumSet<DayOfWeek> selectedDays = EnumSet.noneOf(DayOfWeek.class);

   /** Called by parent before showing the dialog */
   public void setServices(SupplierService s, AgreementService a) {
      this.supplierService = s;
      this.agreementService = a;
   }

   /** Called by parent before showing the dialog */
   public void setSupplier(SupplierDTO dto) {
      this.supplier = dto;
   }

   /** JavaFX lifecycle: wire up controls, but do _not_ populate fields here */
   @FXML
   public void initialize() {
      // spinner
      leadDaysSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 365, 0));

      // payment combos
      methodCombo.getItems().setAll("CASH", "CREDIT_CARD", "BANK_TRANSFER", "CASH_ON_DELIVERY");
      termCombo.getItems().setAll("N30", "N60", "N90", "PREPAID", "POSTPAID");

      // contacts list
      contactsList.setItems(contacts);
      addContactBtn.setOnAction(e -> {
         String txt = newContactField.getText();
         if (txt != null && !txt.isBlank()) {
            contacts.add(txt.trim());
            newContactField.clear();
         }
      });

      // day-of-week checkboxes
      for (DayOfWeek day : DayOfWeek.values()) {
         CheckBox cb = new CheckBox(day.name());
         cb.selectedProperty().addListener((obs, oldV, newV) -> {
            if (newV)
               selectedDays.add(day);
            else
               selectedDays.remove(day);
         });
         daysPane.getChildren().add(cb);
      }
      daysPane.setVisible(false);
      selfSupplyCheck.selectedProperty().addListener((obs, oldV, newV) -> daysPane.setVisible(newV));

      // cancel & save
      cancelBtn.setOnAction(e -> closeWindow());
      saveBtn.setOnAction(e -> onSave());
   }

   /** Must be called _after_ setSupplier(...) */
   public void populateForm() {
      if (supplier == null)
         return;

      // basic fields
      nameField.setText(supplier.getName());
      taxField.setText(supplier.getTaxNumber());

      // address
      AddressDTO adr = supplier.getAddressDTO();
      streetField.setText(adr.getStreet());
      cityField.setText(adr.getCity());
      buildingField.setText(adr.getBuildingNumber());

      // self supply & days
      selfSupplyCheck.setSelected(supplier.getSelfSupply());
      daysPane.setVisible(supplier.getSelfSupply());
      supplier.getSupplyDays().forEach(day -> daysPane.getChildren().stream()
            .filter(n -> n instanceof CheckBox && ((CheckBox) n).getText().equals(day.name()))
            .map(n -> (CheckBox) n)
            .forEach(cb -> {
               cb.setSelected(true);
               selectedDays.add(day);
            }));

      // lead days
      leadDaysSpinner.getValueFactory().setValue(supplier.getLeadSupplyDays());

      // payment
      PaymentDetailsDTO pay = supplier.getPaymentDetailsDTO();
      bankField.setText(pay.getBankAccountNumber());
      methodCombo.setValue(pay.getPaymentMethod().name());
      termCombo.setValue(pay.getPaymentTerm().name());

      // contacts
      supplier.getContactsInfoDTOList()
            .forEach(ci -> contacts.add(ci.getName() + "," + ci.getEmail() + "," + ci.getPhone()));
   }

   private void onSave() {
      // validate…
      if (nameField.getText().isBlank()) {
         showAlert("Name required");
         return;
      }
      if (taxField.getText().isBlank()) {
         showAlert("Tax number required");
         return;
      }
      if (bankField.getText().length() != 6) {
         showAlert("Bank account must be 6 digits");
         return;
      }
      if (methodCombo.getValue() == null) {
         showAlert("Select payment method");
         return;
      }
      if (termCombo.getValue() == null) {
         showAlert("Select payment term");
         return;
      }

      // build DTO
      AddressDTO adr = new AddressDTO(
            streetField.getText(),
            cityField.getText(),
            buildingField.getText());

      PaymentDetailsDTO pay = new PaymentDetailsDTO(
            bankField.getText(),
            PaymentMethod.valueOf(methodCombo.getValue()),
            PaymentTerm.valueOf(termCombo.getValue()));

      SupplierDTO updated = new SupplierDTO(
            supplier.getId(), // preserve ID
            nameField.getText().trim(),
            taxField.getText().trim(),
            adr,
            selfSupplyCheck.isSelected(),
            EnumSet.copyOf(selectedDays),
            leadDaysSpinner.getValue(),
            pay,
            contacts.stream()
                  .map(s -> {
                     String[] parts = s.split(",", 3);
                     return new ContactInfoDTO(parts[0], parts[1], parts[2]);
                  })
                  .toList());

      ServiceResponse<?> resp = supplierService.updateSupplier(updated, supplier.getId());
      if (resp.isSuccess()) {
         closeWindow();
      } else {
         showAlert(resp.getErrors().toString());
      }
   }

   private void closeWindow() {
      ((Stage) cancelBtn.getScene().getWindow()).close();
   }

   private void showAlert(String msg) {
      new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
   }
}