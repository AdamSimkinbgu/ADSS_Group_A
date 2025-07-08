package PresentationLayer.GUI.SupplierScreen.Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProductUIModel {
   private final StringProperty id = new SimpleStringProperty();
   private final StringProperty manufacturer = new SimpleStringProperty();
   private final StringProperty name = new SimpleStringProperty();

   public ProductUIModel(String id, String manufacturer, String name) {
      this.id.set(id);
      this.manufacturer.set(manufacturer);
      this.name.set(name);
   }

   public StringProperty idProperty() {
      return id;
   }

   public String getId() {
      return id.get();
   }

   public StringProperty manufacturerProperty() {
      return manufacturer;
   }

   public String getManufacturer() {
      return manufacturer.get();
   }

   public StringProperty nameProperty() {
      return name;
   }

   public String getName() {
      return name.get();
   }
}