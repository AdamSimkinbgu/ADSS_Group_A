package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.AgreementDTO;
import DTOs.SuppliersModuleDTOs.BillofQuantitiesItemDTO;
import DTOs.SuppliersModuleDTOs.CatalogProductDTO;
import DTOs.SuppliersModuleDTOs.SupplierDTO;
import DomainLayer.SystemFactory;
import ServiceLayer.SuppliersServiceSubModule.AgreementService;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.StringConverter;
import javafx.util.converter.BigDecimalStringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AgreementAddController {
   @FXML
   private ComboBox<SupplierDTO> supplierCombo;
   @FXML
   private DatePicker startDatePicker, endDatePicker;

   @FXML
   private TextField prodSearchField;
   @FXML
   private ListView<CatalogProductDTO> availableList;
   @FXML
   private TextField quantityField, discountField;
   @FXML
   private TableView<BillofQuantitiesItemDTO> selectedTable;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, Integer> colLineId;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, String> colProdName;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, Integer> colQuantity;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, BigDecimal> colDiscount;

   private SystemFactory systemFactory = new SystemFactory();
   private AgreementService agreementService = systemFactory.getSupplierModule().getAgreementService();
   private SupplierService supplierService = systemFactory.getSupplierModule().getSupplierService();
   private final ObservableList<CatalogProductDTO> available = FXCollections.observableArrayList();
   private final ObservableList<BillofQuantitiesItemDTO> picked = FXCollections.observableArrayList();
   private int nextLine = 1;

   @FXML
   void initialize() {
      // wire up columns
      selectedTable.setItems(picked);
      selectedTable.setEditable(true);
      colLineId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getLineInBillID()).asObject());
      colProdName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProductName()));
      colQuantity.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());
      colQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
      colQuantity.setOnEditCommit(e -> {
         int v = Math.max(1, e.getNewValue());
         e.getRowValue().setQuantity(v);
      });
      colDiscount.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDiscountPercent()));
      colDiscount.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
      colDiscount.setOnEditCommit(e -> {
         BigDecimal d = e.getNewValue().max(BigDecimal.ZERO).min(BigDecimal.valueOf(100));
         e.getRowValue().setDiscountPercent(d);
      });

      // product search
      prodSearchField.textProperty().addListener((obs, old, nu) -> {
         var all = supplierService.getSupplierProducts(
               supplierCombo.getValue().getId()).getValue();
         available.setAll(all.stream()
               .map(CatalogProductDTO::new)
               .filter(p -> p.getProductName().toLowerCase().contains(nu.toLowerCase()))
               .toList());
      });

      availableList.setItems(available);

      supplierCombo.getItems().setAll(supplierService.getAllSuppliers().getValue());
      // 1) How each item in the popup list is rendered:
      supplierCombo.setCellFactory(cb -> new ListCell<>() {
         @Override
         protected void updateItem(SupplierDTO s, boolean empty) {
            super.updateItem(s, empty);
            if (empty || s == null) {
               setText(null);
            } else {
               setText(s.getId() + " \u2013 " + s.getName());
            }
         }
      });

      // 2) How the selected item (the “button cell”) is rendered
      supplierCombo.setButtonCell(new ListCell<>() {
         @Override
         protected void updateItem(SupplierDTO s, boolean empty) {
            super.updateItem(s, empty);
            if (empty || s == null) {
               setText(null);
            } else {
               setText(s.getId() + " \u2013 " + s.getName());
            }
         }
      });

      // 3) A converter so that ComboBox#getEditor() and toString() use the same
      // format
      supplierCombo.setConverter(new StringConverter<>() {
         @Override
         public String toString(SupplierDTO s) {
            if (s == null)
               return "";
            return s.getId() + " \u2013 " + s.getName();
         }

         @Override
         public SupplierDTO fromString(String string) {
            // We won’t support free‐text entry, so just return null
            return null;
         }
      });
      supplierCombo.getSelectionModel().selectedItemProperty().addListener((o, old, nu) -> {
         if (nu != null)
            loadProducts(nu.getId());
      });
      startDatePicker.setValue(LocalDate.now());
      endDatePicker.setValue(LocalDate.now().plusMonths(1));
   }

   private void loadProducts(int supplierId) {
      available.setAll(
            supplierService.getSupplierProducts(supplierId).getValue()
                  .stream().map(CatalogProductDTO::new).toList());
   }

   @FXML
   private void onAddItem() {
      CatalogProductDTO sel = availableList.getSelectionModel().getSelectedItem();
      if (sel == null)
         return;
      int qty = 1;
      try {
         qty = Math.max(1, Integer.parseInt(quantityField.getText()));
      } catch (NumberFormatException e) {
         /* ignored */}
      BigDecimal disc = BigDecimal.ZERO;
      try {
         BigDecimal discTemp = new BigDecimal(discountField.getText());
         if (discTemp.compareTo(BigDecimal.ZERO) < 0 || discTemp.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new NumberFormatException("Discount must be between 0 and 100");
         }
         disc = discTemp;

      } catch (NumberFormatException e) {
         Alert alert = new Alert(Alert.AlertType.INFORMATION, "Invalid discount value. Using 0%.");
         alert.showAndWait();
         disc = BigDecimal.ZERO;
      }
      var dto = new BillofQuantitiesItemDTO(
            -1, nextLine++, sel.getProductName(), sel.getProductId(), qty, disc.divide(BigDecimal.valueOf(100)));
      boolean exists = false;
      for (BillofQuantitiesItemDTO item : picked) {
         if (item.getProductId() == sel.getProductId() && item.getQuantity() == qty) {
            exists = true;
         }
      }
      if (!exists) {
         picked.add(dto);
         availableList.getSelectionModel().clearSelection();
      } else {
         Alert alert = new Alert(Alert.AlertType.WARNING, "This item is already in the list with the same quantity.");
         alert.showAndWait();
      }
      quantityField.clear();
      discountField.clear();
   }

   @FXML
   private void onRemoveItem() {
      var sel = selectedTable.getSelectionModel().getSelectedItem();
      if (sel != null) {
         picked.remove(sel);
         for (int i = 0; i < picked.size(); i++)
            picked.get(i).setLineInBillID(i + 1);
      }
   }

   @FXML
   private void onSave() {
      AgreementDTO dto = new AgreementDTO(
            supplierCombo.getValue().getId(),
            supplierCombo.getValue().getName(),
            startDatePicker.getValue(),
            endDatePicker.getValue(),
            new ArrayList<BillofQuantitiesItemDTO>(picked));
      ServiceResponse<?> r = agreementService.createAgreement(dto);
      if (!r.isSuccess()) {
         new Alert(Alert.AlertType.ERROR, String.join("\n", r.getErrors())).showAndWait();
      } else {
         // Close the dialog
         Stage stage = (Stage) supplierCombo.getScene().getWindow();
         stage.close();
      }
   }

   @FXML
   private void onCancel() {
      Stage stage = (Stage) supplierCombo.getScene().getWindow();
      stage.close();
   }
}