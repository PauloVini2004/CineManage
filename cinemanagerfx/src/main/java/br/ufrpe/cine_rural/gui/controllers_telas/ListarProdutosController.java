package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.dados.implemento.RepositorioProdutoImpl;
import br.ufrpe.cine_rural.model.loja.Produto;
import br.ufrpe.cine_rural.negocios.ProdutoNegocios;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.*;

import javafx.geometry.Pos;

import javafx.stage.Stage;

import java.util.ArrayList;

public class ListarProdutosController {

    private final ProdutoNegocios produtoNegocios =
            new ProdutoNegocios(
                    RepositorioProdutoImpl.getInstancia()
            );

    @FXML
    private TilePane tileProdutos;

    @FXML
    private Button btnVoltar;

    @FXML
    private Button btnAdicionar;


    @FXML
    public void initialize() {

        carregarProdutos();

        btnVoltar.setOnAction(e -> voltarParaGerente());

        btnAdicionar.setOnAction(e -> adicionarProduto());
    }

    private VBox criarCard(Produto produto) {

        VBox card = new VBox(4);
        card.getStyleClass().add("card-produto");

        Label nome = new Label(produto.getNome());
        nome.getStyleClass().add("nome-produto");

        ImageView imagem = new ImageView();

        imagem.setFitWidth(100);
        imagem.setFitHeight(130);
        imagem.setPreserveRatio(true);

        try {

            imagem.setImage(
                    new Image(
                            getClass()
                                    .getResource(produto.getCaminhoImagem())
                                    .toExternalForm()
                    )
            );

        } catch (Exception e) {

            System.out.println(
                    "Imagem não encontrada: "
                            + produto.getNome()
            );
        }

        Label estoque =
                new Label(
                        produto.getQtdEstoque()
                                + " itens disp."
                );

        estoque.getStyleClass().add("disponivel");

        Label preco =
                new Label(
                        String.format(
                                "R$ %.2f",
                                produto.getPreco()
                        )
                );

        preco.getStyleClass().add("preco");

        Button btnEditar =
                new Button("Editar");

        btnEditar.getStyleClass().add("btn-editar");

        btnEditar.setOnAction(
                e -> irParaTelaEditar(produto)
        );

        HBox rodape =
                new HBox(
                        10,
                        preco,
                        btnEditar
                );

        rodape.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(
                nome,
                imagem,
                estoque,
                rodape
        );

        return card;
    }

    private void carregarProdutos() {

        tileProdutos.getChildren().clear();

        ArrayList<Produto> produtos =
                produtoNegocios.listarProdutos();

        for (Produto produto : produtos) {

            tileProdutos.getChildren().add(
                    criarCard(produto)
            );
        }
    }

    private void voltarParaGerente() {

        trocarTela(
                "/br/ufrpe/cine_rural/gui/home-view.fxml",
                btnVoltar
        );
    }

    private void adicionarProduto() {

        trocarTela(
                "/br/ufrpe/cine_rural/gui/AdicionarProduto.fxml",
                btnAdicionar
        );
    }

    private void irParaTelaEditar(Produto produto) {

        try {

            System.out.println(
                    "Editando: " + produto.getNome()
            );

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/br/ufrpe/cine_rural/gui/Produto.fxml"
                            )
                    );

            Parent root = loader.load();

            Stage stage =
                    (Stage) btnVoltar
                            .getScene()
                            .getWindow();

            stage.setScene(
                    new Scene(root)
            );

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void trocarTela(
            String caminho,
            Button referencia
    ) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(caminho)
                    );

            Parent root = loader.load();

            Stage stage =
                    (Stage) referencia
                            .getScene()
                            .getWindow();

            stage.setScene(
                    new Scene(root)
            );

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
