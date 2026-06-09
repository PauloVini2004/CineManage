package br.ufrpe.cine_rural.gui.controllers_telas;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.util.Objects;

public class GerenteController {
    @FXML
    private Button btnSair;

    @FXML
    public Label txtGerente;

    @FXML
    public void initialize(){
        txtGerente.setText(Objects.requireNonNullElse(HomeController.gerenteAtual, "Gerente!"));
    }

    @FXML
    public void onSairClick(){
        ScreenManager.getInstance().showHomeScreen();
    }
}
