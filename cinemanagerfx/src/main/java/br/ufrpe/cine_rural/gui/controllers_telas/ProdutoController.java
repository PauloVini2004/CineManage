package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.dados.implemento.RepositorioProdutoImpl;
import br.ufrpe.cine_rural.dados.interfaces.IRepositorioProduto;
import br.ufrpe.cine_rural.model.loja.Produto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProdutoController {

    // Dependência do repositório
    private IRepositorioProduto repoProduto = new RepositorioProdutoImpl();

    @FXML private Button btnSubtotal;

    // Produto 1
    @FXML private Label nomeProduto1, precoProduto1, contadorProduto1;
    @FXML private ImageView imagemProduto1;
    @FXML private Button maisProduto1, menosProduto1;
    private int qtdProduto1 = 0;
    private Produto prod1;

    // Produto 2
    @FXML private Label nomeProduto2, precoProduto2, contadorProduto2;
    @FXML private ImageView imagemProduto2;
    @FXML private Button maisProduto2, menosProduto2;
    private int qtdProduto2 = 0;
    private Produto prod2;

    // Produto 3
    @FXML private Label nomeProduto3, precoProduto3, contadorProduto3;
    @FXML private ImageView imagemProduto3;
    @FXML private Button maisProduto3, menosProduto3;
    private int qtdProduto3 = 0;
    private Produto prod3;

    // Produto 4
    @FXML private Label nomeProduto4, precoProduto4, contadorProduto4;
    @FXML private ImageView imagemProduto4;
    @FXML private Button maisProduto4, menosProduto4;
    private int qtdProduto4 = 0;
    private Produto prod4;

    // Produto 5
    @FXML private Label nomeProduto5, precoProduto5, contadorProduto5;
    @FXML private ImageView imagemProduto5;
    @FXML private Button maisProduto5, menosProduto5;
    private int qtdProduto5 = 0;
    private Produto prod5;

    // Produto 6
    @FXML private Label nomeProduto6, precoProduto6, contadorProduto6;
    @FXML private ImageView imagemProduto6;
    @FXML private Button maisProduto6, menosProduto6;
    private int qtdProduto6 = 0;
    private Produto prod6;


    @FXML
    public void initialize() {

        inicializarEstoqueDoSistema();

        // Buscar os produtos salvos no repositório
        prod1 = repoProduto.buscar(1);
        prod2 = repoProduto.buscar(2);
        prod3 = repoProduto.buscar(3);
        prod4 = repoProduto.buscar(4);
        prod5 = repoProduto.buscar(5);
        prod6 = repoProduto.buscar(6);

        // Configurar os componentes visuais com os dados do repositório. OBS.: coloque imagens png ou jpg 3x4 e evite dor de cabeça
        configurarProdutoNaTela(prod1, nomeProduto1, precoProduto1, contadorProduto1, imagemProduto1, "/br/ufrpe/cine_rural/gui/imagens/Pipoca2.jpg");
        configurarProdutoNaTela(prod2, nomeProduto2, precoProduto2, contadorProduto2, imagemProduto2, "/br/ufrpe/cine_rural/gui/imagens/RefriCoca.jpg");
        configurarProdutoNaTela(prod3, nomeProduto3, precoProduto3, contadorProduto3, imagemProduto3, "/br/ufrpe/cine_rural/gui/imagens/RefriFanta.jpg");
        configurarProdutoNaTela(prod4, nomeProduto4, precoProduto4, contadorProduto4, imagemProduto4, "/br/ufrpe/cine_rural/gui/imagens/Guarana.jpg");
        configurarProdutoNaTela(prod5, nomeProduto5, precoProduto5, contadorProduto5, imagemProduto5, "/br/ufrpe/cine_rural/gui/imagens/RefriSprite.jpg");
        configurarProdutoNaTela(prod6, nomeProduto6, precoProduto6, contadorProduto6, imagemProduto6, "/br/ufrpe/cine_rural/gui/imagens/Hersheys.jpg");

        // Configurar ações dos botões
        maisProduto1.setOnAction(e -> adicionarItem(prod1, ++qtdProduto1, contadorProduto1));
        menosProduto1.setOnAction(e -> removerItem(prod1, --qtdProduto1, contadorProduto1));

        maisProduto2.setOnAction(e -> adicionarItem(prod2, ++qtdProduto2, contadorProduto2));
        menosProduto2.setOnAction(e -> removerItem(prod2, --qtdProduto2, contadorProduto2));

        maisProduto3.setOnAction(e -> adicionarItem(prod3, ++qtdProduto3, contadorProduto3));
        menosProduto3.setOnAction(e -> removerItem(prod3, --qtdProduto3, contadorProduto3));

        maisProduto4.setOnAction(e -> adicionarItem(prod4, ++qtdProduto4, contadorProduto4));
        menosProduto4.setOnAction(e -> removerItem(prod4, --qtdProduto4, contadorProduto4));

        maisProduto5.setOnAction(e -> adicionarItem(prod5, ++qtdProduto5, contadorProduto5));
        menosProduto5.setOnAction(e -> removerItem(prod5, --qtdProduto5, contadorProduto5));

        maisProduto6.setOnAction(e -> adicionarItem(prod6, ++qtdProduto6, contadorProduto6));
        menosProduto6.setOnAction(e -> removerItem(prod6, --qtdProduto6, contadorProduto6));

    }

     //popular repositório para testes
    private void inicializarEstoqueDoSistema() {
        repoProduto.cadastrar(new Produto(1, "Pipoca", 14.90, 50));     // 50 unidades no estoque
        repoProduto.cadastrar(new Produto(2, "Coca Cola", 10.50, 30));   // 30 unidades no estoque
        repoProduto.cadastrar(new Produto(3, "Refri Fanta", 9.50, 20));
        repoProduto.cadastrar(new Produto(4, "Guaraná", 9.50, 15));
        repoProduto.cadastrar(new Produto(5, "Sprite", 9.50, 40));
        repoProduto.cadastrar(new Produto(6, "Hershey's", 13.80, 10));
    }

    // configurar visualização
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

    // Adicionar item de acordo com o estoque
    private void adicionarItem(Produto p, int novaQtd, Label labelContador) {
        // Valida se a quantidade que o usuário quer colocar é maior do que tem no estoque do repositório
        if (novaQtd > p.getQtdEstoque()) {
            System.out.println("Estoque insuficiente para " + p.getNome());
            // Força a quantidade selecionada a travar no limite máximo do estoque
            atualizarVariavelQtd(p.getId(), p.getQtdEstoque());
            return;
        }

        atualizarVariavelQtd(p.getId(), novaQtd);
        labelContador.setText(novaQtd + " itens / " + p.getQtdEstoque() + " disp.");
        atualizarSubtotalGeral();
    }

    // Remover item selecionado
    private void removerItem(Produto p, int novaQtd, Label labelContador) {
        if (novaQtd < 0) novaQtd = 0;

        atualizarVariavelQtd(p.getId(), novaQtd);
        labelContador.setText(novaQtd + " itens / " + p.getQtdEstoque() + " disp.");
        atualizarSubtotalGeral();
    }

    // Altera o valor das variáveis locais de quantidade
    private void atualizarVariavelQtd(int id, int valor) {
        if (id == 1) qtdProduto1 = valor;
        else if (id == 2) qtdProduto2 = valor;
        else if (id == 3) qtdProduto3 = valor;
        else if (id == 4) qtdProduto4 = valor;
        else if (id == 5) qtdProduto5 = valor;
        else if (id == 6) qtdProduto6 = valor;

    }

    // Calcula o subtotal
    private void atualizarSubtotalGeral() {
        int totalItens = qtdProduto1 + qtdProduto2 + qtdProduto3 + qtdProduto4 + qtdProduto5 + qtdProduto6;

        double valorTotal = (qtdProduto1 * (prod1 != null ? prod1.getPreco() : 0)) + (qtdProduto2 * (prod2 != null ? prod2.getPreco() : 0)) + (qtdProduto3 * (prod3 != null ? prod3.getPreco() : 0)) + (qtdProduto4 * (prod4 != null ? prod4.getPreco() : 0)) + (qtdProduto5 * (prod5 != null ? prod5.getPreco() : 0)) + (qtdProduto6 * (prod6 != null ? prod6.getPreco() : 0));

        btnSubtotal.setText(totalItens + " itens | Subtotal R$ " + String.format("%.2f", valorTotal));
    }
}
