package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.CatalogProductDTO;
import DTOs.SuppliersModuleDTOs.OrderInfoDTO;
import DomainLayer.SystemFactory;
import DomainLayer.SystemFactory.SupplierModuleComponents;
import DTOs.SuppliersModuleDTOs.OrderDTO;
import ServiceLayer.SuppliersServiceSubModule.OrderService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

/**
 * Dialog controller for creating/editing a single Order.
 */
public class OrderFormController {
   @FXML
   private DatePicker orderDatePicker;
   @FXML
   private ListView<CatalogProductDTO> availableList;
   @FXML
   private TableView<Picked> selectedTable;
   @FXML
   private TableColumn<Picked, Integer> selProdIdCol;
   @FXML
   private TableColumn<Picked, String> selProdNameCol;
   @FXML
   private TableColumn<Picked, Integer> selQtyCol;

   private OrderService service;
   // private OrderInfoDTO editingDto; // null = new
   private SystemFactory systemFactory = new SystemFactory();
   SupplierModuleComponents components = systemFactory.getSupplierModule();
   private final ObservableList<CatalogProductDTO> available = FXCollections.observableArrayList();
   private final ObservableList<Picked> picked = FXCollections.observableArrayList();

   public void init(OrderDTO existing, String mode) {
      this.service = components.getOrderService();
      // load catalog
      ServiceResponse<List<CatalogProductDTO>> resp = components.getSupplierService().getAllProducts();
      if (resp.isSuccess())
         available.setAll(resp.getValue());
      availableList.setItems(available);

      selProdIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().product.getProductId()).asObject());
      selProdNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().product.getProductName()));
      selQtyCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().quantity).asObject());
      selectedTable.setItems(picked);

      if (existing != null) {
         // editing: populate date + products
         orderDatePicker.setValue(existing.getOrderDate());
         existing.getItems().forEach(item -> picked.add(
               new Picked(
                     available.stream().filter(p -> p.getProductId() == item.getProductId()).findFirst().orElse(null),
                     item.getQuantity(),
                     null)));
         // (we could map back to CatalogProductDTO by id)
      } else {
         orderDatePicker.setValue(LocalDate.now());
      }
   }

   @FXML
   private void onAddProduct() {
      var sel = availableList.getSelectionModel().getSelectedItem();
      if (sel == null)
         return;
      // add with qty=1
      picked.add(new Picked(sel, 1, null));
   }

   @FXML
   private void onRemoveProduct() {
      var p = selectedTable.getSelectionModel().getSelectedItem();
      if (p != null)
         picked.remove(p);
   }

   @FXML
   private void onSave() {
      // build DTO
      var items = new HashMap<Integer, Integer>();
      for (var p : picked)
         items.put(p.product.getProductId(), p.quantity);
      var info = new OrderInfoDTO(orderDatePicker.getValue(), items);
      ServiceResponse<?> resp;
      // if editingDto != null => updateOrder else createOrder
      resp = service.createOrder(info);
      if (resp.isSuccess()) {
         close();
      } else {
         new Alert(Alert.AlertType.ERROR, String.join("\n", resp.getErrors())).showAndWait();
      }
   }

   @FXML
   private void onCancel() {
      close();
   }

   private void close() {
      ((Stage) orderDatePicker.getScene().getWindow()).close();
   }

   /** helper for table rows */
   public static class Picked {
      public final CatalogProductDTO product;
      public int quantity;

      public Picked(CatalogProductDTO p, int q, Object unused) {
         this.product = p;
         this.quantity = q;
      }
   }
}