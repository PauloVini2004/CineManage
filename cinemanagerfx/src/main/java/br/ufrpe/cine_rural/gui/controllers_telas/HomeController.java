package br.ufrpe.cine_rural.gui.controllers_telas;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class HomeController {
    @FXML
    private Pane painelCentral;

    @FXML
    private ImageView imageCentral;

    @FXML
    public void initialize() {
        imageCentral.fitWidthProperty().bind(painelCentral.widthProperty());
        imageCentral.fitHeightProperty().bind(painelCentral.heightProperty());
    }
}
