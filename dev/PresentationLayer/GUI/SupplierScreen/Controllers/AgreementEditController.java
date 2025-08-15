package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.AgreementDTO;
import DTOs.SuppliersModuleDTOs.BillofQuantitiesItemDTO;
import DTOs.SuppliersModuleDTOs.SupplierDTO;
import DTOs.SuppliersModuleDTOs.SupplierProductDTO;
import DomainLayer.SystemFactory;
import ServiceLayer.SuppliersServiceSubModule.AgreementService;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AgreementEditController {
   @FXML
   private ComboBox<SupplierDTO> supplierCombo;
   @FXML
   private TextField agreementIdField;
   @FXML
   private DatePicker startDatePicker;
   @FXML
   private DatePicker endDatePicker;

   @FXML
   private TextField productSearchField;
   @FXML
   private ListView<SupplierProductDTO> availableProductsList;
   @FXML
   private Button addLineBtn, removeLineBtn;
   @FXML
   private TableView<BillofQuantitiesItemDTO> selectedLinesTable;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, Integer> colLineId;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, String> colProduct;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, Integer> colQuantity;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, Integer> colDiscount;

   private SystemFactory systemFactory = new SystemFactory();
   private AgreementService agreementService = systemFactory.getSupplierModule().getAgreementService();
   private SupplierService supplierService = systemFactory.getSupplierModule().getSupplierService();
   private AgreementDTO editing;

   private final ObservableList<BillofQuantitiesItemDTO> selectedLines = FXCollections.observableArrayList();
   private final ObservableList<SupplierProductDTO> availableProducts = FXCollections.observableArrayList();

   /**
    * Call after FXML load
    */
   public void init(AgreementDTO dto) {
      this.editing = dto;

      // populate suppliers (for display only)
      var allSuppliers = supplierService.getAllSuppliers().getValue();
      supplierCombo.setItems(FXCollections.observableArrayList(allSuppliers));
      supplierCombo.getSelectionModel().select(
            allSuppliers.stream()
                  .filter(s -> s.getId() == dto.getSupplierId())
                  .findFirst().orElse(null));

      agreementIdField.setText(Integer.toString(dto.getAgreementId()));
      startDatePicker.setValue(dto.getAgreementStartDate());
      endDatePicker.setValue(dto.getAgreementEndDate());

      // load available products for this supplier
      ServiceResponse<?> prods = supplierService.listProducts(dto.getSupplierId());
      if (prods.isSuccess()) {
         List<SupplierProductDTO> products = (List<SupplierProductDTO>) prods.getValue();
         availableProducts.setAll(products);
         availableProductsList.setItems(availableProducts);
      } else {
         new Alert(Alert.AlertType.ERROR, "Failed to load products: " + String.join("\n", prods.getErrors()))
               .showAndWait();
      }

      // pre-load existing BOQ lines
      selectedLines.clear();
      List<BillofQuantitiesItemDTO> boq = dto.getBillOfQuantitiesItems();
      if (boq != null) {
         selectedLines.addAll(boq);
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
      }
      selectedLinesTable.setItems(selectedLines);

      // configure columns
      colLineId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getLineInBillID()));
      colProduct.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getProductName()));

      colQuantity.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getQuantity()));
      colQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
      colQuantity.setOnEditCommit(evt -> {
         int val = Math.max(1, evt.getNewValue());
         evt.getRowValue().setQuantity(val);
         selectedLinesTable.refresh();
      });

      colDiscount.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDiscountPercent().intValue()));
      colDiscount.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
      colDiscount.setOnEditCommit(evt -> {
         int pct = Math.max(0, Math.min(100, evt.getNewValue()));
         evt.getRowValue().setDiscountPercent(BigDecimal.valueOf(pct));
         selectedLinesTable.refresh();
      });
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
   }

   @FXML
   private void onAddLine() {
      var sel = availableProductsList.getSelectionModel().getSelectedItem();
      if (sel == null)
         return;
      // lineId and agreementId will be set by backend
      var dto = new BillofQuantitiesItemDTO(
            editing.getAgreementId(),
            -1,
            sel.getName(),
            sel.getProductId(),
            1,
            java.math.BigDecimal.ZERO);
      if (!selectedLines.contains(dto))
         selectedLines.add(dto);
   }

   @FXML
   private void onRemoveLine() {
      var sel = selectedLinesTable.getSelectionModel().getSelectedItem();
      if (sel != null)
         selectedLines.remove(sel);
   }

   @FXML
   private void onCancel() {
      ((Stage) agreementIdField.getScene().getWindow()).close();
   }

   @FXML
   private void onSave() {
      // validate
      LocalDate start = startDatePicker.getValue();
      LocalDate end = endDatePicker.getValue();
      if (start == null || end == null || end.isBefore(start)) {
         new Alert(Alert.AlertType.ERROR, "Please select a valid end date").showAndWait();
         return;
      }

      editing.setAgreementStartDate(start);
      editing.setAgreementEndDate(end);
      editing.setBillOfQuantitiesItems(selectedLines);

      ServiceResponse<?> resp = agreementService.updateAgreement(editing.getAgreementId(), editing);
      if (resp.isSuccess()) {
         ((Stage) agreementIdField.getScene().getWindow()).close();
      } else {
         new Alert(Alert.AlertType.ERROR, String.join("\n", resp.getErrors()))
               .showAndWait();
      }
   }

   /**
    * Filter available products by search
    */
   @FXML
   private void onProductSearch() {
      String filter = productSearchField.getText().toLowerCase().trim();
      availableProductsList.setItems(
            availableProducts.stream()
                  .filter(p -> p.getName().toLowerCase().contains(filter))
                  .collect(Collectors.toCollection(FXCollections::observableArrayList)));
   }
}