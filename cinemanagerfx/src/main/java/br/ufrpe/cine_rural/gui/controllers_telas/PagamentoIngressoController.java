package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.dados.implemento.RepositorioVendaIngressoImpl;
import br.ufrpe.cine_rural.model.Cliente;
import br.ufrpe.cine_rural.model.Ingresso;
import br.ufrpe.cine_rural.model.VendaIngresso;
import br.ufrpe.cine_rural.model.loja.ItemVenda;
import br.ufrpe.cine_rural.util.EnviadorEmail;
import br.ufrpe.cine_rural.util.GeradorPDF;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Map;

import static br.ufrpe.cine_rural.util.EnviadorEmail.enviarConfirmacaoIngresso;

public class PagamentoIngressoController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtCpf;
    @FXML private ComboBox<String> comboPagamento;
    @FXML private TextArea txtResumoCompra;
    @FXML private Label lblTotal;
    @FXML private Button btnVoltar;
    @FXML private Button btnPagar;

    private ArrayList<Ingresso> ingressos;
    private Map<String, Integer> idadesPorAssento;
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
            ArrayList<Ingresso> ingressos,
            Map<String, Integer> idadesPorAssento
    ) {
        this.ingressos        = ingressos;
        this.idadesPorAssento = idadesPorAssento;

        atualizarResumo();
        btnPagar.setDisable(ingressos == null || ingressos.isEmpty());
    }

    private void atualizarResumo() {
        if (ingressos == null || ingressos.isEmpty()) return;

        StringBuilder resumo = new StringBuilder();
        total = 0;

        resumo.append("===============================\n");
        resumo.append("       CINEMA RURAL - RESUMO    \n");
        resumo.append("===============================\n");
        resumo.append("Filme: ")
                .append(ingressos.get(0).getSessao().getFilme().getTitulo())
                .append("\n");
        resumo.append("Sala: ")
                .append(ingressos.get(0).getSessao().getSala().toString())
                .append("\n");
        resumo.append("Horário: ")
                .append(ingressos.get(0).getSessao().getHorario().toString())
                .append("\n");
        resumo.append("-------------------------------\n");
        resumo.append("Assentos escolhidos:\n");

        for (Ingresso ingresso : ingressos) {
            resumo.append(" -> ").append(ingresso.getAssento().toString());
            resumo.append(" | Valor: R$ ")
                    .append(String.format("%.2f", ingresso.getPreco()))
                    .append("\n");
            total += ingresso.getPreco();
        }

        resumo.append("-------------------------------");

        txtResumoCompra.setText(resumo.toString());
        lblTotal.setText("TOTAL R$ " + String.format("%.2f", total));
    }


    // Pagamento
    private void realizarPagamento() {

        if (ingressos == null || ingressos.isEmpty()) {
            mostrarErro("Selecione pelo menos um assento antes de realizar o pagamento.");
            return;
        }

        // Validações dos campos
        String nome  = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String cpf   = txtCpf.getText().trim();

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
        if (comboPagamento.getValue() == null) {
            mostrarErro("Selecione uma forma de pagamento.");
            return;
        }

        try {

            // Associa cliente a cada ingresso usando a idade informada
            for (Ingresso ingresso : ingressos) {

                String nomeAssento = ingresso.getAssento().getCodigo();

                int idadeCliente = (idadesPorAssento != null
                        && idadesPorAssento.containsKey(nomeAssento))
                        ? idadesPorAssento.get(nomeAssento)
                        : 0;

                Cliente cliente = new Cliente(nome, cpf, idadeCliente, email);

                ingresso.setCliente(cliente);
            }

            // Marca assentos como ocupados
            ArrayList<String> codigosVendidos = new ArrayList<>();
            for (Ingresso ingresso : ingressos) {
                codigosVendidos.add(ingresso.getAssento().getCodigo());
            }

            AssentoController.ocuparAssentos(
                    ingressos.get(0).getSessao().getHorario(),
                    codigosVendidos
            );

            // Persiste venda
            VendaIngresso venda = new VendaIngresso(ingressos);
            venda.setFormaPagamento(comboPagamento.getValue());

            System.out.println("ANTES DE SALVAR");
            RepositorioVendaIngressoImpl.getInstancia().cadastrar(venda);
            System.out.println("DEPOIS DE SALVAR");

            // PDFs
            try {
                GeradorPDF.gerarNotaFiscalIngresso(venda);
                for (Ingresso ingresso : ingressos) {
                    GeradorPDF.gerarIngresso(ingresso);
                }
            } catch (Exception pdfEx) {
                System.err.println("Aviso PDF — " + pdfEx.getMessage());
            }

            // ALERTA
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pagamento");
            alert.setHeaderText(null);
            alert.setContentText("Ingresso emitido com sucesso.");
            alert.showAndWait();

            try {

                Cliente clienteEmail = ingressos.get(0).getCliente();

                EnviadorEmail.enviarConfirmacaoIngresso(
                        clienteEmail,
                        ingressos,
                        total
                );

            } catch (Exception e) {
                System.out.println("Erro ao enviar e-mail: " + e.getMessage());
                e.printStackTrace();
            }

            limparCampos();
            navegarParaAtendente();

        } catch (Exception e) {
            e.printStackTrace();
            String msg = (e.getMessage() != null) ? e.getMessage() : e.getClass().getSimpleName();
            mostrarErro("Erro ao processar pagamento: " + msg);
        }
    }


    // Navegação
    // Volta para a tela de assentos usando a cena salva antes de navegar para cá.
    private void voltarParaAssento() {
        try {
            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            stage.setScene(PagamentoIngressoController.cenaAnterior);
            stage.show();
        } catch (Exception e) {
            System.err.println("Erro ao voltar para assentos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * Navega para a tela do Atendente após o pagamento.
     * Tenta o ScreenManager primeiro; caso o mainStage não esteja disponível,
     * carrega a tela diretamente no stage atual.
     */
    private void navegarParaAtendente() {
        // Tenta usar o ScreenManager (caminho normal)
        try {
            ScreenManager sm = ScreenManager.getInstance();
            if (sm != null) {
                sm.showAtendenteScreen();
                return;
            }
        } catch (Exception e) {
            System.err.println("ScreenManager indisponível, carregando Atendente diretamente.");
        }

        // Fallback: carrega a tela do atendente diretamente no stage atual
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/ufrpe/cine_rural/gui/Atendente-View.fxml")
            );
            Parent root = loader.load();
            Stage stage = (Stage) btnPagar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            System.err.println("Erro ao carregar tela do Atendente: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    // Utilitários

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void limparCampos() {
        txtNome.clear();
        txtEmail.clear();
        txtCpf.clear();
        comboPagamento.getSelectionModel().clearSelection();
        txtResumoCompra.clear();
        lblTotal.setText("TOTAL R$ 0,00");
        ingressos = new ArrayList<>();
        btnPagar.setDisable(true);
        total = 0;
    }

}