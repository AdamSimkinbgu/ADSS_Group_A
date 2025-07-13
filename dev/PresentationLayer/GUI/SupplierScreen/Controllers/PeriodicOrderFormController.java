package PresentationLayer.GUI.SupplierScreen.Controllers;

import DTOs.SuppliersModuleDTOs.CatalogProductDTO;
import DTOs.SuppliersModuleDTOs.PeriodicOrderDTO;
import DomainLayer.SystemFactory;
import DomainLayer.SystemFactory.SupplierModuleComponents;
import ServiceLayer.SuppliersServiceSubModule.OrderService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;

/**
 * Dialog controller for creating/editing a PeriodicOrder.
 */
public class PeriodicOrderFormController {
   @FXML
   private ComboBox<DayOfWeek> dayCombo;
   @FXML
   private CheckBox activeCheck;
   @FXML
   private ListView<CatalogProductDTO> availList;
   @FXML
   private TableView<Line> selTable;
   @FXML
   private TableColumn<Line, Integer> pIdCol;
   @FXML
   private TableColumn<Line, String> pNameCol;
   @FXML
   private TableColumn<Line, Integer> pQtyCol;

   private OrderService service;
   private PeriodicOrderDTO editing;
   private SystemFactory systemFactory = new SystemFactory();
   SupplierModuleComponents components = systemFactory.getSupplierModule();
   private final ObservableList<CatalogProductDTO> available = FXCollections.observableArrayList();
   private final ObservableList<Line> selected = FXCollections.observableArrayList();

   public void init(OrderService svc, PeriodicOrderDTO existing) {
      this.service = components.getOrderService();
      // dayOfWeek
      dayCombo.getItems().setAll(DayOfWeek.values());
      // products
      ServiceResponse<List<CatalogProductDTO>> resp = components.getSupplierService().getAllProducts();
      if (resp.isSuccess())
         available.setAll(resp.getValue());
      availList.setItems(available);
      // selected table
      pIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().product.getProductId()).asObject());
      pNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().product.getProductName()));
      pQtyCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().quantity).asObject());
      selTable.setItems(selected);

      if (existing != null) {
         editing = existing;
         dayCombo.setValue(existing.getDeliveryDay());
         activeCheck.setSelected(existing.isActive());
         existing.getProductsInOrder().forEach((pid, q) -> {
            // find CatalogProductDTO by id:
            available.stream()
                  .filter(p -> p.getProductId() == pid)
                  .findFirst()
                  .ifPresent(p -> selected.add(new Line(p, q)));
         });
      } else {
         dayCombo.setValue(DayOfWeek.MONDAY);
         activeCheck.setSelected(true);
      }
   }

   @FXML
   private void onAdd() {
      var p = availList.getSelectionModel().getSelectedItem();
      if (p != null)
         selected.add(new Line(p, 1));
   }

   @FXML
   private void onRemove() {
      var l = selTable.getSelectionModel().getSelectedItem();
      if (l != null)
         selected.remove(l);
   }

   @FXML
   private void onSave() {
      var map = new HashMap<Integer, Integer>();
      selected.forEach(l -> map.put(l.product.getProductId(), l.quantity));
      var dto = new PeriodicOrderDTO(
            editing == null ? -1 : editing.getPeriodicOrderID(),
            dayCombo.getValue(),
            map,
            activeCheck.isSelected());
      ServiceResponse<?> r = (editing == null)
            ? service.createPeriodicOrder(dto)
            : service.updatePeriodicOrder(dto);
      if (r.isSuccess())
         close();
      else
         new Alert(Alert.AlertType.ERROR, String.join("\n", r.getErrors())).showAndWait();
   }

   @FXML
   private void onCancel() {
      close();
   }

   private void close() {
      ((Stage) dayCombo.getScene().getWindow()).close();
   }

   /** table row */
   public static class Line {
      public final CatalogProductDTO product;
      public int quantity;

      public Line(CatalogProductDTO p, int q) {
         product = p;
         quantity = q;
      }
   }
}