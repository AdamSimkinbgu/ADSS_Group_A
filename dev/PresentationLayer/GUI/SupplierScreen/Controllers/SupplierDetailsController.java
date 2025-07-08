package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.SupplierDTO;
import DTOs.SuppliersModuleDTOs.AgreementDTO;
import DTOs.SuppliersModuleDTOs.BillofQuantitiesItemDTO;
import DTOs.SuppliersModuleDTOs.ContactInfoDTO;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.AgreementService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SupplierDetailsController {
   @FXML
   private Label idLabel, nameLabel;

   @FXML
   private TableView<ContactInfoDTO> contactsTable;
   @FXML
   private TableColumn<ContactInfoDTO, String> contactNameColumn;
   @FXML
   private TableColumn<ContactInfoDTO, String> contactPhoneColumn;
   @FXML
   private TableColumn<ContactInfoDTO, String> contactEmailColumn;

   @FXML
   private TableView<AgreementDTO> agreementsTable;
   @FXML
   private TableColumn<AgreementDTO, Integer> agreementIdColumn;
   @FXML
   private TableColumn<AgreementDTO, String> agreementPeriodColumn;
   @FXML
   private TableColumn<AgreementDTO, Boolean> agreementValidColumn;

   @FXML
   private TableView<BillofQuantitiesItemDTO> boqTable;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, Integer> lineIdColumn;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, String> productNameColumn;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, Integer> productIdColumn;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, Integer> quantityColumn;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, String> discountColumn;

   @FXML
   private Button closeBtn;

   private SupplierDTO supplier;
   private SupplierService supplierService;
   private AgreementService agreementService;
   private final DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;

   public void setSupplier(SupplierDTO supplier) {
      this.supplier = supplier;
   }

   public void setServices(SupplierService s, AgreementService a) {
      this.supplierService = s;
      this.agreementService = a;
   }

   @FXML
   public void initialize() {
      // configure contacts table
      contactNameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
      contactPhoneColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
      contactEmailColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

      // configure agreements table
      agreementIdColumn.setCellValueFactory(a -> new SimpleObjectProperty<>(a.getValue().getAgreementId()));
      agreementPeriodColumn.setCellValueFactory(a -> {
         String start = a.getValue().getAgreementStartDate().format(df);
         String end = a.getValue().getAgreementEndDate().format(df);
         return new SimpleStringProperty(start + " → " + end);
      });
      agreementValidColumn.setCellValueFactory(a -> new SimpleObjectProperty<>(a.getValue().isValid()));

      // configure BOQ table
      lineIdColumn.setCellValueFactory(b -> new SimpleObjectProperty<>(b.getValue().getLineInBillID()));
      productNameColumn.setCellValueFactory(b -> new SimpleStringProperty(b.getValue().getProductName()));
      productIdColumn.setCellValueFactory(b -> new SimpleObjectProperty<>(b.getValue().getProductId()));
      quantityColumn.setCellValueFactory(b -> new SimpleObjectProperty<>(b.getValue().getQuantity()));
      discountColumn.setCellValueFactory(
            b -> new SimpleStringProperty(BigDecimal.ONE
                  .subtract(b.getValue().getDiscountPercent())
                  .multiply(BigDecimal.valueOf(100))
                  .setScale(1, RoundingMode.HALF_UP).toPlainString() + "%"));

      // react to agreement selection
      agreementsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
         if (sel != null) {
            ObservableList<BillofQuantitiesItemDTO> items = FXCollections
                  .observableArrayList(sel.getBillOfQuantitiesItems());
            boqTable.setItems(items);
         } else {
            boqTable.getItems().clear();
         }
      });

      closeBtn.setOnAction(e -> ((Stage) closeBtn.getScene().getWindow()).close());
   }

   public void populate() {
      if (supplier == null)
         return;

      idLabel.setText(String.valueOf(supplier.getId()));
      nameLabel.setText(supplier.getName());

      // load contacts
      ObservableList<ContactInfoDTO> contacts = FXCollections.observableArrayList(supplier.getContactsInfoDTOList());
      contactsTable.setItems(contacts);

      // load agreements
      ServiceResponse<List<AgreementDTO>> resp = agreementService.getAgreementsBySupplierId(supplier.getId());
      if (resp.isSuccess()) {
         agreementsTable.setItems(FXCollections.observableArrayList(resp.getValue()));
      }

      // load BOQ items
      for (AgreementDTO agreement : agreementsTable.getItems()) {
         if (agreement.getBillOfQuantitiesItems() != null) {
            boqTable.setItems(FXCollections.observableArrayList(agreement.getBillOfQuantitiesItems()));
            break; // only show first agreement's BOQ items
         }
      }
   }
}