package br.ufrpe.cine_rural.gui;
import br.ufrpe.cine_rural.gui.controllers_telas.*;

import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) {
        ScreenManager.getInstance().setMainStage(primaryStage);
        ScreenManager.getInstance().showHomeScreen();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
