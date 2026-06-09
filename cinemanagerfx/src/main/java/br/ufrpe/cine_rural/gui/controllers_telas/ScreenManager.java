package br.ufrpe.cine_rural.gui.controllers_telas;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ScreenManager {

    private static ScreenManager instance;
    private Stage mainStage;

    //listagem das telas principais
    private Scene homeScene;
    private Scene atendenteScene;
    private Scene gerenteScene;
    private Scene filmesScene;
    private Scene assentosScene;
    private Scene pagamentoIngressoScene;


    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }

        return instance;
    }

    private ScreenManager() {
        // Construtor privado para evitar instanciação

        try {
            BorderPane homePane = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/home-view.fxml"));
            // inicializando cena home
            this.homeScene = new Scene(homePane);

            BorderPane atendentePane = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/Atendente-View.fxml"));
            // inicializando cena atendente
            this.atendenteScene = new Scene(atendentePane);

            BorderPane gerentePane = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/Gerente-View.fxml"));
            // inicializando cena gerente
            this.gerenteScene = new Scene(gerentePane);

            /*
            BorderPane filmePane = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/Filme.fxml"));
            // inicializando cena gerente
            this.filmesScene = new Scene(filmePane);
            */

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;

        // configurando largura e altura do stage.
        mainStage.setWidth(1024);
        mainStage.setHeight(768);

        // configurando título da app
        mainStage.setTitle("Cine Manager");
    }

    public void showHomeScreen() {
        this.mainStage.setScene(this.homeScene);
        this.mainStage.show();
    }

    public void showAtendenteScreen() {
        this.mainStage.setScene(this.atendenteScene);
        this.mainStage.show();
    }

    public void showGerenteScreen() {
        this.mainStage.setScene(this.gerenteScene);
        this.mainStage.show();
    }

    public void showFilmesScreen() {
        this.mainStage.setScene(this.filmesScene);
        this.mainStage.show();
    }

    public void showAssentosScreen() {
        this.mainStage.setScene(this.assentosScene);
        this.mainStage.show();
    }

    public void showPagamentoIngressoScreen() {
        this.mainStage.setScene(this.pagamentoIngressoScene);
        this.mainStage.show();
    }
}
