package br.ufrpe.cine_rural.gui.controllers_telas.emergencia;

import br.ufrpe.cine_rural.model.Cliente;
import br.ufrpe.cine_rural.model.Ingresso;
import br.ufrpe.cine_rural.model.VendaIngresso;
import br.ufrpe.cine_rural.dados.implemento.RepositorioVendaIngressoImpl;
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

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PagamentoIngressoController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtCpf;
    @FXML private TextField txtIdade;
    @FXML private ComboBox<String> comboMeiaEntrada;
    @FXML private ComboBox<String> comboPagamento;
    @FXML private TextArea txtResumoCompra;
    @FXML private Label lblTotal;
    @FXML private Button btnVoltar;
    @FXML private Button btnPagar;

    private ArrayList<Ingresso> ingressos;
    private double total;

    @FXML
    public void initialize() {
        comboPagamento.setItems(FXCollections.observableArrayList("PIX", "Cartão", "Dinheiro"));
        comboMeiaEntrada.setItems(FXCollections.observableArrayList("Sim", "Não"));

        btnPagar.setOnAction(e -> realizarPagamento());
        btnVoltar.setOnAction(e -> voltarParaAssento());
    }


    public void receberIngressos(ArrayList<Ingresso> ingressos) {
        this.ingressos = ingressos;
        atualizarResumo();
    }

    private void atualizarResumo() {
        if (ingressos == null || ingressos.isEmpty()) return;

        StringBuilder resumo = new StringBuilder();
        total = 0;


        resumo.append("===============================\n");
        resumo.append("       CINEMA RURAL - RESUMO    \n");
        resumo.append("===============================\n");
        resumo.append("Filme: ").append(ingressos.get(0).getSessao().getFilme().getTitulo()).append("\n");
        resumo.append("Sala: ").append(ingressos.get(0).getSessao().getSala().toString()).append("\n");
        resumo.append("Horário: ").append(ingressos.get(0).getSessao().getHorario().toString()).append("\n");
        resumo.append("-------------------------------\n");
        resumo.append("Assentos escolhidos:\n");

        for (Ingresso ingresso : ingressos) {

            resumo.append(" -> ").append(ingresso.getAssento().toString());
            resumo.append(" | Valor: R$ ").append(String.format("%.2f", ingresso.getPreco())).append("\n");

            total += ingresso.getPreco();
        }

        resumo.append("-------------------------------");

        txtResumoCompra.setText(resumo.toString());
        lblTotal.setText("Total R$ " + String.format("%.2f", total));
    }

    private void realizarPagamento() {
        try {
            if (txtNome.getText().isEmpty() || txtCpf.getText().isEmpty() || txtIdade.getText().isEmpty()) {
                mostrarErro("Preencha as informações do Cliente.");
                return;
            }

            Cliente cliente = new Cliente(
                    txtNome.getText(),
                    txtCpf.getText(),
                    Integer.parseInt(txtIdade.getText()),
                    txtEmail.getText()
            );

            if (comboPagamento.getValue() == null) {
                mostrarErro("Selecione uma forma de pagamento.");
                return;
            }

            for (Ingresso ingresso : ingressos) {
                ingresso.setCliente(cliente);
            }

            VendaIngresso venda = new VendaIngresso(ingressos);
            venda.setFormaPagamento(comboPagamento.getValue());

            RepositorioVendaIngressoImpl repositorio =
                    RepositorioVendaIngressoImpl.getInstancia();

            System.out.println("ANTES DE SALVAR");
            repositorio.cadastrar(venda);
            System.out.println("DEPOIS DE SALVAR");

            GeradorPDF.gerarNotaFiscalIngresso(venda);

            for (Ingresso ingresso : ingressos) {
                GeradorPDF.gerarIngresso(ingresso);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pagamento");
            alert.setHeaderText(null);
            alert.setContentText("Ingresso emitido com sucesso.");
            alert.showAndWait();

        } catch (NumberFormatException e) {
            mostrarErro("Idade inválida.");
        }
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void voltarParaAssento() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/ufrpe/cine_rural/gui/Assentos.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Assentos");
            stage.show();
        } catch (Exception e) {
            System.out.println("Erro ao carregar a visualização de assentos.");
            e.printStackTrace();
        }
    }
}