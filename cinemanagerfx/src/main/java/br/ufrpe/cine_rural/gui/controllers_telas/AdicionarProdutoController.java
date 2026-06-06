package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.dados.implemento.RepositorioProdutoImpl;
import br.ufrpe.cine_rural.model.loja.Produto;
import br.ufrpe.cine_rural.negocios.ProdutoNegocios;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AdicionarProdutoController {

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
    private TextField txtImagem;

    @FXML
    private TextField txtQuantidade;

    @FXML
    private Button btnSalvar;

    @FXML
    private Button btnVoltar;

    @FXML
    public void initialize() {

        btnSalvar.setOnAction(e -> salvarProduto());

        btnVoltar.setOnAction(e -> voltar());
    }

    private void salvarProduto() {

        try {

            int id = Integer.parseInt(
                    txtId.getText().trim()
            );

            String nome = txtNome.getText().trim();

            double preco = Double.parseDouble(
                    txtPreco.getText().trim()
            );

            int quantidade = Integer.parseInt(
                    txtQuantidade.getText().trim()
            );

            String caminhoImagem =
                    txtImagem.getText().trim();

            produtoNegocios.cadastrarProduto(
                    id,
                    nome,
                    preco,
                    quantidade,
                    caminhoImagem
            );
            System.out.println(
                    "Produtos após cadastro: "
                            + produtoNegocios.listarProdutos().size()
            );

            mostrarSucesso(
                    "Produto cadastrado com sucesso!"
            );

            limparCampos();

        } catch (NumberFormatException e) {

            mostrarErro(
                    "ID, preço e quantidade devem ser números válidos."
            );

        } catch (Exception e) {

            mostrarErro(e.getMessage());
        }

        for (Produto p : produtoNegocios.listarProdutos()) {

            System.out.println(
                    p.getId() + " - " + p.getNome()
            );
        }
    }

    private void limparCampos() {

        txtId.clear();
        txtNome.clear();
        txtPreco.clear();
        txtImagem.clear();
        txtQuantidade.clear();
    }

    private void voltar() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/br/ufrpe/cine_rural/gui/ListarProdutos.fxml"
                            )
                    );

            Parent root = loader.load();

            Stage stage =
                    (Stage) btnVoltar.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {

            mostrarErro(
                    "Erro ao voltar para a tela anterior."
            );

            e.printStackTrace();
        }
    }

    private void mostrarErro(String mensagem) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);

        alert.showAndWait();
    }

    private void mostrarSucesso(String mensagem) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);

        alert.showAndWait();
    }
}
