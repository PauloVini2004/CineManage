package br.ufrpe.cine_rural.gui;

/* SEQUENCIA DE TELAS : SELECIONAR FILME E SESSÃO -> ESCOLHER ASSENTO -> EMISSÃO INGRESSO
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
*/





/*  TELA ATENDENTE <- PRODUTO -> DADOS CLIENTE (avançar não funciona. Ainda não foi fornecida a tela dados cliente no main para a conexão)
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/ufrpe/cine_rural/gui/Produto.fxml"));
            Parent root = loader.load();

            primaryStage.setTitle("Produto");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/







/* (voltar vai para home por enquanto)TELA GERENTE <- TELA LISTARPRODUTOS -> TELA ADICIONARPRODUTO E TELAEDITARPRODUTO(editar vai para produto por enquanto)
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/br/ufrpe/cine_rural/gui/ListarProdutos.fxml"
                )
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Listar Produto");
        stage.setWidth(900);
        stage.setHeight(700);
        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/





