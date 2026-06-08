package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.dados.implemento.RepositorioProdutoImpl;
import br.ufrpe.cine_rural.model.loja.Produto;
import br.ufrpe.cine_rural.negocios.ProdutoNegocios;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.stage.Stage;

import java.util.Optional;
import javafx.stage.FileChooser;
import java.io.File;

public class EditarProdutoController {

    private Produto produtoAtual;

    private final ProdutoNegocios produtoNegocios =
            new ProdutoNegocios(
                    RepositorioProdutoImpl.getInstancia()
            );

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtPreco;

    @FXML
    private TextField txtQuantidade;

    @FXML
    private Label lblNomeProduto;

    @FXML
    private Label lblPreco;

    @FXML
    private Label lblEstoque;

    @FXML
    private ImageView imgProduto;

    @FXML
    private Button btnVoltar;

    @FXML
    private Button btnExcluir;

    @FXML
    private Button btnConfirmar;

    @FXML
    private Button btnSelecionarImagem;

    private String caminhoImagemSelecionada;

    @FXML
    public void initialize() {

        btnVoltar.setOnAction(
                e -> voltar()
        );

        btnExcluir.setOnAction(
                e -> excluirProduto()
        );

        btnConfirmar.setOnAction(
                e -> salvarAlteracoes()
        );

        btnSelecionarImagem.setOnAction(
                e -> selecionarImagem()
        );
    }

    private void selecionarImagem() {

        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Selecionar Imagem"
        );

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Imagens",
                        "*.png",
                        "*.jpg"
                )
        );

        File arquivo =
                fileChooser.showOpenDialog(
                        (Stage) btnConfirmar
                                .getScene()
                                .getWindow()
                );

        if (arquivo != null) {

            caminhoImagemSelecionada =
                    arquivo.getAbsolutePath();

            Image img =
                    new Image(
                            arquivo.toURI().toString()
                    );

            imgProduto.setImage(img);
        }

    }

    public void carregarProduto(Produto produto) {

        this.produtoAtual = produto;

        caminhoImagemSelecionada =
                produto.getCaminhoImagem();

        txtId.setText(
                String.valueOf(produto.getId())
        );

        txtNome.setText(
                produto.getNome()
        );

        txtPreco.setText(
                String.valueOf(produto.getPreco())
        );

        txtQuantidade.setText(
                String.valueOf(produto.getQtdEstoque())
        );

        atualizarVisualizacao();
    }

    private void atualizarVisualizacao() {

        lblNomeProduto.setText(
                txtNome.getText()
        );

        lblPreco.setText(
                "R$ " + txtPreco.getText()
        );

        lblEstoque.setText(
                txtQuantidade.getText()
                        + " disp."
        );

        if (caminhoImagemSelecionada != null
                && !caminhoImagemSelecionada.isBlank()) {

            Image img =
                    new Image(
                            new File(
                                    caminhoImagemSelecionada
                            ).toURI().toString()
                    );

            imgProduto.setImage(img);
        }
    }

    private void salvarAlteracoes() {

        try {

            int id =
                    Integer.parseInt(
                            txtId.getText()
                    );

            String nome =
                    txtNome.getText();

            double preco =
                    Double.parseDouble(
                            txtPreco.getText()
                    );

            int qtd =
                    Integer.parseInt(
                            txtQuantidade.getText()
                    );

            produtoNegocios.atualizarProduto(
                    id,
                    nome,
                    preco,
                    qtd,
                    caminhoImagemSelecionada
            );

            produtoAtual =
                    produtoNegocios.buscarProduto(id);

            atualizarVisualizacao();

            Alert alert =
                    new Alert(
                            Alert.AlertType.INFORMATION
                    );

            alert.setTitle(
                    "Sucesso"
            );

            alert.setHeaderText(null);

            alert.setContentText(
                    "Produto atualizado."
            );

            alert.showAndWait();

        } catch (Exception e) {

            Alert alert =
                    new Alert(
                            Alert.AlertType.ERROR
                    );

            alert.setHeaderText(null);

            alert.setContentText(
                    e.getMessage()
            );

            alert.showAndWait();
        }
    }

    private void excluirProduto() {

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle(
                "Excluir Produto"
        );

        alert.setHeaderText(null);

        alert.setContentText(
                "Deseja realmente excluir este produto?"
        );

        Optional<ButtonType> resposta =
                alert.showAndWait();

        if (resposta.isPresent()
                && resposta.get() == ButtonType.OK) {

            produtoNegocios.removerProduto(
                    produtoAtual.getId()
            );

            voltar();
        }
    }

    private void voltar() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/br/ufrpe/cine_rural/gui/TelasProduto/ListarProdutos.fxml"
                            )
                    );

            Parent root =
                    loader.load();

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
}
