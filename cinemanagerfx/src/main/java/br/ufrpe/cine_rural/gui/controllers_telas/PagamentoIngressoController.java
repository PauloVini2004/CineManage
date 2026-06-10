package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.gui.controllers_telas.AssentoController;
import br.ufrpe.cine_rural.model.Cliente;
import br.ufrpe.cine_rural.model.Ingresso;
import br.ufrpe.cine_rural.model.VendaIngresso;
import br.ufrpe.cine_rural.dados.implemento.RepositorioVendaIngressoImpl;

import br.ufrpe.cine_rural.util.GeradorPDF;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.ArrayList;

public class PagamentoIngressoController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtCpf;
    @FXML private TextField txtIdade;
    @FXML private ComboBox<String> comboPagamento;
    @FXML private TextArea txtResumoCompra;
    @FXML private Label lblTotal;
    @FXML private Button btnVoltar;
    @FXML private Button btnPagar;

    private ArrayList<Ingresso> ingressos;
    private double total;
    public static Scene cenaAnterior;

    @FXML
    public void initialize() {
        comboPagamento.setItems(FXCollections.observableArrayList("PIX", "Cartão", "Dinheiro"));
        btnPagar.setDisable(true);

        btnPagar.setOnAction(e -> realizarPagamento());
        btnVoltar.setOnAction(e -> voltarParaAssento());
    }

        public void receberIngressos(
                ArrayList<Ingresso> ingressos
        ) {

            this.ingressos = ingressos;

            atualizarResumo();

            btnPagar.setDisable(
                    ingressos == null ||
                            ingressos.isEmpty()
            );
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
        lblTotal.setText("TOTAL R$ " + String.format("%.2f", total));
    }

    private void realizarPagamento() {

        if (ingressos == null || ingressos.isEmpty()) {

            mostrarErro(
                    "Selecione pelo menos um assento antes de realizar o pagamento."
            );

            return;
        }

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
            if (txtNome.getText().isEmpty() || txtCpf.getText().isEmpty() || txtIdade.getText().isEmpty()) {
                mostrarErro("Preencha as informações do Cliente.");
                return;
            }

            Cliente cliente =
                    new Cliente(
                            nome,
                            cpf,
                            idade,
                            email
                    );

            if (comboPagamento.getValue() == null) {
                mostrarErro("Selecione uma forma de pagamento.");
                return;
            }

            ArrayList<String> assentosVendidos =
                    new ArrayList<>();

            for (Ingresso ingresso : ingressos) {
                assentosVendidos.add(
                        ingresso.getAssento().toString()
                );
            }

            AssentoController.ocuparAssentos(
                    ingressos.get(0)
                            .getSessao()
                            .getHorario(),
                    assentosVendidos
            );

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
            limparCampos();

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

            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            stage.setScene(
                    PagamentoIngressoController.cenaAnterior
            );
            stage.setTitle("Assentos");
            stage.show();
        } catch (Exception e) {
            System.out.println("Erro ao carregar a visualização de assentos.");
            e.printStackTrace();
        }
    }

    private void limparCampos() {

        txtNome.clear();
        txtEmail.clear();
        txtCpf.clear();
        txtIdade.clear();

        comboPagamento.getSelectionModel().clearSelection();

        txtResumoCompra.clear();

        lblTotal.setText("TOTAL R$ 0,00");

        ingressos = new ArrayList<>();

        btnPagar.setDisable(true);

        total = 0;
    }
}
//REQ19: Não permitir a venda de ingressos para menores de idade sem acompanhante caso a classificação seja restritiva.