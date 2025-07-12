package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.SupplierDTO;
import DTOs.SuppliersModuleDTOs.SupplierProductDTO;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class SupplierProductsController {
   @FXML
   private TableView<SupplierProductDTO> productsTable;
   @FXML
   private TableColumn<SupplierProductDTO, Integer> colProductId;
   @FXML
   private TableColumn<SupplierProductDTO, String> colCatalogNumber, colName, colManufacturer;
   @FXML
   private TableColumn<SupplierProductDTO, BigDecimal> colPrice, colWeight;
   @FXML
   private TableColumn<SupplierProductDTO, Integer> colExpires;
   @FXML
   private TableColumn<SupplierProductDTO, Void> colActions;
   @FXML
   private Button addBtn, refreshBtn;

   @FXML
   private TableView<SupplierDTO> suppliersTable;
   @FXML
   private TableColumn<SupplierDTO, Integer> colId;

   private SupplierService supplierService;
   private final ObservableList<SupplierDTO> suppliers = FXCollections.observableArrayList();

   /** Call this from wherever you load the grid to inject your service */
   public void init(SupplierService svc) {
      this.supplierService = svc;
      loadSuppliers();
      initialize();
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
               SupplierProductDTO dto = getTableView().getItems().get(getIndex());
               openProducts(dto.getSupplierId());
            });
         }

         @Override
         protected void updateItem(Void v, boolean empty) {
            super.updateItem(v, empty);
            setGraphic(empty ? null : btn);
         }
      });

      suppliersTable.setItems(suppliers);

      colProductId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getProductId()));
      colCatalogNumber.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSupplierCatalogNumber()));
      colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
      colPrice.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPrice()));
      colWeight.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getWeight()));
      colExpires.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getExpiresInDays()));
      colManufacturer.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getManufacturerName()));

      // actions column
      colActions.setCellFactory(_ -> new TableCell<>() {
         private final Button editBtn = new Button("✎");
         private final Button delBtn = new Button("🗑");
         {
            editBtn.getStyleClass().add("secondary-button");
            delBtn.getStyleClass().add("secondary-button");
            editBtn.setOnAction(_ -> openForm(getTableView().getItems().get(getIndex()), false));
            delBtn.setOnAction(_ -> deleteProduct(getTableView().getItems().get(getIndex())));
         }

         @Override
         protected void updateItem(Void v, boolean empty) {
            super.updateItem(v, empty);
            if (empty) {
               setGraphic(null);
            } else {
               setGraphic(new HBox(5, editBtn, delBtn));
            }
         }
      });
      productsTable.setItems(items);
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
         ctrl.setSupplierIdAndLoadProducts(supplierId);

         Stage stage = new Stage();
         stage.initModality(Modality.APPLICATION_MODAL);
         stage.setTitle("Products for Supplier #" + supplierId);
         stage.setScene(new Scene(root));
         stage.showAndWait();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   private int supplierId = -1;
   private final ObservableList<SupplierProductDTO> items = FXCollections.observableArrayList();

   @FXML
   public void initializea() {
      colProductId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getProductId()));
      colCatalogNumber.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSupplierCatalogNumber()));
      colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
      colPrice.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPrice()));
      colWeight.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getWeight()));
      colExpires.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getExpiresInDays()));
      colManufacturer.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getManufacturerName()));

      // actions column
      colActions.setCellFactory(_ -> new TableCell<>() {
         private final Button editBtn = new Button("✎");
         private final Button delBtn = new Button("🗑");
         {
            editBtn.getStyleClass().add("secondary-button");
            delBtn.getStyleClass().add("secondary-button");
            editBtn.setOnAction(_ -> openForm(getTableView().getItems().get(getIndex()), false));
            delBtn.setOnAction(_ -> deleteProduct(getTableView().getItems().get(getIndex())));
         }

         @Override
         protected void updateItem(Void v, boolean empty) {
            super.updateItem(v, empty);
            if (empty) {
               setGraphic(null);
            } else {
               setGraphic(new HBox(5, editBtn, delBtn));
            }
         }
      });
      productsTable.setItems(items);
   }

   @FXML
   private void onAdd() {
      openForm(null, true);
   }

   private void loadProducts() {
      ServiceResponse<?> resp = supplierService.listProducts(supplierId);
      if (resp.isSuccess()) {
         items.setAll((List<SupplierProductDTO>) resp.getValue());
      } else {
         new Alert(Alert.AlertType.ERROR, String.join("\n", resp.getErrors()))
               .showAndWait();
      }
   }

   private void deleteProduct(SupplierProductDTO dto) {
      var alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete “" + dto.getName() + "”?",
            ButtonType.YES, ButtonType.NO);
      alert.showAndWait().ifPresent(b -> {
         if (b == ButtonType.YES) {
            ServiceResponse<?> r = supplierService.removeProduct(dto.getProductId(), supplierId);
            if (r.isSuccess())
               loadProducts();
            else
               new Alert(Alert.AlertType.ERROR, String.join("\n", r.getErrors()))
                     .showAndWait();
         }
      });
   }

   private void openForm(SupplierProductDTO existing, boolean creating) {
      try {
         FXMLLoader loader = new FXMLLoader(
               getClass().getResource("/GUI/SupplierScreen/Views/ProductForm.fxml"));
         Parent root = loader.load();

         ProductFormController form = loader.getController();
         form.init(creating, existing, supplierId, supplierService);

         Stage stage = new Stage();
         stage.initModality(Modality.APPLICATION_MODAL);
         stage.setTitle(creating ? "Add Product" : "Edit Product");
         stage.setScene(new Scene(root));
         stage.showAndWait();

         // after modal closes
         loadProducts();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   public void setSupplierIdAndLoadProducts(int supplierId2) {
      this.supplierId = supplierId2;
      loadProducts();
   }
}