package br.ufrpe.cine_rural.gui.controllers_telas;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProdutoController {
    @FXML
    private Label nomeProduto1;

    @FXML
    private Label precoProduto1;

    @FXML
    private ImageView imagemProduto1;

    @FXML
    public void initialize() {

        nomeProduto1.setText("Pipoca");

        precoProduto1.setText("R$ 14,90");

        imagemProduto1.setImage(
                new Image(
                        getClass().getResourceAsStream(
                                "/br/ufrpe/cine_rural/gui/Imagens/Pipoca.jpg"
                        )
                )
        );
    }
}
