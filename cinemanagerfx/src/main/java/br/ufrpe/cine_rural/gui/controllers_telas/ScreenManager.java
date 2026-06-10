package br.ufrpe.cine_rural.gui.controllers_telas;

import java.io.IOException;
import java.util.Objects;

import br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSalaImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
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

            BorderPane gerentePane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/Gerente-View.fxml")));
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

            ScrollPane gerenciarFilmes = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/Gerenciar filmes.fxml"));
            // inicializando cena gerenciar filmes
            this.gerenciarFilmeScene = new Scene(gerenciarFilmes);

            AnchorPane gerenciarSessoes = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/Gerenciar sessoes.fxml"));
            // inicializando cena gerenciar sessoes
            this.gerenciarSessoesScene = new Scene(gerenciarSessoes);

            BorderPane relatorioPane = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/DashBoard.fxml"));
            // inicializando relatorio
            this.relatorioScene= new Scene(relatorioPane);

            //Cena filmes

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
