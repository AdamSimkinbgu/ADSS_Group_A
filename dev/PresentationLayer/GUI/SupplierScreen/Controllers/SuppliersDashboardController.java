package PresentationLayer.GUI.SupplierScreen.Controllers;

import PresentationLayer.GUI.SupplierScreen.Models.ProductUIModel;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.OrderService;
import ServiceLayer.SuppliersServiceSubModule.AgreementService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import DTOs.SuppliersModuleDTOs.CatalogProductDTO;
import DTOs.SuppliersModuleDTOs.SupplierDTO;
import DTOs.SuppliersModuleDTOs.OrderDTO;
import DTOs.SuppliersModuleDTOs.AgreementDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class SuppliersDashboardController {
   // stat‐card labels
   @FXML
   private Label suppliersCountLabel;
   @FXML
   private Label productsCountLabel;
   @FXML
   private Label ordersCountLabel;
   @FXML
   private Label agreementsCountLabel;

   // catalog
   @FXML
   private TextField searchField;
   @FXML
   private TableView<ProductUIModel> catalogTable;
   @FXML
   private TableColumn<ProductUIModel, String> idColumn;
   @FXML
   private TableColumn<ProductUIModel, String> manufacturerColumn;
   @FXML
   private TableColumn<ProductUIModel, String> nameColumn;

   private SupplierService supplierService;
   private OrderService orderService;
   private AgreementService agreementService;

   private final ObservableList<ProductUIModel> masterData = FXCollections.observableArrayList();

   /**
    * Call these right after loading:
    * ctrl.setSupplierService(s);
    * ctrl.setOrderService(o);
    * ctrl.setAgreementService(a);
    */
   public void setSupplierService(SupplierService s) {
      this.supplierService = s;
   }

   public void setOrderService(OrderService o) {
      this.orderService = o;
   }

   public void setAgreementService(AgreementService a) {
      this.agreementService = a;
   }

   @FXML
   public void initialize() {
      // 1) wire columns
      idColumn.setCellValueFactory(c -> c.getValue().idProperty());
      manufacturerColumn.setCellValueFactory(c -> c.getValue().manufacturerProperty());
      nameColumn.setCellValueFactory(c -> c.getValue().nameProperty());

      // 2) searchable
      FilteredList<ProductUIModel> filtered = new FilteredList<>(masterData, _ -> true);
      searchField.textProperty().addListener((_, _, nw) -> {
         String term = (nw == null ? "" : nw).toLowerCase();
         filtered.setPredicate(prod -> prod.getId().toLowerCase().contains(term) ||
               prod.getManufacturer().toLowerCase().contains(term) ||
               prod.getName().toLowerCase().contains(term));
      });
      catalogTable.setItems(filtered);

      // 3) load stats + catalog
      loadStats();
      loadCatalog();
   }

   private void loadStats() {
      // suppliers
      ServiceResponse<List<SupplierDTO>> supResp = supplierService.getAllSuppliers();
      if (supResp.isSuccess()) {
         List<SupplierDTO> all = supResp.getValue();
         suppliersCountLabel.setText(String.valueOf(all.size()));

         // agreements: sum agreements per supplier
         int totalAgreements = all.stream()
               .mapToInt(s -> {
                  ServiceResponse<List<AgreementDTO>> a = agreementService.getAgreementsBySupplierId(s.getId());
                  return a.isSuccess() ? a.getValue().size() : 0;
               })
               .sum();
         agreementsCountLabel.setText(String.valueOf(totalAgreements));
      } else {
         suppliersCountLabel.setText("—");
         agreementsCountLabel.setText("—");
      }

      // products
      ServiceResponse<List<CatalogProductDTO>> prodResp = supplierService.getAllProducts();
      if (prodResp.isSuccess()) {
         productsCountLabel.setText(String.valueOf(prodResp.getValue().size()));
      } else {
         productsCountLabel.setText("—");
      }

      // orders
      ServiceResponse<List<OrderDTO>> ordResp = orderService.getAllOrders();
      if (ordResp.isSuccess()) {
         ordersCountLabel.setText(String.valueOf(ordResp.getValue().size()));
      } else {
         ordersCountLabel.setText("—");
      }
   }

   private void loadCatalog() {
      ServiceResponse<List<CatalogProductDTO>> resp = supplierService.getAllProducts();
      if (!resp.isSuccess())
         return;
      masterData.clear();
      resp.getValue().forEach(dto -> masterData.add(new ProductUIModel(
            String.valueOf(dto.getProductId()),
            dto.getManufacturerName(),
            dto.getProductName())));
   }
}