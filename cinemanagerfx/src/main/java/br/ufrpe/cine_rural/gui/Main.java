package br.ufrpe.cine_rural.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader1 = new FXMLLoader(
                Main.class.getResource("/br/ufrpe/cine_rural/gui/Assentos.fxml")
        );

        FXMLLoader loader2 = new FXMLLoader(
                Main.class.getResource("/br/ufrpe/cine_rural/gui/Filmes.fxml")
        );

        FXMLLoader loader3 = new FXMLLoader(
                Main.class.getResource("/br/ufrpe/cine_rural/gui/EmissaoIngresso.fxml")
        );

        Scene sceneAssentos = new Scene(loader1.load());
        sceneAssentos.getStylesheets().add(
                Main.class.getResource("/br/ufrpe/cine_rural/gui/EstiloAssentos.css")
                        .toExternalForm()
        );

        Scene sceneFilmes = new Scene(loader2.load());
        sceneFilmes.getStylesheets().add(
                Main.class.getResource("/br/ufrpe/cine_rural/gui/EstiloFilmes.css")
                        .toExternalForm()
        );

        Scene sceneIngresso = new Scene(loader3.load());
        sceneIngresso.getStylesheets().add(
                Main.class.getResource("/br/ufrpe/cine_rural/gui/EstiloIngresso.css")
                        .toExternalForm()
        );

        Stage stage1 = new Stage();
        stage1.setTitle("Filmes");
        stage1.setScene(sceneFilmes);
        stage1.setResizable(false);

        stage1.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

 /*
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource(
                        "/br/ufrpe/cine_rural/gui/Produto.fxml"
                )
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Teste Produto");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} */










