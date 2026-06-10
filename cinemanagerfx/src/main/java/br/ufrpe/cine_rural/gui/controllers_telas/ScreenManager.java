package br.ufrpe.cine_rural.gui.controllers_telas;

import java.io.IOException;

import br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSalaImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSessaoImpl;
import br.ufrpe.cine_rural.model.tiposala.Comum;
import br.ufrpe.cine_rural.model.tiposala.Imax;
import br.ufrpe.cine_rural.model.tiposala.Vip;
import br.ufrpe.cine_rural.negocios.FilmeNegocios;
import br.ufrpe.cine_rural.negocios.SalaNegocios;
import br.ufrpe.cine_rural.negocios.SessaoNegocios;
import br.ufrpe.cine_rural.gui.controllers_telas.GerenciarfilmeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ScreenManager {

    private static ScreenManager instance;
    private static RepositorioSalaImpl salas;
    private static RepositorioFilmeImpl filmes;
    private Stage mainStage;

    //listagem das telas principais
    private Scene homeScene;
    private Scene atendenteScene;
    private Scene gerenteScene;
    private Scene filmesScene;
    private Scene produtosScene;
    private Scene listarProdutosScene;
    private Scene gerenciarFilmeScene;
    private Scene gerenciarSessoesScene;
    private Scene relatorioScene;
    private Scene ingressosScene;

    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    public static RepositorioFilmeImpl getFilmes() {
        if (filmes == null) {
            filmes = new RepositorioFilmeImpl();
        }
        return filmes;
    }

    public static RepositorioSalaImpl getSalas() {
        if (salas == null) {
            salas = new RepositorioSalaImpl();
        }
        return salas;
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

            AnchorPane produtosPane = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/TelasProduto/Produto.fxml"));
            // inicializando cena produtos
            this.produtosScene = new Scene(produtosPane);

            BorderPane listarProdutosPane = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/TelasProduto/ListarProdutos.fxml"));
            // inicializando cena lista produtos
            this.listarProdutosScene = new Scene(listarProdutosPane);

            //inicializando a tela gerenciar filmes
            RepositorioSessaoImpl sessoes = new RepositorioSessaoImpl(ScreenManager.getFilmes(), ScreenManager.getSalas());
            SessaoNegocios sessaoNegocios = new SessaoNegocios(sessoes);
            FilmeNegocios filmeNegocios = new FilmeNegocios(ScreenManager.getFilmes(), sessaoNegocios);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                            "/br/ufrpe/cine_rural/gui/Gerenciar filmes.fxml"));

            this.gerenciarFilmeScene = new Scene(loader.load());

            GerenciarfilmeController controller = loader.getController();
            controller.setNegocios(filmeNegocios);

            //iniciando gerenciar sessoes
                salas.cadastrar(new Comum(1, 20));
                salas.cadastrar(new Imax(2, 30));
                salas.cadastrar(new Vip(3, 20));

                RepositorioSessaoImpl sessoes2 = new RepositorioSessaoImpl(ScreenManager.getFilmes(), ScreenManager.getSalas());
                SessaoNegocios sessaoNegocios2 = new SessaoNegocios(sessoes2);
                FilmeNegocios filmeNegocios2 = new FilmeNegocios(ScreenManager.getFilmes(), sessaoNegocios2);
                SalaNegocios salaNegocios = new SalaNegocios(ScreenManager.getSalas());

                FXMLLoader loader2 = new FXMLLoader(getClass().getResource(
                        "/br/ufrpe/cine_rural/gui/Gerenciar sessoes.fxml"));
                this.gerenciarSessoesScene = new Scene(loader2.load());

                GerenciarSessaoController controller2 = loader2.getController();

                controller2.setRepositorios(
                        filmeNegocios2,
                        sessaoNegocios2,
                        salaNegocios
                );

            BorderPane relatorioPane = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/DashBoard.fxml"));
            // inicializando relatorio
            this.relatorioScene= new Scene(relatorioPane);

            //Cena filmes
            salas.cadastrar(new Comum(1, 80));   // Sala 1 → Comum
            salas.cadastrar(new Imax(2,  60));   // Sala 2 → Imax
            salas.cadastrar(new Vip(3,   30));   // Sala 3 → Vip

            RepositorioSessaoImpl repositorioSessoes = new RepositorioSessaoImpl(ScreenManager.getFilmes(), ScreenManager.getSalas());

            SalaNegocios salaNegociosFilme = new SalaNegocios(ScreenManager.getSalas());
            SessaoNegocios sessaoNegociosFilme = new SessaoNegocios(repositorioSessoes);
            FilmeNegocios filmeNegociosFilme = new FilmeNegocios(ScreenManager.getFilmes(), sessaoNegociosFilme);

            // 3. Carrega a interface gráfica (Fxml) da tela de Filmes
            FXMLLoader filmes = new FXMLLoader(
                    getClass().getResource("/br/ufrpe/cine_rural/gui/Filmes.fxml")
            );
            this.filmesScene = new Scene(filmes.load());
            filmesScene.getStylesheets().add(
                    getClass().getResource("/br/ufrpe/cine_rural/gui/EstiloFilmes.css")
                            .toExternalForm()
            );

            // 4. Injeta as instâncias de NEGÓCIOS no controller em vez dos repositórios
            FilmesController filmesController = filmes.getController();
            filmesController.setNegocios(filmeNegociosFilme, sessaoNegociosFilme);

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

    public void showProdutosScreen() {
        this.mainStage.setScene(this.produtosScene);
        this.mainStage.show();
        System.out.println(
                getClass().getResource("/br/ufrpe/cine_rural/gui/TelasProduto/EstiloProduto.css")
        );
    }

    public void showListarProdutosScreen() {
        this.mainStage.setScene(this.listarProdutosScene);
        this.mainStage.show();
    }

    public void showGerenciarFilmeScreen() {
        this.mainStage.setScene(this.gerenciarFilmeScene);
        this.mainStage.show();
    }

    public void showGerenciarSessoesScreen() {
        this.mainStage.setScene(this.gerenciarSessoesScene);
        this.mainStage.show();
    }

    public void showRelatorioScreen() {
        this.mainStage.setScene(this.relatorioScene);
        this.mainStage.show();
    }
}
