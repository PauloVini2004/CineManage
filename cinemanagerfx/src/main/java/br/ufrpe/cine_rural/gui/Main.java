package br.ufrpe.cine_rural.gui;

/* SEQUENCIA DE TELAS : SELECIONAR FILME E SESSÃO -> ESCOLHER ASSENTO -> PAGAMENTO
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
                Main.class.getResource("/br/ufrpe/cine_rural/gui/Emergencia/PagamentoIngresso.fxml")
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

        Scene scenePagamento = new Scene(loader3.load());
        scenePagamento.getStylesheets().add(
                Main.class.getResource("/br/ufrpe/cine_rural/gui/Emergencia/EstiloPagamentoIngresso.css")
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



//TELA ATENDENTE <- PRODUTO -> DADOS CLIENTE (avançar não funciona. Ainda não foi fornecida a tela dados cliente no main para a conexão)
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/ufrpe/cine_rural/gui/TelasProduto/Produto.fxml"));
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



/*TELA GERENTE <- TELA LISTARPRODUTOS -> TELA ADICIONARPRODUTO E TELAEDITARPRODUTO
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/br/ufrpe/cine_rural/gui/TelasProduto/ListarProdutos.fxml"
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


// Gerenciar Filmes
/*
import br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSalaImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSessaoImpl;
import br.ufrpe.cine_rural.gui.controllers_telas.GerenciarfilmeController;
import br.ufrpe.cine_rural.negocios.FilmeNegocios;
import br.ufrpe.cine_rural.negocios.SessaoNegocios;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        RepositorioFilmeImpl filmes = new RepositorioFilmeImpl();
        RepositorioSalaImpl salas = new RepositorioSalaImpl();

        RepositorioSessaoImpl sessoes =
                new RepositorioSessaoImpl(filmes, salas);

        SessaoNegocios sessaoNegocios =
                new SessaoNegocios(sessoes);

        FilmeNegocios filmeNegocios =
                new FilmeNegocios(filmes, sessaoNegocios);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/br/ufrpe/cine_rural/gui/Gerenciar filmes.fxml"
                )
        );

        Scene scene = new Scene(loader.load());

        GerenciarfilmeController controller =
                loader.getController();

        controller.setNegocios(filmeNegocios);

        stage.setTitle("Gerenciar Filmes");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/


/* Gerenciar Sessoes

import br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSalaImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSessaoImpl;
import br.ufrpe.cine_rural.gui.controllers_telas.GerenciarSessaoController;
import br.ufrpe.cine_rural.model.tiposala.Comum;
import br.ufrpe.cine_rural.model.tiposala.Imax;
import br.ufrpe.cine_rural.negocios.FilmeNegocios;
import br.ufrpe.cine_rural.negocios.SalaNegocios;
import br.ufrpe.cine_rural.negocios.SessaoNegocios;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        RepositorioFilmeImpl filmes = new RepositorioFilmeImpl();
        RepositorioSalaImpl salas = new RepositorioSalaImpl();

        salas.cadastrar(new Comum(1, 20));
        salas.cadastrar(new Imax(2, 30));

        RepositorioSessaoImpl sessoes =
                new RepositorioSessaoImpl(filmes, salas);

        SessaoNegocios sessaoNegocios =
                new SessaoNegocios(sessoes);

        FilmeNegocios filmeNegocios =
                new FilmeNegocios(filmes, sessaoNegocios);

        SalaNegocios salaNegocios =
                new SalaNegocios(salas);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/br/ufrpe/cine_rural/gui/Gerenciar sessoes.fxml"
                )
        );

        Scene scene = new Scene(loader.load());

        GerenciarSessaoController controller =
                loader.getController();

        controller.setRepositorios(
                filmeNegocios,
                sessaoNegocios,
                salaNegocios
        );

        stage.setTitle("Gerenciar Sessões");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/