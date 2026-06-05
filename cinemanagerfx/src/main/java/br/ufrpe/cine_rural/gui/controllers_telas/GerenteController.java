package br.ufrpe.cine_rural.gui.controllers_telas;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class GerenteController {
    @FXML
    private Button btnSair;

    @FXML
    private Text txtGerente;

    @FXML
    public void initialize(){
        txtGerente.setText(HomeController.gerenteAtual);
    }

    @FXML
    public void onSairClick(){
        Platform.exit();
    }
}
