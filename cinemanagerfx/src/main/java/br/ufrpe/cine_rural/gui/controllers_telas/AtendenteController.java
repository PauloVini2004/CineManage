package br.ufrpe.cine_rural.gui.controllers_telas;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AtendenteController {
    @FXML
    public Button btnCprProd;

    @FXML
    public Button btnCprIngr;

    @FXML
    public Button btnEncCxa;

    @FXML
    public void btnCprIngrAction() {
        ScreenManager.getInstance().showFilmesScreen();
    }

    @FXML
    public void btnCprProdAction() {
        ScreenManager.getInstance().showProdutosScreen();
    }

    @FXML
    public void btnEncCxaAction() {
        ScreenManager.getInstance().showHomeScreen();
    }


}
