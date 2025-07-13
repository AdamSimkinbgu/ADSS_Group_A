package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.OrderDTO;
import DTOs.SuppliersModuleDTOs.PeriodicOrderDTO;
import DTOs.SuppliersModuleDTOs.OrderResultDTO;
import ServiceLayer.SuppliersServiceSubModule.OrderService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Master controller for the combined Orders / Periodic Orders tab.
 */
public class OrdersViewController {
   // ==== ORDERS UI ====
   @FXML
   private Button newOrderBtn, refreshOrdersBtn;
   @FXML
   private TextField orderSearchField;
   @FXML
   private TableView<OrderDTO> ordersTable;
   @FXML
   private TableColumn<OrderDTO, Integer> orderIdCol, supplierIdCol;
   @FXML
   private TableColumn<OrderDTO, String> dateCol, statusCol;
   @FXML
   private TableColumn<OrderDTO, Void> orderActionsCol;

   // ==== PERIODIC ORDERS UI ====
   @FXML
   private Button newPeriodicBtn, refreshPeriodicBtn, executeTodayBtn, executeWeekBtn;
   @FXML
   private TableView<PeriodicOrderDTO> periodicTable;
   @FXML
   private TableColumn<PeriodicOrderDTO, Integer> periodicIdCol;
   @FXML
   private TableColumn<PeriodicOrderDTO, DayOfWeek> deliveryDayCol;
   @FXML
   private TableColumn<PeriodicOrderDTO, Boolean> activeCol;
   @FXML
   private TableColumn<PeriodicOrderDTO, Void> periodicActionsCol;

   private OrderService orderService;

   private final ObservableList<OrderDTO> ordersList = FXCollections.observableArrayList();
   private final ObservableList<PeriodicOrderDTO> periodicList = FXCollections.observableArrayList();
   private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

   @FXML
   private SplitPane splitPane;

   /** Inject the service and kick off initial load. */
   public void setOrderService(OrderService svc) {
      this.orderService = svc;
      loadOrders();
      loadPeriodic();
   }

   @FXML
   private void initialize() {
      // — Orders table setup —
      orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
      supplierIdCol.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
      dateCol.setCellValueFactory(o -> new SimpleStringProperty(o.getValue().getOrderDate().format(df)));
      statusCol.setCellValueFactory(o -> new SimpleStringProperty(o.getValue().getStatus().name()));
      orderActionsCol.setCellFactory(tc -> new TableCell<>() {
         private final Button view = new Button("View");
         private final Button edit = new Button("Edit");
         private final Button del = new Button("Delete");
         {
            view.getStyleClass().add("action-button");
            edit.getStyleClass().add("secondary-button");
            del.getStyleClass().add("secondary-button");
            view.setOnAction(e -> openOrderDialog(getIndex(), false, "view"));
            edit.setOnAction(e -> openOrderDialog(getIndex(), false, "edit"));
            del.setOnAction(e -> deleteOrder(getIndex()));
         }

         @Override
         protected void updateItem(Void v, boolean empty) {
            super.updateItem(v, empty);
            setGraphic(empty ? null : new HBox(5, view, edit, del));
         }
      });
      ordersTable.setItems(ordersList);

      // — Periodic orders table setup —
      periodicIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPeriodicOrderID()).asObject());
      deliveryDayCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDeliveryDay()));
      activeCol.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isActive()));
      periodicActionsCol.setCellFactory(tc -> new TableCell<>() {
         private final Button edit = new Button("Edit");
         private final Button del = new Button("Delete");
         private final Button exec = new Button("Run");
         {
            edit.getStyleClass().add("secondary-button");
            del.getStyleClass().add("secondary-button");
            exec.getStyleClass().add("action-button");
            edit.setOnAction(e -> openPeriodicDialog(getIndex(), false));
            del.setOnAction(e -> deletePeriodic(getIndex()));
            exec.setOnAction(e -> executeOnePeriodic(getIndex()));
         }

         @Override
         protected void updateItem(Void v, boolean empty) {
            super.updateItem(v, empty);
            if (empty) {
               setGraphic(null);
            } else {
               setGraphic(new HBox(5, edit, del, exec));
            }
         }
      });
      periodicTable.setItems(periodicList);
      splitPane.lookupAll(".split-pane-divider")
            .forEach(div -> {
               div.setMouseTransparent(true);
            });
   }

   // ==== ORDERS HANDLERS ====

   @FXML
   private void onNewOrder() {
      openOrderDialog(-1, true, "create");
   }

   @FXML
   private void onRefreshOrders() {
      loadOrders();
   }

   @FXML
   private void onSearchOrders() {
      String f = orderSearchField.getText().toLowerCase().trim();
      ordersTable.setItems(ordersList.filtered(o -> Integer.toString(o.getOrderId()).contains(f) ||
            o.getSupplierName().toLowerCase().contains(f)));
   }

   private void loadOrders() {
      if (orderService == null)
         return;
      ServiceResponse<List<OrderDTO>> resp = orderService.getAllOrders();
      if (resp.isSuccess()) {
         ordersList.setAll(resp.getValue());
      } else if (!resp.getErrors().isEmpty() && resp.getErrors().get(0).equals("No orders found.")) {
         ordersList.clear();
      } else {
         new Alert(Alert.AlertType.ERROR, String.join("\n", resp.getErrors())).showAndWait();
      }
   }

   private void openOrderDialog(int idx, boolean creating, String mode) {
      try {
         var fxml = getClass().getResource("/GUI/SupplierScreen/Views/OrderFormView.fxml");
         FXMLLoader loader = new FXMLLoader(fxml);
         Parent root = loader.load();
         OrderFormController ctrl = loader.getController();
         ctrl.init(creating ? null : ordersList.get(idx), mode);
         Stage s = new Stage();
         s.initModality(Modality.APPLICATION_MODAL);
         s.setScene(new Scene(root));
         s.setTitle(creating ? "New Order" : "Order ― " + ordersList.get(idx).getOrderId());
         s.showAndWait();
         loadOrders();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   private void deleteOrder(int idx) {
      var o = ordersList.get(idx);
      var A = new Alert(Alert.AlertType.CONFIRMATION, "Delete Order #" + o.getOrderId() + "?", ButtonType.YES,
            ButtonType.NO);
      A.showAndWait().filter(bt -> bt == ButtonType.YES).ifPresent(bt -> {
         var r = orderService.removeOrder(o.getOrderId());
         if (r.isSuccess())
            loadOrders();
         else
            new Alert(Alert.AlertType.ERROR, String.join("\n", r.getErrors())).showAndWait();
      });
   }

   // ==== PERIODIC ORDERS HANDLERS ====

   @FXML
   private void onNewPeriodic() {
      openPeriodicDialog(-1, true);
   }

   @FXML
   private void onRefreshPeriodic() {
      loadPeriodic();
   }

   @FXML
   private void onExecuteToday() {
      var resp = orderService.getAllPeriodicOrdersForToday();
      if (resp.isSuccess()) {
         StringBuilder sb = new StringBuilder();
         for (OrderDTO o : resp.getValue())
            sb.append(o).append("\n");
         new Alert(Alert.AlertType.INFORMATION, sb.toString()).showAndWait();
      } else {
         new Alert(Alert.AlertType.ERROR, String.join("\n", resp.getErrors())).showAndWait();
      }
   }

   @FXML
   private void onExecuteWeek() {
      var resp = orderService.executePeriodicOrdersForThisWeek();
      if (resp.isSuccess())
         new Alert(Alert.AlertType.INFORMATION, resp.getValue().toString()).showAndWait();
      else
         new Alert(Alert.AlertType.ERROR, String.join("\n", resp.getErrors())).showAndWait();
   }

   private void loadPeriodic() {
      if (orderService == null)
         return;
      var resp = orderService.getAllPeriodicOrders();
      if (resp.isSuccess())
         periodicList.setAll(resp.getValue());
      else
         new Alert(Alert.AlertType.ERROR, String.join("\n", resp.getErrors())).showAndWait();
   }

   private void openPeriodicDialog(int idx, boolean creating) {
      try {
         var fxml = getClass().getResource("/GUI/SupplierScreen/Views/PeriodicOrderForm.fxml");
         FXMLLoader loader = new FXMLLoader(fxml);
         Parent root = loader.load();
         PeriodicOrderFormController ctrl = loader.getController();
         ctrl.init(orderService, creating ? null : periodicList.get(idx));
         Stage s = new Stage();
         s.initModality(Modality.APPLICATION_MODAL);
         s.setScene(new Scene(root));
         s.setTitle(creating ? "New Periodic Order" : "Periodic #" + periodicList.get(idx).getPeriodicOrderID());
         s.showAndWait();
         loadPeriodic();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   private void deletePeriodic(int idx) {
      var p = periodicList.get(idx);
      var A = new Alert(Alert.AlertType.CONFIRMATION, "Delete Periodic #" + p.getPeriodicOrderID() + "?",
            ButtonType.YES, ButtonType.NO);
      A.showAndWait().filter(bt -> bt == ButtonType.YES).ifPresent(bt -> {
         var r = orderService.removePeriodicOrder(p.getPeriodicOrderID());
         if (r.isSuccess())
            loadPeriodic();
         else
            new Alert(Alert.AlertType.ERROR, String.join("\n", r.getErrors())).showAndWait();
      });
   }

   private void executeOnePeriodic(int idx) {
      var p = periodicList.get(idx);
      var resp = orderService.executePeriodicOrdersForDay(p.getDeliveryDay().name());
      if (resp.isSuccess()) {
         StringBuilder sb = new StringBuilder();
         for (OrderResultDTO r : resp.getValue())
            sb.append(r).append("\n");
         new Alert(Alert.AlertType.INFORMATION, sb.toString()).showAndWait();
      } else {
         new Alert(Alert.AlertType.ERROR, String.join("\n", resp.getErrors())).showAndWait();
      }
   }
}