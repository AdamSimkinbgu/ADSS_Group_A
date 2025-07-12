// OrdersController.java

package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.OrderDTO;
import DomainLayer.SystemFactory;
import DomainLayer.SystemFactory.SupplierModuleComponents;
import ServiceLayer.SuppliersServiceSubModule.OrderService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class OrdersController {

   @FXML
   private TableView<OrderDTO> ordersTable;
   @FXML
   private TableColumn<OrderDTO, Integer> colId;
   @FXML
   private TableColumn<OrderDTO, String> colSupplier;
   @FXML
   private TableColumn<OrderDTO, String> colDate;
   @FXML
   private TableColumn<OrderDTO, String> colStatus;
   @FXML
   private TableColumn<OrderDTO, Integer> colItemCount;

   private final SystemFactory factory = new SystemFactory();
   private final SupplierModuleComponents supplierComponents = factory.getSupplierModule();
   private final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

   @FXML
   public void initialize() {
      // colId.setCellValueFactory(p -> new
      // IntegerProperty(p.getValue().getOrderId()).as);
      // colSupplier.setCellValueFactory(p -> p.getValue().supplierNameProperty());
      // colDate.setCellValueFactory(p -> p.getValue().orderDateProperty().map(d ->
      // d.format(DATE_FMT)));
      // colStatus.setCellValueFactory(p -> p.getValue().statusProperty().asString());
      // colItemCount.setCellValueFactory(p ->
      // p.getValue().totalItemsProperty().asObject());

      // loadOrders();
   }

   // private void loadOrders() {
   // ServiceResponse<List<OrderDTO>> resp = orderService.getAllOrders();
   // if (resp.isSuccess()) {
   // ObservableList<OrderDTO> list =
   // FXCollections.observableArrayList(resp.getData());
   // ordersTable.setItems(list);
   // } else {
   // showAlert(Alert.AlertType.ERROR, "Load Failed", String.join("\n",
   // resp.getErrors()));
   // }
   // }

   // @FXML
   // private void onRefresh() {
   // loadOrders();
   // }

   // @FXML
   // private void onNewOrder() {
   // // TODO: open a dialog or new pane to collect OrderInfoDTO, then:
   // // ServiceResponse<?> r = orderService.createOrder(infoDTO);
   // // if success reload; else showAlert(...)
   // showAlert(Alert.AlertType.INFORMATION, "Not implemented", "Create-order flow
   // here");
   // }

   // @FXML
   // private void onEditOrder() {
   // OrderDTO sel = ordersTable.getSelectionModel().getSelectedItem();
   // if (sel == null) {
   // showAlert(Alert.AlertType.WARNING, "No selection", "Please select an order
   // first.");
   // return;
   // }
   // // TODO: open edit dialog prefilled with sel; then call
   // // orderService.updateOrder(...)
   // showAlert(Alert.AlertType.INFORMATION, "Not implemented", "Edit-order flow
   // here");
   // }

   // @FXML
   // private void onDeleteOrder() {
   // OrderDTO sel = ordersTable.getSelectionModel().getSelectedItem();
   // if (sel == null) {
   // showAlert(Alert.AlertType.WARNING, "No selection", "Please select an order
   // first.");
   // return;
   // }
   // Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
   // "Are you sure you want to delete order #" + sel.getOrderId() + "?",
   // ButtonType.YES, ButtonType.NO);
   // Optional<ButtonType> choice = confirm.showAndWait();
   // if (choice.orElse(ButtonType.NO) == ButtonType.YES) {
   // ServiceResponse<Boolean> resp = orderService.removeOrder(sel.getOrderId());
   // if (resp.isSuccess() && resp.getValue()) {
   // loadOrders();
   // } else {
   // showAlert(Alert.AlertType.ERROR, "Delete failed", String.join("\n",
   // resp.getErrors()));
   // }
   // }
   // }

   private void showAlert(Alert.AlertType type, String title, String msg) {
      Alert a = new Alert(type, msg, ButtonType.OK);
      a.initOwner(ordersTable.getScene().getWindow());
      a.setHeaderText(title);
      a.showAndWait();
   }
}