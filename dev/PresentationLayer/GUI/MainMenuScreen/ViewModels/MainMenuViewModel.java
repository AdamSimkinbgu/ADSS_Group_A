package PresentationLayer.GUI.MainMenuScreen.ViewModels;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class MainMenuViewModel {
    private final IntegerProperty userId = new SimpleIntegerProperty();

    public MainMenuViewModel(int userId) {
        this.userId.set(userId);
    }

    public int getUserId() {
        return userId.get();
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }
}