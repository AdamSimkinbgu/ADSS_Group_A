package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.OrderDTO;
import ServiceLayer.SuppliersServiceSubModule.OrderService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrdersController {
   @FXML
   private TableView<OrderDTO> ordersTable;
   @FXML
   private TableColumn<OrderDTO, Integer> orderIdCol;
   @FXML
   private TableColumn<OrderDTO, Integer> supplierIdCol;
   @FXML
   private TableColumn<OrderDTO, String> dateCol;
   @FXML
   private TableColumn<OrderDTO, String> statusCol;
   @FXML
   private TableColumn<OrderDTO, Void> actionsCol;
   @FXML
   private Button newOrderBtn;
   @FXML
   private Button refreshBtn;
   @FXML
   private TextField searchField;

   private ObservableList<OrderDTO> allOrders = FXCollections.observableArrayList();
   private OrderService orderService;
   private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

   public void setOrderService(OrderService service) {
      this.orderService = service;
   }

   @FXML
   public void initialize() {
      // column value factories
      orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
      supplierIdCol.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
      dateCol.setCellValueFactory(o -> new SimpleStringProperty(o.getValue().getOrderDate().format(df)));
      statusCol.setCellValueFactory(o -> new SimpleStringProperty(o.getValue().getStatus().name()));

      // action buttons
      actionsCol.setCellFactory(_ -> new TableCell<>() {
         private final Button viewBtn = new Button("View");
         private final Button editBtn = new Button("Edit");
         private final Button deleteBtn = new Button("Delete");
         {
            viewBtn.setOnAction(_ -> onView(getIndex()));
            editBtn.setOnAction(_ -> onEdit(getIndex()));
            deleteBtn.setOnAction(_ -> onDelete(getIndex()));
            viewBtn.getStyleClass().add("action-button");
            editBtn.getStyleClass().add("secondary-button");
            deleteBtn.getStyleClass().add("secondary-button");
         }

         @Override
         protected void updateItem(Void v, boolean empty) {
            super.updateItem(v, empty);
            if (empty) {
               setGraphic(null);
            } else {
               HBox container = new HBox(5, viewBtn, editBtn, deleteBtn);
               setGraphic(container);
            }
         }
      });

      loadOrders();
   }

   private void loadOrders() {
      ServiceResponse<List<OrderDTO>> resp = orderService.getAllOrders();
      if (resp.isSuccess()) {
         allOrders.setAll(resp.getValue());
         ordersTable.setItems(allOrders);
      } else {
         new Alert(Alert.AlertType.ERROR, String.join("\n", resp.getErrors()))
               .showAndWait();
      }
   }

   @FXML
   private void onNewOrder() {
      // TODO: open NewOrder dialog / screen
      // TODO: allow periodic orders
      System.out.println("New order dialog requested");
   }

   private void onView(int index) {
      OrderDTO order = ordersTable.getItems().get(index);
      // TODO: open OrderDetailsController with order
      System.out.println("View order: " + order.getOrderId());
   }

   private void onEdit(int index) {
      OrderDTO order = ordersTable.getItems().get(index);
      // TODO: open EditOrder dialog / screen
      System.out.println("Edit order: " + order.getOrderId());
   }

   @FXML
   private void onRefresh() {
      loadOrders();
   }

   @FXML
   private void onSearch(KeyEvent event) {
      String filter = searchField.getText().toLowerCase().trim();
      // assume you loaded all orders into an ObservableList<OrderDTO> allOrders
      ordersTable.setItems(
            allOrders.filtered(o -> Integer.toString(o.getOrderId()).contains(filter)
                  || o.getSupplierName().toLowerCase().contains(filter)));
   }

   private void onDelete(int index) {
      OrderDTO order = ordersTable.getItems().get(index);
      Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete order #" + order.getOrderId() + "?", ButtonType.YES, ButtonType.NO);
      confirm.showAndWait().ifPresent(bt -> {
         if (bt == ButtonType.YES) {
            ServiceResponse<?> resp = orderService.removeOrder(order.getOrderId());
            if (resp.isSuccess()) {
               loadOrders();
            } else {
               new Alert(Alert.AlertType.ERROR, String.join("\n", resp.getErrors()))
                     .showAndWait();
            }
         }
      });
   }
}