package br.ufrpe.cine_rural.gui.controllers_telas;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.Objects;

public class GerenteController {
    @FXML
    private Button btnSair;

    @FXML
    private Label txtGerente;

    @FXML
    private Button btnEditProdutos;

    @FXML
    private Button btnGerenciarFilmes;

    @FXML
    private Button btnGerenciarSessoes;

    @FXML
    private Button btnRelatorio;

    @FXML
    public void initialize(){
        txtGerente.setText(Objects.requireNonNullElse(HomeController.gerenteAtual, "Gerente!"));
    }

    @FXML
    private void voltarParaHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/ufrpe/cine_rural/gui/home-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnSair.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.out.println("Erro ao tentar voltar para a tela de Atendente. Verifique o caminho do FXML.");
            e.printStackTrace();
        }
    }

    @FXML
    private void btnEditProdutosAction() {
        ScreenManager.getInstance().showListarProdutosScreen();
    }

    @FXML
    private void btnGerenciarFilmesAction() {
        ScreenManager.getInstance().showGerenciarFilmeScreen();
    }

    @FXML
    private void btnGerenciarSessoesAction() {
        ScreenManager.getInstance().showGerenciarSessoesScreen();
    }

    @FXML
    private void btnRelatorioAction() {
        ScreenManager.getInstance().showRelatorioScreen();
    }
}
