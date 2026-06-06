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

import javafx.stage.Stage;

import java.util.ArrayList;

import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Pos;

public class ProdutoController {

    private ProdutoNegocios produtoNegocios = new ProdutoNegocios(RepositorioProdutoImpl.getInstancia());
    private final Map<Integer, Integer> quantidades = new HashMap<>();

    @FXML private Button resumoCompra;
    @FXML private Button btnAvancar;
    @FXML private Button btnVoltar;

    @FXML
    private TilePane tileProdutos;


    @FXML
    public void initialize() {

        inicializarEstoqueGlobal();

        carregarProdutos();

        atualizarSubtotalGeral();

        btnVoltar.setOnAction(
                e -> voltarParaAtendente()
        );

    }

    private VBox criarCard(Produto produto) {

        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);

        card.getStyleClass().add("card-produto");

        Label nome =
                new Label(produto.getNome());

        Label preco =
                new Label(
                        String.format(
                                "R$ %.2f",
                                produto.getPreco()
                        )
                );

        Label contador =
                new Label(
                        "0 itens | "
                                + produto.getQtdEstoque()
                                + " disp."
                );

        ImageView imagem =
                new ImageView();

        imagem.setFitWidth(150);
        imagem.setFitHeight(225);
        imagem.setPreserveRatio(true);

        try {

            imagem.setImage(
                    new Image(
                            getClass()
                                    .getResource(
                                            produto.getCaminhoImagem()
                                    )
                                    .toExternalForm()
                    )
            );

        } catch (Exception e) {

            System.out.println(
                    "Erro ao carregar imagem de "
                            + produto.getNome()
            );
        }

        Button btnMais =
                new Button("+");

        Button btnMenos =
                new Button("-");

        btnMais.setOnAction(
                e -> adicionarItem(
                        produto,
                        contador
                )
        );

        btnMenos.setOnAction(
                e -> removerItem(
                        produto,
                        contador
                )
        );

        HBox botoes =
                new HBox(
                        10,
                        btnMenos,
                        btnMais
                );

        StackPane imagemPane =
                new StackPane(imagem);

        card.getChildren().addAll(
                nome,
                imagemPane,
                contador,
                preco,
                botoes
        );

        return card;
    }

    //Isso aqui vai ser apagado depois, foi só para testar os ítens na tela
    private void inicializarEstoqueGlobal() {

        if (produtoNegocios.isEstoqueVazio()) {

            try {

                produtoNegocios.cadastrarProduto(
                        1,
                        "Pipoca",
                        14.90,
                        50,
                        "/br/ufrpe/cine_rural/gui/ImagensProduto/Pipoca2.jpg"
                );

                produtoNegocios.cadastrarProduto(
                        2,
                        "Coca Cola",
                        10.50,
                        30,
                        "/br/ufrpe/cine_rural/gui/ImagensProduto/RefriCoca.jpg"
                );

                produtoNegocios.cadastrarProduto(
                        3,
                        "Refri Fanta",
                        9.50,
                        20,
                        "/br/ufrpe/cine_rural/gui/ImagensProduto/RefriFanta.jpg"
                );

                produtoNegocios.cadastrarProduto(
                        4,
                        "Guaraná",
                        9.50,
                        15,
                        "/br/ufrpe/cine_rural/gui/ImagensProduto/Guarana.jpg"
                );

                produtoNegocios.cadastrarProduto(
                        5,
                        "Sprite",
                        9.50,
                        40,
                        "/br/ufrpe/cine_rural/gui/ImagensProduto/RefriSprite.jpg"
                );

                produtoNegocios.cadastrarProduto(
                        6,
                        "Hershey's",
                        13.80,
                        10,
                        "/br/ufrpe/cine_rural/gui/ImagensProduto/Hersheys.jpg"
                );

                System.out.println(
                        "[Sistema] Estoque inicializado com sucesso!"
                );

            } catch (Exception e) {

                System.out.println(
                        "Erro ao inicializar o estoque padrão."
                );

                e.printStackTrace();
            }
        }
    }


    private void carregarProdutos() {

        tileProdutos.getChildren().clear();

        ArrayList<Produto> produtos =
                produtoNegocios.listarProdutos();

        System.out.println(
                "Quantidade de produtos: "
                        + produtos.size()
        );

        for (Produto produto : produtos) {

            System.out.println(
                    produto.getId()
                            + " - "
                            + produto.getNome()
            );

            tileProdutos.getChildren().add(
                    criarCard(produto)
            );
        }
    }



    private void adicionarItem(
            Produto p,
            Label labelContador
    ) {

        if (p == null)
            return;

        int qtdAtual =
                quantidades.getOrDefault(
                        p.getId(),
                        0
                );

        try {

            produtoNegocios.validarEstoque(
                    p,
                    qtdAtual + 1
            );

        } catch (IllegalStateException e) {

            System.out.println(
                    e.getMessage()
            );

            return;
        }

        qtdAtual++;

        quantidades.put(
                p.getId(),
                qtdAtual
        );

        labelContador.setText(
                qtdAtual
                        + " itens | "
                        + p.getQtdEstoque()
                        + " disp."
        );

        atualizarSubtotalGeral();
    }

    private void removerItem(
            Produto p,
            Label labelContador
    ) {

        if (p == null)
            return;

        int qtdAtual =
                quantidades.getOrDefault(
                        p.getId(),
                        0
                );

        if (qtdAtual <= 0)
            return;

        qtdAtual--;

        quantidades.put(
                p.getId(),
                qtdAtual
        );

        labelContador.setText(
                qtdAtual
                        + " itens | "
                        + p.getQtdEstoque()
                        + " disp."
        );

        atualizarSubtotalGeral();
    }


    private void atualizarSubtotalGeral() {

        int totalItens = 0;

        double valorTotal = 0;

        ArrayList<Produto> produtos =
                produtoNegocios.listarProdutos();

        for (Produto produto : produtos) {

            int qtd =
                    quantidades.getOrDefault(
                            produto.getId(),
                            0
                    );

            totalItens += qtd;

            valorTotal +=
                    qtd * produto.getPreco();
        }

        resumoCompra.setText(
                totalItens
                        + " itens | Subtotal R$ "
                        + String.format(
                        "%.2f",
                        valorTotal
                )
        );
    }

    private void voltarParaAtendente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/ufrpe/cine_rural/gui/Atendente-View.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.out.println("Erro ao tentar voltar para a tela de Atendente. Verifique o caminho do FXML.");
            e.printStackTrace();
        }
    }
}