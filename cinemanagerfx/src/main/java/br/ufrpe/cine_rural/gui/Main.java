package br.ufrpe.cine_rural.gui;

import br.ufrpe.cine_rural.gui.controllers_telas.ScreenManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

//Inicio da Main
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        ScreenManager.getInstance().setMainStage(stage);


        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/br/ufrpe/cine_rural/gui/home-view.fxml"
                )
        );

        Scene scene = new Scene(loader.load());

        stage.resizableProperty().setValue(Boolean.FALSE);
        stage.setTitle("Home");
        stage.setWidth(900);
        stage.setHeight(700);
        ScreenManager.getInstance().setMainStage(stage);

        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}



// Testes de Telas Singulares




// SEQUENCIA DE TELAS : SELECIONAR FILME E SESSÃO -> ESCOLHER ASSENTO -> PAGAMENTO
/*
import br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSalaImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSessaoImpl;
import br.ufrpe.cine_rural.gui.controllers_telas.FilmesController;
import br.ufrpe.cine_rural.negocios.FilmeNegocios;
import br.ufrpe.cine_rural.negocios.SalaNegocios;
import br.ufrpe.cine_rural.negocios.SessaoNegocios; // Importado
import br.ufrpe.cine_rural.model.tiposala.Comum;
import br.ufrpe.cine_rural.model.tiposala.Imax;
import br.ufrpe.cine_rural.model.tiposala.Vip;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        RepositorioSalaImpl salas = RepositorioSalaImpl.getInstancia();
        salas.cadastrar(new Comum(1, 20));
        salas.cadastrar(new Imax(2, 30));
        salas.cadastrar(new Vip(3, 20));


        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/br/ufrpe/cine_rural/gui/Filmes.fxml")
        );
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(
                getClass().getResource("/br/ufrpe/cine_rural/gui/EstiloFilmes.css")
                        .toExternalForm()
        );;

        primaryStage.setTitle("Cinema Rural — Filmes em Cartaz");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/
/* TELA DASHBOARD
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/br/ufrpe/cine_rural/gui/Dashboard.fxml"
                )
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Dashboard");
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






/*TELA ATENDENTE <- PRODUTO -> PAGAMENTO
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
*/

/*TELA PAGAMENTO INGRESSO
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/br/ufrpe/cine_rural/gui/TelasProduto/PagamentoIngresso.fxml"
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

//Gerenciar Filmes
/*
import br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSalaImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSessaoImpl;
import br.ufrpe.cine_rural.gui.controllers_telas.GerenciarfilmeController;
import br.ufrpe.cine_rural.gui.controllers_telas.ScreenManager;
import br.ufrpe.cine_rural.negocios.FilmeNegocios;
import br.ufrpe.cine_rural.negocios.SessaoNegocios;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/br/ufrpe/cine_rural/gui/Gerenciar filmes.fxml"
                )
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Gerenciar Filmes");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
 */


//Gerenciar Sessoes
/*
import br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSalaImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSessaoImpl;
import br.ufrpe.cine_rural.gui.controllers_telas.GerenciarSessaoController;
import br.ufrpe.cine_rural.model.tiposala.Comum;
import br.ufrpe.cine_rural.model.tiposala.Vip;
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

        salas.cadastrar(new Comum(1, 20));
        salas.cadastrar(new Imax(2, 30));
        salas.cadastrar(new Vip(3, 20));


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
/* Teste Formulario/Relatorios

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/br/ufrpe/cine_rural/gui/DashBoard.fxml"
                )
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("DashBoard");
        stage.setWidth(900);
        stage.setHeight(700);


        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/




