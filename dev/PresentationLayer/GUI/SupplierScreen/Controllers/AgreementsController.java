package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.AgreementDTO;
import DTOs.SuppliersModuleDTOs.SupplierDTO;
import DomainLayer.SystemFactory;
import DomainLayer.SystemFactory.SupplierModuleComponents;
import ServiceLayer.SuppliersServiceSubModule.AgreementService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class AgreementsController {
   @FXML
   private TableView<AgreementDTO> agreementsTable;
   @FXML
   private TableColumn<AgreementDTO, Integer> idCol;
   @FXML
   private TableColumn<AgreementDTO, Integer> supplierIdCol;
   @FXML
   private TableColumn<AgreementDTO, String> supplierName;
   @FXML
   private TableColumn<AgreementDTO, String> startDateCol, endDateCol, statusCol;
   @FXML
   private TableColumn<AgreementDTO, Void> actionsCol;
   @FXML
   private Button newAgreementBtn;
   @FXML
   private Button refreshBtn;
   @FXML
   private TextField searchField;

   private SystemFactory systemFactory = new SystemFactory();
   private SupplierModuleComponents components = systemFactory.getSupplierModule();
   private final ObservableList<AgreementDTO> allAgreements = FXCollections.observableArrayList();
   private AgreementService agreementService;
   private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

   public void setAgreementService(AgreementService svc) {
      this.agreementService = svc;
   }

   @FXML
   public void initialize() {
      idCol.setCellValueFactory(new PropertyValueFactory<>("agreementId"));
      supplierIdCol.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
      supplierName.setCellValueFactory(a -> {
         ServiceResponse<SupplierDTO> nameSupplier = components.getSupplierService()
               .getSupplierByID(a.getValue().getSupplierId());
         if (nameSupplier.isSuccess())
            return new SimpleStringProperty(nameSupplier.getValue().getName());
         else
            return new SimpleStringProperty("Unknown Supplier");
      });
      startDateCol.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().getAgreementStartDate().format(dtf)));
      endDateCol.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().getAgreementEndDate().format(dtf)));
      statusCol.setCellValueFactory(a -> new SimpleBooleanProperty(a.getValue().isValid())
            .asString());

      // Actions column
      actionsCol.setCellFactory(_ -> new TableCell<>() {
         private final Button viewBtn = new Button("View");
         private final Button editBtn = new Button("Edit");
         private final Button delBtn = new Button("Delete");
         {
            viewBtn.getStyleClass().add("action-button");
            editBtn.getStyleClass().add("secondary-button");
            delBtn.getStyleClass().add("secondary-button");

            viewBtn.setOnAction(_ -> onView(getIndex()));
            editBtn.setOnAction(_ -> onEdit(getIndex()));
            delBtn.setOnAction(_ -> onDelete(getIndex()));
         }

         @Override
         protected void updateItem(Void v, boolean empty) {
            super.updateItem(v, empty);
            if (empty) {
               setGraphic(null);
            } else {
               HBox box = new HBox(5, viewBtn, editBtn, delBtn);
               setGraphic(box);
            }
         }
      });
      // wire up refresh/search to loadAgreements
      loadAgreements();
   }

   private void loadAgreements() {
      ServiceResponse<List<AgreementDTO>> resp = agreementService.getAllAgreements();
      if (resp.isSuccess()) {
         allAgreements.setAll(resp.getValue());
         agreementsTable.setItems(allAgreements);
         // create
      } else {
         new Alert(Alert.AlertType.ERROR, String.join("\n", resp.getErrors()))
               .showAndWait();
      }
   }

   @FXML
   private void onRefresh() {
      loadAgreements();
   }

   @FXML
   private void onNewAgreement() {
      System.out.println("New Agreement dialog requested");
      openAddDialog();
   }

   private void onView(int idx) {
      AgreementDTO dto = agreementsTable.getItems().get(idx);
      System.out.println("View agreement #" + dto.getAgreementId());
      openViewDialog(dto);
   }

   private void onEdit(int idx) {
      AgreementDTO dto = agreementsTable.getItems().get(idx);
      System.out.println("Edit agreement #" + dto.getAgreementId());
      openEditDialog(dto);
   }

   @FXML
   private void onSearch(KeyEvent evt) {
      String filter = searchField.getText().toLowerCase().trim();
      agreementsTable.setItems(
            allAgreements.filtered(a -> Integer.toString(a.getAgreementId()).contains(filter) ||
                  Integer.toString(a.getSupplierId()).contains(filter)));
   }

   private void onDelete(int idx) {
      AgreementDTO dto = agreementsTable.getItems().get(idx);
      Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete agreement #" + dto.getAgreementId() + "?", ButtonType.YES, ButtonType.NO);
      confirm.showAndWait()
            .filter(bt -> bt == ButtonType.YES)
            .ifPresent(_ -> {
               ServiceResponse<?> r = agreementService.removeAgreement(dto.getAgreementId(), dto.getSupplierId());
               if (r.isSuccess())
                  loadAgreements();
               else
                  new Alert(Alert.AlertType.ERROR,
                        String.join("\n", r.getErrors())).showAndWait();
            });
   }

   private void openAddDialog() {
      FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/GUI/SupplierScreen/Views/AgreementAddView.fxml"));
      try {
         Parent root = loader.load();
         showModal(root, "New Agreement");
      } catch (Exception e) {
         e.printStackTrace();
         new Alert(Alert.AlertType.ERROR, "Failed to open agreement add form: " + e.getMessage())
               .showAndWait();
      }
      loadAgreements();
   }

   private void openEditDialog(AgreementDTO dto) {
      FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/GUI/SupplierScreen/Views/AgreementEditView.fxml"));
      try {
         Parent root = loader.load();
         AgreementEditController ctrl = loader.getController();
         ctrl.init(dto);
         showModal(root, "New Agreement");
      } catch (Exception e) {
         e.printStackTrace();
         new Alert(Alert.AlertType.ERROR, "Failed to open agreement add form: " + e.getMessage())
               .showAndWait();
      }
      loadAgreements();
   }

   private void openViewDialog(AgreementDTO dto) {
      FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/GUI/SupplierScreen/Views/AgreementDetailsView.fxml"));
      try {
         Parent root = loader.load();
         AgreementDetailsController ctrl = loader.getController();
         ctrl.init(dto);
         showModal(root, "New Agreement");
      } catch (Exception e) {
         e.printStackTrace();
         new Alert(Alert.AlertType.ERROR, "Failed to open agreement add form: " + e.getMessage())
               .showAndWait();
      }
   }

   private void showModal(Parent root, String title) {
      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setTitle(title);
      stage.setScene(new Scene(root));
      stage.showAndWait();
   }
}