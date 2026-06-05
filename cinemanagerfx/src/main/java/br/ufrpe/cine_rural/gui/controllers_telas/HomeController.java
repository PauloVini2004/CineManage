package br.ufrpe.cine_rural.gui.controllers_telas;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import java.io.IOException;
import java.util.Optional;

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
    private Text txtGerente;

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
                case "1234": HomeController.gerenteAtual = "PAULO!"; break;
                case "2345": HomeController.gerenteAtual = "JULIA!"; break;
                case "3456": HomeController.gerenteAtual = "ARTHUR!"; break;
                case "4567": HomeController.gerenteAtual = "GABRIEL!"; break;
                case "5678": HomeController.gerenteAtual = "GEDEDIAS!"; break;
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
    public void initialize() {
        imageCentral.fitWidthProperty().bind(painelCentral.widthProperty());
        imageCentral.fitHeightProperty().bind(painelCentral.heightProperty());
    }
}
