package PresentationLayer.GUI.MainMenuScreen.ViewModels;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MainMenuViewModel {
    private final StringProperty userDetails = new SimpleStringProperty();

    public MainMenuViewModel(String info) {
        this.userDetails.set(info);
    }

    public MainMenuViewModel() {
        // Default constructor for cases where user details are set later
        this.userDetails.set("Please log in");
    }

    public String getUserName() {
        return userDetails.get();
    }

    public StringProperty userDetailsProperty() {
        return userDetails;
    }

}