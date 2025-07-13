package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.SupplierDTO;
import DTOs.SuppliersModuleDTOs.SupplierProductDTO;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.List;

public class SupplierProductsController {
   // — Suppliers UI
   @FXML
   private TableView<SupplierDTO> suppliersTable;
   @FXML
   private TableColumn<SupplierDTO, Integer> supColId;
   @FXML
   private TableColumn<SupplierDTO, String> supColName;
   @FXML
   private TableColumn<SupplierDTO, Void> supColAction;
   @FXML
   private Button refreshSuppliersBtn;

   // — Products UI
   @FXML
   private Label selectedSupplierLabel;
   @FXML
   private TableView<SupplierProductDTO> productsTable;
   @FXML
   private TableColumn<SupplierProductDTO, Integer> colProductId;
   @FXML
   private TableColumn<SupplierProductDTO, String> colCatalogNumber;
   @FXML
   private TableColumn<SupplierProductDTO, String> colName;
   @FXML
   private TableColumn<SupplierProductDTO, java.math.BigDecimal> colPrice;
   @FXML
   private TableColumn<SupplierProductDTO, java.math.BigDecimal> colWeight;
   @FXML
   private TableColumn<SupplierProductDTO, Integer> colExpires;
   @FXML
   private TableColumn<SupplierProductDTO, String> colManufacturer;
   @FXML
   private TableColumn<SupplierProductDTO, Void> colProdActions;
   @FXML
   private Button refreshProductsBtn;
   @FXML
   private Button addProductBtn;

   private SupplierService supplierService;
   private final ObservableList<SupplierDTO> suppliers = FXCollections.observableArrayList();
   private final ObservableList<SupplierProductDTO> products = FXCollections.observableArrayList();
   private int selectedSupplierId = -1;

   /** Must be called *after* FXMLLoader.load() to inject the service. */
   public void init(SupplierService svc) {
      this.supplierService = svc;
      loadSuppliers();
   }

   @FXML
   private void initialize() {
      // --- suppliers table setup ---
      supColId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
      supColName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
      supColAction.setCellFactory(tv -> new TableCell<>() {
         private final Button btn = new Button("Select");
         {
            btn.getStyleClass().add("primary-button");
            btn.setOnAction(e -> {
               SupplierDTO dto = getTableView().getItems().get(getIndex());
               selectSupplier(dto);
            });
         }

         @Override
         protected void updateItem(Void v, boolean empty) {
            super.updateItem(v, empty);
            setGraphic(empty ? null : btn);
         }
      });
      suppliersTable.setItems(suppliers);

      // --- products table setup ---
      colProductId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getProductId()));
      colCatalogNumber.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSupplierCatalogNumber()));
      colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
      colPrice.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPrice()));
      colWeight.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getWeight()));
      colExpires.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getExpiresInDays()));
      colManufacturer.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getManufacturerName()));

      colProdActions.setCellFactory(tv -> new TableCell<>() {
         private final Button editBtn = new Button("Edit");
         private final Button delBtn = new Button("Delete");
         {
            editBtn.getStyleClass().add("secondary-button");
            delBtn.getStyleClass().add("secondary-button");
            editBtn.setOnAction(e -> openEditSupplierProductForm(getTableView().getItems().get(getIndex())));
            delBtn.setOnAction(e -> deleteProduct(getTableView().getItems().get(getIndex())));
         }

         @Override
         protected void updateItem(Void v, boolean empty) {
            super.updateItem(v, empty);
            setGraphic(empty ? null : new HBox(5, editBtn, delBtn));
         }
      });
      productsTable.setItems(products);
   }

   // ─── Suppliers ─────────────────────────────────────────────────

   @FXML
   private void onRefreshSuppliers() {
      loadSuppliers();
      clearProductsSection();
   }

   private void loadSuppliers() {
      ServiceResponse<List<SupplierDTO>> resp = supplierService.getAllSuppliers();
      if (resp.isSuccess()) {
         suppliers.setAll(resp.getValue());
      } else {
         new Alert(Alert.AlertType.ERROR, String.join("\n", resp.getErrors()))
               .showAndWait();
      }
   }

   private void selectSupplier(SupplierDTO dto) {
      selectedSupplierId = dto.getId();
      selectedSupplierLabel.setText(dto.getName() + " (#" + dto.getId() + ")");
      loadProducts();
   }

   // ─── Products ──────────────────────────────────────────────────

   @FXML
   private void onRefreshProducts() {
      loadProducts();
   }

   @FXML
   private void onAddProduct() {
      openAddSupplierProductForm(null);
   }

   protected void openEditSupplierProductForm(SupplierProductDTO supplierProductDTO) {
      try {
         FXMLLoader loader = new FXMLLoader(
               getClass().getResource("/GUI/SupplierScreen/Views/SupplierProductEditView.fxml"));
         Parent root = loader.load();
         SupplierProductEditController form = loader.getController();
         form.init(supplierProductDTO);
         Stage stage = new Stage();
         stage.initModality(Modality.APPLICATION_MODAL);
         stage.setTitle("Edit " + supplierProductDTO.getName() + ": " + supplierProductDTO.getManufacturerName()
               + " (From Supplier " + selectedSupplierLabel.getText() + ")");
         stage.setScene(new Scene(root));
         stage.showAndWait();
         loadProducts();
      } catch (IOException ex) {
         ex.printStackTrace();
         new Alert(Alert.AlertType.ERROR, "Failed to open product edit form: " + ex.getMessage())
               .showAndWait();
      }
   }

   private void loadProducts() {
      if (selectedSupplierId < 0)
         return;
      ServiceResponse<?> resp = supplierService.listProducts(selectedSupplierId);
      if (resp.isSuccess()) {
         // noinspection unchecked
         products.setAll((List<SupplierProductDTO>) resp.getValue());
      } else {
         new Alert(Alert.AlertType.ERROR, String.join("\n", resp.getErrors()))
               .showAndWait();
      }
   }

   private void deleteProduct(SupplierProductDTO dto) {
      Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete “" + dto.getName() + "” From Supplier “" + selectedSupplierLabel.getText() + "”?",
            ButtonType.YES, ButtonType.NO);
      confirm.setTitle("Delete Product");
      confirm.setHeaderText("Are you sure you want to delete this product?");
      confirm.setContentText(
            "This action cannot be undone.\nThe product will be removed from the supplier (orders will not be affected).");
      confirm.getDialogPane().setPrefSize(400, 200);
      confirm.showAndWait().ifPresent(bt -> {
         if (bt == ButtonType.YES) {
            ServiceResponse<?> r = supplierService.removeProduct(dto.getProductId(), selectedSupplierId);
            if (r.isSuccess()) {
               loadProducts();
            } else {
               new Alert(Alert.AlertType.ERROR, String.join("\n", r.getErrors()))
                     .showAndWait();
            }
         }
      });
   }

   private void openAddSupplierProductForm(SupplierProductDTO existing) {
      try {
         FXMLLoader loader = new FXMLLoader(
               getClass().getResource("/GUI/SupplierScreen/Views/SupplierProductAddView.fxml"));
         Parent root = loader.load();

         if (selectedSupplierId < 0) {
            new Alert(Alert.AlertType.INFORMATION, "Please select a supplier first.").showAndWait();
            return;
         }

         SupplierProductAddController form = loader.getController();
         form.init(selectedSupplierId);

         Stage stage = new Stage();
         stage.initModality(Modality.APPLICATION_MODAL);
         stage.setTitle("Add Product");
         stage.setScene(new Scene(root));
         stage.showAndWait();

         loadProducts();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   private void clearProductsSection() {
      selectedSupplierId = -1;
      selectedSupplierLabel.setText("(none)");
      products.clear();
   }
}