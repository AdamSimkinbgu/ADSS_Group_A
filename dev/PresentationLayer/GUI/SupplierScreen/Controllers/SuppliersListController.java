package PresentationLayer.GUI.SupplierScreen.Controllers;

import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.AgreementService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import DTOs.SuppliersModuleDTOs.SupplierDTO;
import PresentationLayer.GUI.Common.Navigation.ScreensEnum;
import DTOs.SuppliersModuleDTOs.AgreementDTO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class SuppliersListController {
   @FXML
   private TextField searchField;
   @FXML
   private TableView<SupplierDTO> suppliersTable;
   @FXML
   private TableColumn<SupplierDTO, String> idColumn;
   @FXML
   private TableColumn<SupplierDTO, String> nameColumn;
   @FXML
   private TableColumn<SupplierDTO, Integer> contactsColumn;
   @FXML
   private TableColumn<SupplierDTO, Integer> agreementsColumn;
   @FXML
   private TableColumn<SupplierDTO, Void> actionsColumn;
   @FXML
   private Button addBtn;
   @FXML
   private Button refreshBtn;

   private SupplierService supplierService;
   private AgreementService agreementService;

   private final ObservableList<SupplierDTO> masterData = FXCollections.observableArrayList();

   public void setSupplierService(SupplierService service) {
      this.supplierService = service;
   }

   public void setAgreementService(AgreementService service) {
      this.agreementService = service;
   }

   @FXML
   public void initialize() {
      // Wire up columns
      idColumn.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getId())));
      nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
      contactsColumn.setCellValueFactory(cell -> {
         List<?> contacts = cell.getValue().getContactsInfoDTOList();
         int count = (contacts != null ? contacts.size() : 0);
         return new SimpleObjectProperty<>(count);
      });
      agreementsColumn.setCellValueFactory(cell -> {
         ServiceResponse<List<AgreementDTO>> resp = agreementService.getAgreementsBySupplierId(cell.getValue().getId());
         int count = resp.isSuccess() ? resp.getValue().size() : 0;
         return new SimpleObjectProperty<>(count);
      });

      // Add action buttons
      addActionButtons();

      // Searchable list
      FilteredList<SupplierDTO> filtered = new FilteredList<>(masterData, s -> true);
      searchField.textProperty().addListener((obs, old, nw) -> {
         String term = (nw == null ? "" : nw).toLowerCase();
         filtered.setPredicate(dto -> dto.getName().toLowerCase().contains(term)
               || String.valueOf(dto.getId()).contains(term));
      });
      suppliersTable.setItems(filtered);

      // Toolbar actions
      addBtn.setOnAction(e -> onAddSupplier());
      refreshBtn.setOnAction(e -> loadSuppliers());

      // Initial data load
      loadSuppliers();
   }

   private void addActionButtons() {
      Callback<TableColumn<SupplierDTO, Void>, TableCell<SupplierDTO, Void>> factory = col -> new TableCell<>() {
         private final Button viewBtn = new Button("View");
         private final Button editBtn = new Button("Edit");
         private final Button removeBtn = new Button("Remove");

         {
            viewBtn.setOnAction(e -> onView(getTableView().getItems().get(getIndex())));
            editBtn.setOnAction(e -> onEdit(getTableView().getItems().get(getIndex())));
            removeBtn.setOnAction(e -> onRemove(getTableView().getItems().get(getIndex())));
         }

         @Override
         protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
               setGraphic(null);
            } else {
               HBox box = new HBox(5, viewBtn, editBtn, removeBtn);
               setGraphic(box);
            }
         }

      };
      actionsColumn.setCellFactory(factory);
   }

   private void loadSuppliers() {
      masterData.clear();
      ServiceResponse<List<SupplierDTO>> resp = supplierService.getAllSuppliers();
      if (resp.isSuccess()) {
         masterData.addAll(resp.getValue());
      }
   }

   private void onAddSupplier() {
      openWindow(
            ScreensEnum.SUPPLIER_ADD.getFxmlPath(),
            loader -> {
               SupplierAddController ctrl = loader.getController();
               ctrl.setService(supplierService);
            });
   }

   private void onView(SupplierDTO dto) {
      openWindow(
            ScreensEnum.SUPPLIER_DETAILS.getFxmlPath(),
            loader -> {
               SupplierDetailsController ctrl = loader.getController();
               ctrl.setSupplier(dto);
               ctrl.setServices(supplierService, agreementService);
               ctrl.populate();
               ;
            });
   }

   private void onEdit(SupplierDTO dto) {
      openWindow(
            ScreensEnum.SUPPLIER_EDIT.getFxmlPath(),
            loader -> {
               SupplierEditController ctrl = loader.getController();
               ctrl.setSupplier(dto);
               ctrl.setServices(supplierService, agreementService);
               ctrl.populateForm();
            });
   }

   private void onRemove(SupplierDTO supplierDTO) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Remove Supplier");
      alert.setHeaderText("Are you sure you want to remove this supplier?");
      alert.setContentText("This action cannot be undone.");

      ButtonType confirmButton = new ButtonType("Remove", ButtonBar.ButtonData.OK_DONE);
      ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
      alert.getButtonTypes().setAll(confirmButton, cancelButton);

      if (alert.showAndWait().orElse(cancelButton) == confirmButton) {
         ServiceResponse<?> response = supplierService.removeSupplier(supplierDTO.getId());
         if (response.isSuccess()) {
            loadSuppliers(); // refresh list
         } else {
            // handle error (e.g. show error message)
            System.err.println("Failed to remove supplier: " + response.getErrors().toString());
         }
      }
   }

   /**
    * Opens an FXML in a new modal window, injecting controller
    */
   private void openWindow(String fxmlPath, Consumer<FXMLLoader> initializer) {
      try {
         FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
         Parent root = loader.load();
         initializer.accept(loader);

         Stage stage = new Stage();
         stage.initModality(Modality.APPLICATION_MODAL);
         stage.setScene(new Scene(root));
         stage.showAndWait();

         loadSuppliers(); // refresh after close
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }
}