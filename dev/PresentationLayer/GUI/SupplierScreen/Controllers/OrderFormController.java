package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.CatalogProductDTO;
import DTOs.SuppliersModuleDTOs.OrderInfoDTO;
import DTOs.SuppliersModuleDTOs.OrderItemLineDTO;
import DTOs.SuppliersModuleDTOs.OrderDTO;
import ServiceLayer.SuppliersServiceSubModule.OrderService;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class OrderFormController {

   @FXML
   private DatePicker datePicker;
   @FXML
   private ComboBox<CatalogProductDTO> productCombo;
   @FXML
   private Spinner<Integer> qtySpinner;
   @FXML
   private TableView<OrderItemLineDTO> linesTable;

   private final ObservableList<OrderItemLineDTO> lines = FXCollections.observableArrayList();

   private OrderService orderService;
   private SupplierService supplierService; // to fetch catalog
   private OrderDTO editing; // non-null if we’re editing

   /** Call this before showing: */
   public void setServices(OrderService os, SupplierService ss) {
      this.orderService = os;
      this.supplierService = ss;
   }

   /** Call this if you want to edit an existing order: */
   public void setOrder(OrderDTO existing) {
      this.editing = existing;
   }

   @FXML
   public void initialize() {
      // 1) configure spinner
      qtySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1_000, 1));

      // 2) fetch catalog
      ServiceResponse<?> resp = supplierService.getAllProducts();
      if (resp.isSuccess()) {
         @SuppressWarnings("unchecked")
         var catalog = (java.util.List<CatalogProductDTO>) resp.getValue();
         productCombo.getItems().setAll(catalog);
      }

      // 3) set up table
      linesTable.setItems(lines);
      // linesTable.getColumns().get(0)
      // .setCellValueFactory(c -> new ReadOnlyObjectWrapper<Integer>()
      // .of(new Integer.valueOf(c.getValue().getProductId())));

      // 4) if editing, preload
      if (editing != null) {
         datePicker.setValue(editing.getOrderDate());
         for (var item : editing.getItems()) {
            lines.add(item);
         }
      } else {
         datePicker.setValue(LocalDate.now().plusDays(1));
      }
   }

   @FXML
   private void onAddLine() {
      var prod = productCombo.getValue();
      if (prod == null) {
         alert("Select a product first");
         return;
      }
      int qty = qtySpinner.getValue();
      lines.add(new OrderItemLineDTO(
            prod.getProductId(), qty));
   }

   /** invoked by “Remove” button in each row */
   private void removeLine(int idx) {
      if (idx >= 0 && idx < lines.size()) {
         lines.remove(idx);
      }
   }

   @FXML
   private void onSave() {
      LocalDate date = datePicker.getValue();
      if (date == null || !date.isAfter(LocalDate.now().minusDays(1))) {
         alert("Order date must be today or later");
         return;
      }
      if (lines.isEmpty()) {
         alert("Add at least one line");
         return;
      }

      // build DTO
      HashMap<Integer, Integer> map = new HashMap<>();
      lines.forEach(l -> map.put(l.getProductId(), l.getQuantity()));
      OrderInfoDTO info = (editing != null)
            ? new OrderInfoDTO(editing) // copies date & lines
            : new OrderInfoDTO(date, map);

      if (editing != null) {
         info.setOrderDate(date);
         info.setProducts(map);
         ServiceResponse<?> r = orderService.updateOrder(info);
         if (!r.isSuccess()) {
            alert(String.join("\n", r.getErrors()));
            return;
         }
      } else {
         info.setOrderDate(date);
         info.setProducts(map);
         ServiceResponse<?> r = orderService.createOrder(info);
         if (!r.isSuccess()) {
            alert(String.join("\n", r.getErrors()));
            return;
         }
      }

      close();
   }

   @FXML
   private void onCancel() {
      close();
   }

   private void alert(String msg) {
      new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
   }

   private void close() {
      ((Stage) datePicker.getScene().getWindow()).close();
   }
}