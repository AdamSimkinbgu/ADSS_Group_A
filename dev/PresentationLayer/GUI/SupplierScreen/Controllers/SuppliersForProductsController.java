package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.SupplierDTO;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.List;

public class SuppliersForProductsController {
   @FXML
   private TableView<SupplierDTO> suppliersTable;
   @FXML
   private TableColumn<SupplierDTO, Integer> colId;
   @FXML
   private TableColumn<SupplierDTO, String> colName;
   @FXML
   private TableColumn<SupplierDTO, Void> colActions;
   @FXML
   private Button refreshBtn;

   private SupplierService supplierService;
   private final ObservableList<SupplierDTO> suppliers = FXCollections.observableArrayList();

   /** Call this from wherever you load the grid to inject your service */
   public void init(SupplierService svc) {
      this.supplierService = svc;
      loadSuppliers();
   }

   @FXML
   public void initialize() {
      colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
      colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

      // Actions cell: “Manage Products”
      colActions.setCellFactory(tv -> new TableCell<>() {
         private final Button btn = new Button("Manage Products");
         {
            btn.getStyleClass().add("primary-button");
            btn.setOnAction(e -> {
               SupplierDTO dto = getTableView().getItems().get(getIndex());
               openProducts(dto.getId());
            });
         }

         @Override
         protected void updateItem(Void v, boolean empty) {
            super.updateItem(v, empty);
            setGraphic(empty ? null : btn);
         }
      });

      suppliersTable.setItems(suppliers);
   }

   @FXML
   private void onRefresh() {
      loadSuppliers();
   }

   private void loadSuppliers() {
      ServiceResponse<List<SupplierDTO>> resp = supplierService.getAllSuppliers();
      if (resp.isSuccess()) {
         suppliers.setAll(resp.getValue());
      } else {
         new Alert(Alert.AlertType.ERROR,
               String.join("\n", resp.getErrors()))
               .showAndWait();
      }
   }

   private void openProducts(int supplierId) {
      try {
         FXMLLoader loader = new FXMLLoader(
               getClass().getResource("/GUI/SupplierScreen/Views/ProductsTab.fxml"));
         Parent root = loader.load();

         SupplierProductsController ctrl = loader.getController();
         // inject service + supplierId, then refresh
         ctrl.init(supplierService);

         Stage stage = new Stage();
         stage.initModality(Modality.APPLICATION_MODAL);
         stage.setTitle("Products for Supplier #" + supplierId);
         stage.setScene(new Scene(root));
         stage.showAndWait();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }
}