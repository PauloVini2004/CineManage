package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSalaImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSessaoImpl;
import br.ufrpe.cine_rural.enums.Idioma;
import br.ufrpe.cine_rural.enums.StatusSessao;
import br.ufrpe.cine_rural.model.Filme;
import br.ufrpe.cine_rural.model.Sessao;
import br.ufrpe.cine_rural.model.tiposala.Sala;
import br.ufrpe.cine_rural.negocios.FilmeNegocios;
import br.ufrpe.cine_rural.negocios.SalaNegocios;
import br.ufrpe.cine_rural.negocios.SessaoNegocios;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDateTime;


public class GerenciarSessaoController {

    @FXML private Button btnSair;
    @FXML private Button btnEditar;
    @FXML private Button btnRemover;
    @FXML private Button btnCadastrar;

    private final SalaNegocios salaNegocios =
            new SalaNegocios(RepositorioSalaImpl.getInstancia());
    private final SessaoNegocios sessaoNegocios =
            new SessaoNegocios(RepositorioSessaoImpl.getInstancia());
    private final FilmeNegocios filmeNegocios =
            new FilmeNegocios(RepositorioFilmeImpl.getInstancia(), sessaoNegocios);

    @FXML private TableView<Sessao> tabelaSessoes;
    @FXML private TableColumn<Sessao, String> colFilme;
    @FXML private TableColumn<Sessao, String> colSala;
    @FXML private TableColumn<Sessao, LocalDateTime> colHorario;
    @FXML private TableColumn<Sessao, Idioma> colIdioma;
    @FXML private TableColumn<Sessao, StatusSessao> colStatus;

    @FXML private ComboBox<Filme> cbFilme;
    @FXML private ComboBox<Sala> cbSala;
    @FXML private ComboBox<Idioma> cbIdioma;
    @FXML private ComboBox<StatusSessao> cbStatus;
    @FXML private DatePicker dpData;
    @FXML private TextField txtHora;

    private final ObservableList<Sessao> sessoes = FXCollections.observableArrayList();

    // Sessão sendo editada (null = modo cadastro)
    private Sessao sessaoEmEdicao = null;

    @FXML
    public void initialize() {

        cbIdioma.getItems().setAll(Idioma.values());
        cbStatus.getItems().setAll(StatusSessao.values());

        // Cell factory para Sala
        javafx.util.Callback<ListView<Sala>, ListCell<Sala>> salaFactory =
            lv -> new ListCell<>() {
                @Override protected void updateItem(Sala item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.toString());
                }
            };
        cbSala.setCellFactory(salaFactory);
        cbSala.setButtonCell(salaFactory.call(null));

        // Cell factory para Filme
        javafx.util.Callback<ListView<Filme>, ListCell<Filme>> filmeFactory =
            lv -> new ListCell<>() {
                @Override protected void updateItem(Filme item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getTitulo());
                }
            };
        cbFilme.setCellFactory(filmeFactory);
        cbFilme.setButtonCell(filmeFactory.call(null));

        colFilme.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getFilme().getTitulo()
                )
        );
        colSala.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getSala().toString()
                )
        );
        colHorario.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("horario"));
        colIdioma.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("idioma"));
        colStatus.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));

        tabelaSessoes.setItems(sessoes);

        // Habilita/desabilita Editar e Remover conforme seleção
        btnEditar.setDisable(true);
        btnRemover.setDisable(true);
        tabelaSessoes.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> {
                boolean temSelecao = (novo != null);
                btnEditar.setDisable(!temSelecao);
                btnRemover.setDisable(!temSelecao);
            }
        );

        carregarCombos();
        atualizarTabela();
    }

    @FXML
    public void cadastrarSessao() {
        try {
            Filme filme = cbFilme.getValue();
            Sala sala   = cbSala.getValue();
            Idioma idioma = cbIdioma.getValue();
            StatusSessao status = cbStatus.getValue();

            if (filme == null || sala == null || idioma == null) {
                mostrarErro("Preencha todos os campos obrigatórios (Filme, Sala, Idioma).");
                return;
            }
            if (dpData.getValue() == null || txtHora.getText().isBlank()) {
                mostrarErro("Informe a data e o horário da sessão.");
                return;
            }

            String[] horaSplit = txtHora.getText().split(":");
            int hora   = Integer.parseInt(horaSplit[0]);
            int minuto = Integer.parseInt(horaSplit[1]);
            LocalDateTime horario = dpData.getValue().atTime(hora, minuto);

            if (sessaoEmEdicao != null) {
                // MODO EDIÇÃO
                // Remove a antiga e cadastra com os novos dados
                sessaoNegocios.removerSessao(sessaoEmEdicao.getHorario());

                StatusSessao novoStatus = (status != null) ? status : StatusSessao.ABERTA;
                Sessao sessaoAtualizada = new Sessao(filme, sala, horario, idioma, novoStatus);
                RepositorioSessaoImpl.getInstancia().cadastrar(sessaoAtualizada);

                mostrarInfo("Sessão atualizada com sucesso!");
                cancelarEdicao();
            } else {
                // MODO CADASTRO
                sessaoNegocios.cadastrarSessao(filme, sala, horario, idioma);
                mostrarInfo("Sessão cadastrada com sucesso!");
                limparCampos();
            }

            atualizarTabela();

        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarErro(e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro: verifique se os campos estão preenchidos corretamente.");
        }
    }

    // Carrega a sessão selecionada no formulário para edição.
    @FXML
    public void editarSessao() {
        Sessao selecionada = tabelaSessoes.getSelectionModel().getSelectedItem();
        if (selecionada == null) return;

        sessaoEmEdicao = selecionada;

        cbFilme.setValue(selecionada.getFilme());
        cbSala.setValue(selecionada.getSala());
        cbIdioma.setValue(selecionada.getIdioma());
        cbStatus.setValue(selecionada.getStatus());
        dpData.setValue(selecionada.getHorario().toLocalDate());
        txtHora.setText(
            String.format("%02d:%02d",
                selecionada.getHorario().getHour(),
                selecionada.getHorario().getMinute())
        );

        // Troca texto do botão para modo edição
        btnCadastrar.setText("Salvar Edição");
    }

    // Remove a sessão selecionada após confirmação.
    @FXML
    public void removerSessao() {
        Sessao selecionada = tabelaSessoes.getSelectionModel().getSelectedItem();
        if (selecionada == null) return;

        if (!selecionada.getIngressos().isEmpty()) {
            mostrarErro("Não é possível remover uma sessão que já possui ingressos vendidos.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Remoção");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText(
            "Deseja remover a sessão de \"" + selecionada.getFilme().getTitulo()
            + "\" às " + selecionada.getHorario() + "?"
        );

        confirmacao.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.OK) {
                sessaoNegocios.removerSessao(selecionada.getHorario());
                atualizarTabela();
                cancelarEdicao();
                mostrarInfo("Sessão removida com sucesso.");
            }
        });
    }

    private void cancelarEdicao() {
        sessaoEmEdicao = null;
        btnCadastrar.setText("Adicionar Sessão");
        limparCampos();
        tabelaSessoes.getSelectionModel().clearSelection();
    }

    private void carregarCombos() {
        cbFilme.getItems().setAll(filmeNegocios.listarFilmes());
        cbSala.getItems().setAll(salaNegocios.listarSalas());
    }

    private void atualizarTabela() {
        sessoes.setAll(sessaoNegocios.listarSessoes());
    }

    private void limparCampos() {
        cbFilme.setValue(null);
        cbSala.setValue(null);
        cbIdioma.setValue(null);
        cbStatus.setValue(null);
        dpData.setValue(null);
        txtHora.clear();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    public void voltar() {
        ScreenManager.getInstance().showGerenteScreen();
    }
}
