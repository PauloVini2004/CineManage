package br.ufrpe.cine_rural.gui.controllers_telas;

import java.io.IOException;
import java.util.Objects;

import br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSalaImpl;
import br.ufrpe.cine_rural.model.tiposala.Comum;
import br.ufrpe.cine_rural.model.tiposala.Imax;
import br.ufrpe.cine_rural.model.tiposala.Vip;
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
    private Scene assentosScene;

    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    private ScreenManager() {
        // Cadastra as salas uma única vez no repositório compartilhado
        RepositorioSalaImpl repositorioSala = RepositorioSalaImpl.getInstancia();
        if (repositorioSala.listar().isEmpty()) {
            repositorioSala.cadastrar(new Comum(1, 20));
            repositorioSala.cadastrar(new Imax(2, 30));
            repositorioSala.cadastrar(new Vip(3, 20));
        }

        try {
            BorderPane homePane = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/home-view.fxml"));
            this.homeScene = new Scene(homePane);

            BorderPane atendentePane = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/Atendente-View.fxml"));
            this.atendenteScene = new Scene(atendentePane);

            BorderPane gerentePane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/Gerente-View.fxml")));
            this.gerenteScene = new Scene(gerentePane);

            // Tela Filmes (com estilo)
            FXMLLoader filmesLoader = new FXMLLoader(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/Filmes.fxml"));
            AnchorPane filmesPane = filmesLoader.load();
            this.filmesScene = new Scene(filmesPane);
            this.filmesScene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource("/br/ufrpe/cine_rural/gui/EstiloFilmes.css")
                    ).toExternalForm()
            );

            AnchorPane produtosPane = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/TelasProduto/Produto.fxml"));
            this.produtosScene = new Scene(produtosPane);

            BorderPane listarProdutosPane = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/TelasProduto/ListarProdutos.fxml"));
            this.listarProdutosScene = new Scene(listarProdutosPane);

            ScrollPane gerenciarFilmes = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/Gerenciar filmes.fxml"));
            this.gerenciarFilmeScene = new Scene(gerenciarFilmes);

            AnchorPane gerenciarSessoes = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/Gerenciar sessoes.fxml"));
            this.gerenciarSessoesScene = new Scene(gerenciarSessoes);

            BorderPane relatorioPane = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/DashBoard.fxml"));
            this.relatorioScene = new Scene(relatorioPane);

            AnchorPane assentosPane = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/Assentos.fxml"));
            // inicializando relatorio
            this.assentosScene = new Scene(assentosPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
        mainStage.setWidth(1024);
        mainStage.setHeight(768);
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
        // Recarrega a tela sempre que chamada para refletir sessões atualizadas
        try {
            FXMLLoader filmesLoader = new FXMLLoader(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/Filmes.fxml"));
            AnchorPane filmesPane = filmesLoader.load();
            this.filmesScene = new Scene(filmesPane);
            this.filmesScene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource("/br/ufrpe/cine_rural/gui/EstiloFilmes.css")
                    ).toExternalForm()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        // Recarrega para pegar salas e filmes atualizados
        try {
            AnchorPane gerenciarSessoes = FXMLLoader.load(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/Gerenciar sessoes.fxml"));
            this.gerenciarSessoesScene = new Scene(gerenciarSessoes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mainStage.setScene(this.gerenciarSessoesScene);
        this.mainStage.show();
    }

    public void showRelatorioScreen() {
        this.mainStage.setScene(this.relatorioScene);
        this.mainStage.show();
    }

    public void showAssentosScreen() {
        this.mainStage.setScene(this.assentosScene);
        this.mainStage.show();
    }
}
