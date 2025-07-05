package PresentationLayer.GUI.MainMenuScreen.ViewModels;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MainMenuViewModel {
    private final StringProperty userDetails = new SimpleStringProperty();

    public MainMenuViewModel(String info) {
        this.userDetails.set(info);
    }

    public String getUserName() {
        return userDetails.get();
    }

    public StringProperty userDetailsProperty() {
        return userDetails;
    }

}