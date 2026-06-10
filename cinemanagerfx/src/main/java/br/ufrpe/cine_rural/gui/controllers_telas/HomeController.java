package br.ufrpe.cine_rural.gui.controllers_telas;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import java.io.IOException;
import java.util.Optional;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import javafx.scene.image.Image;

public class HomeController {
    public static String gerenteAtual;

    @FXML
    private Pane painelCentral;

    @FXML
    private ImageView imageCentral;

    @FXML
    private Button tituloCentral;

    @FXML
    private Button btnGerente;

    @FXML
    private Button btnAtendente;

    @FXML private Button btnSair;

    @FXML
    public void btnGerenteAction() throws IOException {
        TextInputDialog tiDialog = new TextInputDialog();
        tiDialog.setTitle("Informe sua Senha");
        tiDialog.setHeaderText("Informe sua Senha");
        tiDialog.setContentText("Digite sua Senha:");
        tiDialog.showAndWait();

        Optional<String> result = tiDialog.showAndWait();
        if (result.isPresent()) {
            switch (result.get()) {
                case "1234": gerenteAtual = "PAULO!";
                    ScreenManager.getInstance().showGerenteScreen();
                    break;
                case "2345": gerenteAtual = "JULIA!";
                    ScreenManager.getInstance().showGerenteScreen();
                    break;
                case "3456": gerenteAtual = "ARTHUR!";
                    ScreenManager.getInstance().showGerenteScreen();
                    break;
                case "4567": gerenteAtual = "GABRIEL!";
                    ScreenManager.getInstance().showGerenteScreen();
                    break;
                case "5678": gerenteAtual = "GEDEDIAS!";
                    ScreenManager.getInstance().showGerenteScreen();
                    break;
                default:
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Erro");
                    alert.setHeaderText("Senha Invalida");
                    alert.showAndWait();
                    Platform.exit();
                    break;
            }
        }
    }

    @FXML
    public void btnAtendenteAction() throws IOException {
        ScreenManager.getInstance().showAtendenteScreen();
    }

    @FXML
    public void initialize() {
        imageCentral.fitWidthProperty().bind(painelCentral.widthProperty());
        imageCentral.fitHeightProperty().bind(painelCentral.heightProperty());
    }

    @FXML
    public void btnSairAction() throws IOException {
        Platform.exit();
    }

}
