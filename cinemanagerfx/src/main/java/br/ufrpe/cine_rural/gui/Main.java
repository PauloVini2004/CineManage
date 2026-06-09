package br.ufrpe.cine_rural.gui;


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

        // 1. Instanciação dos Repositórios (Persistência/Dados)
        RepositorioFilmeImpl repositorioFilmes = new RepositorioFilmeImpl();
        RepositorioSalaImpl repositorioSalas = new RepositorioSalaImpl();

        // Salas pré-definidas sendo cadastradas no repositório
        repositorioSalas.cadastrar(new Comum(1, 80));   // Sala 1 → Comum
        repositorioSalas.cadastrar(new Imax(2,  60));   // Sala 2 → Imax
        repositorioSalas.cadastrar(new Vip(3,   30));   // Sala 3 → Vip

        RepositorioSessaoImpl repositorioSessoes =
                new RepositorioSessaoImpl(repositorioFilmes, repositorioSalas);

        // 2. Instanciação da Camada de Negócios (Injeção de Dependência dos Repositórios)
        SalaNegocios salaNegocios = new SalaNegocios(repositorioSalas);
        SessaoNegocios sessaoNegocios = new SessaoNegocios(repositorioSessoes);
        FilmeNegocios filmeNegocios = new FilmeNegocios(repositorioFilmes, sessaoNegocios);

        // 3. Carrega a interface gráfica (Fxml) da tela de Filmes
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/br/ufrpe/cine_rural/gui/Filmes.fxml")
        );
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(
                getClass().getResource("/br/ufrpe/cine_rural/gui/EstiloFilmes.css")
                        .toExternalForm()
        );

        // 4. Injeta as instâncias de NEGÓCIOS no controller em vez dos repositórios
        FilmesController filmesController = loader.getController();
        filmesController.setNegocios(filmeNegocios, sessaoNegocios);

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

//Gerenciar Sessoes

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

        RepositorioFilmeImpl filmes = new RepositorioFilmeImpl();
        RepositorioSalaImpl salas = new RepositorioSalaImpl();

        salas.cadastrar(new Comum(1, 20));
        salas.cadastrar(new Imax(2, 30));
        salas.cadastrar(new Vip(3, 20));

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

/* Inicio da Main
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/br/ufrpe/cine_rural/gui/Filmes.fxml"
                )
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Home");
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
