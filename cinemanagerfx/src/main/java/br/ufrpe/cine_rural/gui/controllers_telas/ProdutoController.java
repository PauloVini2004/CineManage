package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.dados.implemento.RepositorioProdutoImpl;
import br.ufrpe.cine_rural.dados.interfaces.IRepositorioProduto;
import br.ufrpe.cine_rural.model.loja.ItemVenda;
import br.ufrpe.cine_rural.model.loja.Produto;
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

public class ProdutoController {

    // Instância única global do repositório
    private IRepositorioProduto repoProduto = RepositorioProdutoImpl.getInstancia();

    @FXML private Button resumoCompra;
    @FXML private Button btnAvancar;
    @FXML private Button btnVoltar;

    // Produto 1 ao 6 e suas respectivas quantidades selecionadas
    @FXML private Label nomeProduto1, precoProduto1, contadorProduto1;
    @FXML private ImageView imagemProduto1;
    @FXML private Button maisProduto1, menosProduto1;
    private int qtdProduto1 = 0;
    private Produto prod1;

    @FXML private Label nomeProduto2, precoProduto2, contadorProduto2;
    @FXML private ImageView imagemProduto2;
    @FXML private Button maisProduto2, menosProduto2;
    private int qtdProduto2 = 0;
    private Produto prod2;

    @FXML private Label nomeProduto3, precoProduto3, contadorProduto3;
    @FXML private ImageView imagemProduto3;
    @FXML private Button maisProduto3, menosProduto3;
    private int qtdProduto3 = 0;
    private Produto prod3;

    @FXML private Label nomeProduto4, precoProduto4, contadorProduto4;
    @FXML private ImageView imagemProduto4;
    @FXML private Button maisProduto4, menosProduto4;
    private int qtdProduto4 = 0;
    private Produto prod4;

    @FXML private Label nomeProduto5, precoProduto5, contadorProduto5;
    @FXML private ImageView imagemProduto5;
    @FXML private Button maisProduto5, menosProduto5;
    private int qtdProduto5 = 0;
    private Produto prod5;

    @FXML private Label nomeProduto6, precoProduto6, contadorProduto6;
    @FXML private ImageView imagemProduto6;
    @FXML private Button maisProduto6, menosProduto6;
    private int qtdProduto6 = 0;
    private Produto prod6;

    @FXML
    public void initialize() {
        // Inicializa o estoque apenas na primeira vez que a tela abrir no sistema
        if (repoProduto.listar().isEmpty()) {
            inicializarEstoqueDoSistema();
        }

        // Buscar referências diretas do repositório
        prod1 = repoProduto.buscar(1);
        prod2 = repoProduto.buscar(2);
        prod3 = repoProduto.buscar(3);
        prod4 = repoProduto.buscar(4);
        prod5 = repoProduto.buscar(5);
        prod6 = repoProduto.buscar(6);

        // Renderizar na tela
        configurarProdutoNaTela(prod1, nomeProduto1, precoProduto1, contadorProduto1, imagemProduto1, "/br/ufrpe/cine_rural/gui/imagens/Pipoca2.jpg");
        configurarProdutoNaTela(prod2, nomeProduto2, precoProduto2, contadorProduto2, imagemProduto2, "/br/ufrpe/cine_rural/gui/imagens/RefriCoca.jpg");
        configurarProdutoNaTela(prod3, nomeProduto3, precoProduto3, contadorProduto3, imagemProduto3, "/br/ufrpe/cine_rural/gui/imagens/RefriFanta.jpg");
        configurarProdutoNaTela(prod4, nomeProduto4, precoProduto4, contadorProduto4, imagemProduto4, "/br/ufrpe/cine_rural/gui/imagens/Guarana.jpg");
        configurarProdutoNaTela(prod5, nomeProduto5, precoProduto5, contadorProduto5, imagemProduto5, "/br/ufrpe/cine_rural/gui/imagens/RefriSprite.jpg");
        configurarProdutoNaTela(prod6, nomeProduto6, precoProduto6, contadorProduto6, imagemProduto6, "/br/ufrpe/cine_rural/gui/imagens/Hersheys.jpg");

        // Eventos de clique corrigidos (Isolando o fluxo para os métodos controlarem a lógica)
        maisProduto1.setOnAction(e -> adicionarItem(prod1, contadorProduto1));
        menosProduto1.setOnAction(e -> removerItem(prod1, contadorProduto1));

        maisProduto2.setOnAction(e -> adicionarItem(prod2, contadorProduto2));
        menosProduto2.setOnAction(e -> removerItem(prod2, contadorProduto2));

        maisProduto3.setOnAction(e -> adicionarItem(prod3, contadorProduto3));
        menosProduto3.setOnAction(e -> removerItem(prod3, contadorProduto3));

        maisProduto4.setOnAction(e -> adicionarItem(prod4, contadorProduto4));
        menosProduto4.setOnAction(e -> removerItem(prod4, contadorProduto4));

        maisProduto5.setOnAction(e -> adicionarItem(prod5, contadorProduto5));
        menosProduto5.setOnAction(e -> removerItem(prod5, contadorProduto5));

        maisProduto6.setOnAction(e -> adicionarItem(prod6, contadorProduto6));
        menosProduto6.setOnAction(e -> removerItem(prod6, contadorProduto6));

        // Ação para o botão voltar
        btnVoltar.setOnAction(e -> voltarParaAtendente());
    }

    private void inicializarEstoqueDoSistema() {
        repoProduto.cadastrar(new Produto(1, "Pipoca", 14.90, 50));
        repoProduto.cadastrar(new Produto(2, "Coca Cola", 10.50, 30));
        repoProduto.cadastrar(new Produto(3, "Refri Fanta", 9.50, 20));
        repoProduto.cadastrar(new Produto(4, "Guaraná", 9.50, 15));
        repoProduto.cadastrar(new Produto(5, "Sprite", 9.50, 40));
        repoProduto.cadastrar(new Produto(6, "Hershey's", 13.80, 10));
    }

    private void configurarProdutoNaTela(Produto p, Label nome, Label preco, Label contador, ImageView imgView, String caminhoImg) {
        if (p != null) {
            nome.setText(p.getNome());
            preco.setText(String.format("R$ %.2f", p.getPreco()));
            contador.setText("0 itens / " + p.getQtdEstoque() + " disp.");
            try {
                imgView.setImage(new Image(getClass().getResourceAsStream(caminhoImg)));
            } catch (Exception e) {
                System.out.println("Erro ao carregar imagem de: " + p.getNome());
            }
        }
    }

    // Busca a variável de quantidade correta baseada no ID do produto
    private int getQtdPorId(int id) {
        if (id == 1) return qtdProduto1;
        if (id == 2) return qtdProduto2;
        if (id == 3) return qtdProduto3;
        if (id == 4) return qtdProduto4;
        if (id == 5) return qtdProduto5;
        if (id == 6) return qtdProduto6;
        return 0;
    }

    private void adicionarItem(Produto p, Label labelContador) {
        if (p == null) return;

        int qtdAtual = getQtdPorId(p.getId());

        // Impede que adicione mais itens do que o disponível no estoque
        if (qtdAtual >= p.getQtdEstoque()) {
            System.out.println("Estoque insuficiente para " + p.getNome());
            return;
        }

        int novaQtd = qtdAtual + 1;
        atualizarVariavelQtd(p.getId(), novaQtd);
        labelContador.setText(novaQtd + " itens / " + p.getQtdEstoque() + " disp.");
        atualizarSubtotalGeral();
    }

    private void removerItem(Produto p, Label labelContador) {
        if (p == null) return;

        int qtdAtual = getQtdPorId(p.getId());

        // Impede que a quantidade fique negativa
        if (qtdAtual <= 0) {
            return;
        }

        int novaQtd = qtdAtual - 1;
        atualizarVariavelQtd(p.getId(), novaQtd);
        labelContador.setText(novaQtd + " itens / " + p.getQtdEstoque() + " disp.");
        atualizarSubtotalGeral();
    }

    private void atualizarVariavelQtd(int id, int valor) {
        if (id == 1) qtdProduto1 = valor;
        else if (id == 2) qtdProduto2 = valor;
        else if (id == 3) qtdProduto3 = valor;
        else if (id == 4) qtdProduto4 = valor;
        else if (id == 5) qtdProduto5 = valor;
        else if (id == 6) qtdProduto6 = valor;
    }

    private void atualizarSubtotalGeral() {
        int totalItens = qtdProduto1 + qtdProduto2 + qtdProduto3 + qtdProduto4 + qtdProduto5 + qtdProduto6;
        double valorTotal = (qtdProduto1 * (prod1 != null ? prod1.getPreco() : 0)) +
                (qtdProduto2 * (prod2 != null ? prod2.getPreco() : 0)) +
                (qtdProduto3 * (prod3 != null ? prod3.getPreco() : 0)) +
                (qtdProduto4 * (prod4 != null ? prod4.getPreco() : 0)) +
                (qtdProduto5 * (prod5 != null ? prod5.getPreco() : 0)) +
                (qtdProduto6 * (prod6 != null ? prod6.getPreco() : 0));

        resumoCompra.setText(totalItens + " itens | Subtotal R$ " + String.format("%.2f", valorTotal));
    }

    private void voltarParaAtendente() {
        try {
            // Importante: Lembre-se de importar o javafx.fxml.FXMLLoader no topo do arquivo!
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