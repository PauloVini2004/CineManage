package br.ufrpe.cine_rural.gui.controllers_telas.emergencia;

import br.ufrpe.cine_rural.dados.implemento.RepositorioProdutoImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioVendaLojinhaImpl;
import br.ufrpe.cine_rural.model.Cliente;
import br.ufrpe.cine_rural.model.loja.ItemVenda;

import br.ufrpe.cine_rural.model.loja.VendaLojinha;
import br.ufrpe.cine_rural.util.GeradorPDF;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class PagamentoProdutoController {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtCpf;

    @FXML
    private TextField txtIdade;

    @FXML
    private ComboBox<String> comboPagamento;

    @FXML
    private TextArea txtResumoCompra;

    @FXML
    private Label lblTotal;

    @FXML
    private Button btnVoltar;

    @FXML
    private Button btnPagar;

    private ArrayList<ItemVenda> itensVenda;

    private double total;

    @FXML
    public void initialize() {

        comboPagamento.setItems(
                FXCollections.observableArrayList(
                        "PIX",
                        "Cartão",
                        "Dinheiro"
                )
        );

        btnPagar.setOnAction(
                e -> realizarPagamento()
        );

        btnVoltar.setOnAction(
                e -> voltarParaProduto()
        );


    }

    public void receberItensVenda(
            ArrayList<ItemVenda> itens
    ) {
        System.out.println("Recebi " + itens.size() + " itens");

        this.itensVenda = itens;

        atualizarResumo();

    }

    private void atualizarResumo() {

        if (itensVenda == null)
            return;

        StringBuilder resumo =
                new StringBuilder();

        total = 0;

        for (ItemVenda item : itensVenda) {

            resumo.append(
                    item.getProduto().getNome()
            );

            resumo.append(
                    " x"
            );

            resumo.append(
                    item.getQuantidade()
            );

            resumo.append(
                    " - R$ "
            );

            resumo.append(
                    String.format(
                            "%.2f",
                            item.getSubtotal()
                    )
            );

            resumo.append("\n");

            System.out.println(
                    item.getProduto().getNome()
                            + " qtd=" + item.getQuantidade()
                            + " subtotal=" + item.getSubtotal()
            );

            total += item.getSubtotal();
        }

        txtResumoCompra.setText(
                resumo.toString()
        );

        lblTotal.setText(
                "TOTAL R$ "
                        + String.format(
                        "%.2f",
                        total
                )
        );
        System.out.println("TOTAL = " + total);
    }

    private void realizarPagamento() {


        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String cpf = txtCpf.getText().trim();
        String idadeTexto = txtIdade.getText().trim();

        if (nome.isEmpty()) {
            mostrarErro("Informe o nome do cliente.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarErro("E-mail inválido.");
            return;
        }

        if (!cpf.matches("\\d{11}")) {
            mostrarErro("CPF deve conter exatamente 11 números.");
            return;
        }

        if (!idadeTexto.matches("\\d+")) {
            mostrarErro("Idade deve conter apenas números.");
            return;
        }

        int idade = Integer.parseInt(idadeTexto);

        if (idade < 0 || idade > 120) {
            mostrarErro("Idade inválida.");
            return;
        }

        try {

            Cliente cliente =
                    new Cliente(
                            nome,
                            cpf,
                            idade,
                            email
                    );

            if (comboPagamento.getValue() == null) {

                mostrarErro(
                        "Selecione uma forma de pagamento."
                );

                return;
            }

            GeradorPDF.gerarNotaFiscalProduto(
                    cliente,
                    itensVenda,
                    total
            );

            VendaLojinha venda =
                    new VendaLojinha(itensVenda);

            venda.setCliente(cliente);

            venda.setFormaPagamento(
                    comboPagamento.getValue()
            );

            venda.setDataVenda(
                    java.time.LocalDateTime.now()
            );

            RepositorioProdutoImpl repositorioProduto =
                    RepositorioProdutoImpl.getInstancia();


            RepositorioVendaLojinhaImpl repositorio =
                    new RepositorioVendaLojinhaImpl();

            for (ItemVenda item : itensVenda) {

                item.getProduto().reduzirEstoque(
                        item.getQuantidade()
                );

                repositorioProduto.atualizar(
                        item.getProduto()
                );

                System.out.println(
                        item.getProduto().getNome()
                                + " estoque restante = "
                                + item.getProduto().getQtdEstoque()
                );
            }

            repositorio.cadastrar(venda);


            Alert alert =
                    new Alert(
                            Alert.AlertType.INFORMATION
                    );

            alert.setTitle(
                    "Pagamento"
            );

            alert.setHeaderText(
                    null
            );

            alert.setContentText(
                    "Pagamento realizado com sucesso."
            );

            alert.showAndWait();

        } catch (NumberFormatException e) {

            mostrarErro(
                    "Idade inválida."
            );
        }

    }

    private void mostrarErro(
            String mensagem
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                mensagem
        );

        alert.showAndWait();
    }

    private void voltarParaProduto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/ufrpe/cine_rural/gui/TelasProduto/Produto.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.out.println("Erro ao tentar voltar para a tela de Produto. Verifique o caminho do FXML.");
            e.printStackTrace();
        }
    }
}
