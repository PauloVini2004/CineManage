package br.ufrpe.cine_rural.gui.controllers_telas;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProdutoController {

    // Produto 1
    @FXML private Label nomeProduto1;
    @FXML private Label precoProduto1;
    @FXML private ImageView imagemProduto1;
    @FXML private Label contadorProduto1;
    @FXML private Button maisProduto1;
    @FXML private Button menosProduto1;
    private int qtdProduto1 = 0;

    // Produto 2
    @FXML private Label nomeProduto2;
    @FXML private Label precoProduto2;
    @FXML private ImageView imagemProduto2;
    @FXML private Label contadorProduto2;
    @FXML private Button maisProduto2;
    @FXML private Button menosProduto2;
    private int qtdProduto2 = 0;

    // Produto 3
    @FXML private Label nomeProduto3;
    @FXML private Label precoProduto3;
    @FXML private ImageView imagemProduto3;
    @FXML private Label contadorProduto3;
    @FXML private Button maisProduto3;
    @FXML private Button menosProduto3;
    private int qtdProduto3 = 0;

    // Produto 4
    @FXML private Label nomeProduto4;
    @FXML private Label precoProduto4;
    @FXML private ImageView imagemProduto4;
    @FXML private Label contadorProduto4;
    @FXML private Button maisProduto4;
    @FXML private Button menosProduto4;
    private int qtdProduto4 = 0;

    // Produto 5
    @FXML private Label nomeProduto5;
    @FXML private Label precoProduto5;
    @FXML private ImageView imagemProduto5;
    @FXML private Label contadorProduto5;
    @FXML private Button maisProduto5;
    @FXML private Button menosProduto5;
    private int qtdProduto5 = 0;

    // Produto 6
    @FXML private Label nomeProduto6;
    @FXML private Label precoProduto6;
    @FXML private ImageView imagemProduto6;
    @FXML private Label contadorProduto6;
    @FXML private Button maisProduto6;
    @FXML private Button menosProduto6;
    private int qtdProduto6 = 0;

    @FXML
    public void initialize() {
        // Produto 1
        nomeProduto1.setText("Pipoca");
        precoProduto1.setText("R$ 14,90");
        contadorProduto1.setText("0 itens");
        imagemProduto1.setImage(new Image(getClass().getResourceAsStream("/br/ufrpe/cine_rural/gui/Imagens/Pipoca2.jpg")));
        maisProduto1.setOnAction(e -> atualizarContador(++qtdProduto1, contadorProduto1));
        menosProduto1.setOnAction(e -> atualizarContador(qtdProduto1 > 0 ? --qtdProduto1 : 0, contadorProduto1));

        // Produto 2
        nomeProduto2.setText("Coca Cola");
        precoProduto2.setText("R$ 10,50");
        contadorProduto2.setText("0 itens");
        imagemProduto2.setImage(new Image(getClass().getResourceAsStream("/br/ufrpe/cine_rural/gui/Imagens/RefriCoca.jpg")));
        maisProduto2.setOnAction(e -> atualizarContador(++qtdProduto2, contadorProduto2));
        menosProduto2.setOnAction(e -> atualizarContador(qtdProduto2 > 0 ? --qtdProduto2 : 0, contadorProduto2));

        // Produto 3
        nomeProduto3.setText("Refri Fanta");
        precoProduto3.setText("R$ 9,50");
        contadorProduto3.setText("0 itens");
        imagemProduto3.setImage(new Image(getClass().getResourceAsStream("/br/ufrpe/cine_rural/gui/Imagens/RefriFanta.jpg")));
        maisProduto3.setOnAction(e -> atualizarContador(++qtdProduto3, contadorProduto3));
        menosProduto3.setOnAction(e -> atualizarContador(qtdProduto3 > 0 ? --qtdProduto3 : 0, contadorProduto3));

        // Produto 4
        nomeProduto4.setText("Guaraná");
        precoProduto4.setText("R$ 9,50");
        contadorProduto4.setText("0 itens");
        imagemProduto4.setImage(new Image(getClass().getResourceAsStream("/br/ufrpe/cine_rural/gui/Imagens/Guarana.jpg")));
        maisProduto4.setOnAction(e -> atualizarContador(++qtdProduto4, contadorProduto4));
        menosProduto4.setOnAction(e -> atualizarContador(qtdProduto4 > 0 ? --qtdProduto4 : 0, contadorProduto4));

        // Produto 5
        nomeProduto5.setText("Sprite");
        precoProduto5.setText("R$ 9,50");
        contadorProduto5.setText("0 itens");
        imagemProduto5.setImage(new Image(getClass().getResourceAsStream("/br/ufrpe/cine_rural/gui/Imagens/RefriSprite.jpg")));
        maisProduto5.setOnAction(e -> atualizarContador(++qtdProduto5, contadorProduto5));
        menosProduto5.setOnAction(e -> atualizarContador(qtdProduto5 > 0 ? --qtdProduto5 : 0, contadorProduto5));

        // Produto 6
        nomeProduto6.setText("Hershey's");
        precoProduto6.setText("R$ 13,80");
        contadorProduto6.setText("0 itens");
        imagemProduto6.setImage(new Image(getClass().getResourceAsStream("/br/ufrpe/cine_rural/gui/Imagens/Hersheys.jpg")));
        maisProduto6.setOnAction(e -> atualizarContador(++qtdProduto6, contadorProduto6));
        menosProduto6.setOnAction(e -> atualizarContador(qtdProduto6 > 0 ? --qtdProduto6 : 0, contadorProduto6));
    }

    private void atualizarContador(int qtd, Label contador) {
        contador.setText(qtd + " itens");
    }
}
