package PresentationLayer.GUI.SupplierScreen.Controllers;

import PresentationLayer.GUI.Common.Navigation.ScreenNavigator;
import PresentationLayer.GUI.Common.Navigation.ScreensEnum;
import PresentationLayer.GUI.SupplierScreen.ViewModels.SuppliersViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for the Suppliers screen.
 */
public class SuppliersController {
   private final SuppliersViewModel vm;

   @FXML
   private Button suppliersBtn;
   @FXML
   private Button productsBtn;
   @FXML
   private Button agreementsBtn;
   @FXML
   private Button ordersBtn;
   @FXML
   private Button backBtn;

   public SuppliersController(SuppliersViewModel vm) {
      this.vm = vm;
   }

   @FXML
   public void initialize() {
      suppliersBtn.setOnAction(e -> vm.handleSuppliers());
      productsBtn.setOnAction(e -> vm.handleProducts());
      agreementsBtn.setOnAction(e -> vm.handleAgreements());
      ordersBtn.setOnAction(e -> vm.handleOrders());
      backBtn.setOnAction(e -> ScreenNavigator.getInstance().navigateTo(ScreensEnum.MENU));
   }
}