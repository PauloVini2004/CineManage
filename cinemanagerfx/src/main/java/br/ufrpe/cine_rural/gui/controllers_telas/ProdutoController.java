package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.dados.implemento.RepositorioProdutoImpl;
import br.ufrpe.cine_rural.gui.controllers_telas.emergencia.PagamentoProdutoController;
import br.ufrpe.cine_rural.model.loja.ItemVenda;
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

import java.io.File;
import java.util.ArrayList;

import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Pos;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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

        carregarProdutos();

        atualizarSubtotalGeral();

        btnVoltar.setOnAction(
                e -> voltarParaAtendente()
        );
        btnAvancar.setOnAction(
                e -> avancarParaPagamento()
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

        ImageView imagem = new ImageView();

        try {

            Image img = new Image(
                    new File(produto.getCaminhoImagem())
                            .toURI()
                            .toString()
            );

            imagem.setImage(img);

        } catch (Exception e) {

            System.out.println(
                    "Imagem não encontrada: "
                            + produto.getCaminhoImagem()
            );
        };


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

        nome.getStyleClass().add("nome-produto");

        preco.getStyleClass().add("preco");

        contador.getStyleClass().add("contador");

        imagemPane.getStyleClass().add("imagem-produto");

        btnMais.getStyleClass().add("btn-mais");

        btnMenos.getStyleClass().add("btn-menos");

        imagem.setFitWidth(100);
        imagem.setFitHeight(130);
        imagem.setPreserveRatio(false);

        return card;
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


    private void avancarParaPagamento() {

        ArrayList<ItemVenda> itensVenda =
                gerarItensVenda();

        if (itensVenda.isEmpty()) {

            Alert alerta =
                    new Alert(
                            Alert.AlertType.WARNING
                    );

            alerta.setTitle("Carrinho vazio");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Selecione pelo menos um produto para continuar."
            );

            alerta.showAndWait();

            return;
        }

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/br/ufrpe/cine_rural/gui/emergencia/PagamentoProduto.fxml"
                            )
                    );

            Parent root = loader.load();

            PagamentoProdutoController controller =
                    loader.getController();

            controller.receberItensVenda(
                    gerarItensVenda()
            );

            Stage stage =
                    (Stage) btnAvancar
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


    private ArrayList<ItemVenda> gerarItensVenda() { //Gera lista de produtos adicionados ao carrinho

        ArrayList<ItemVenda> itens =
                new ArrayList<>();

        ArrayList<Produto> produtos =
                produtoNegocios.listarProdutos();

        for (Produto produto : produtos) {

            int qtd =
                    quantidades.getOrDefault(
                            produto.getId(),
                            0
                    );

            System.out.println(
                    produto.getNome()
                            + " -> id=" + produto.getId()
                            + " qtd=" + qtd
            );

            if (qtd > 0) {

                itens.add(
                        new ItemVenda(
                                qtd,
                                produto
                        )
                );
            }

            System.out.println("Mapa de quantidades:");
            System.out.println(quantidades);
        }

        return itens;
    }
}