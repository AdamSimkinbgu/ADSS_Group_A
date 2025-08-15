package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.AgreementDTO;
import DTOs.SuppliersModuleDTOs.BillofQuantitiesItemDTO;
import DomainLayer.SystemFactory;
import ServiceLayer.SuppliersServiceSubModule.AgreementService;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class AgreementDetailsController {
   @FXML
   private Label titleLabel;
   @FXML
   private ComboBox<String> supplierCombo;
   @FXML
   private Label agreementIdLabel;
   @FXML
   private DatePicker startDatePicker;
   @FXML
   private DatePicker endDatePicker;
   @FXML
   private TableView<BillofQuantitiesItemDTO> boqTable;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, Integer> colLineId;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, String> colProduct;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, Integer> colQuantity;
   @FXML
   private TableColumn<BillofQuantitiesItemDTO, Integer> colDiscount;
   @FXML
   private Button closeBtn;

   private SystemFactory systemFactory = new SystemFactory();
   private AgreementService agreementService = systemFactory.getSupplierModule().getAgreementService();
   private SupplierService supplierService = systemFactory.getSupplierModule().getSupplierService();

   /**
    * Initializes the dialog with an AgreementDTO.
    */
   public void init(AgreementDTO dto) {
      // Title
      titleLabel.setText("Agreement #" + dto.getAgreementId());

      // Supplier (read-only)
      String supplierDisplay = supplierService.getSupplierByID(dto.getSupplierId())
            .getValue().getName() + " (ID: " + dto.getSupplierId() + ")";
      supplierCombo.setItems(FXCollections.observableArrayList(supplierDisplay));
      supplierCombo.getSelectionModel().selectFirst();

      // Agreement ID
      agreementIdLabel.setText(Integer.toString(dto.getAgreementId()));

      // Dates (read-only)
      startDatePicker.setValue(dto.getAgreementStartDate());
      startDatePicker.setDisable(true);
      endDatePicker.setValue(dto.getAgreementEndDate());
      endDatePicker.setDisable(true);

      // BOQ items (null-safe)
      List<BillofQuantitiesItemDTO> lines = agreementService.getAgreement(dto.getAgreementId()).getValue()
            .getBillOfQuantitiesItems();
      if (lines == null) {
         lines = Collections.emptyList();
      }

      // Configure table columns
      colLineId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getLineInBillID()));
      colProduct.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getProductName()));
      colQuantity.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getQuantity()));
      colDiscount.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDiscountPercent().intValue()));

      // Populate table
      boqTable.setItems(FXCollections.observableArrayList(lines));
      boqTable.setEditable(false);

      // Close button
      closeBtn.setOnAction(e -> ((Stage) closeBtn.getScene().getWindow()).close());
   }

   /**
    * Shows the View Agreement dialog modally.
    */
   public static void showDialog(AgreementDTO dto) {
      try {
         FXMLLoader loader = new FXMLLoader(
               AgreementDetailsController.class.getResource(
                     "/GUI/SupplierScreen/Views/AgreementView.fxml"));
         Parent root = loader.load();
         AgreementDetailsController ctrl = loader.getController();
         ctrl.init(dto);

         Stage stage = new Stage();
         stage.initModality(Modality.APPLICATION_MODAL);
         stage.setTitle("View Agreement #" + dto.getAgreementId());
         stage.setScene(new Scene(root));
         stage.showAndWait();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }
}