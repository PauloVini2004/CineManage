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

    private final SalaNegocios salaNegocios =
            new SalaNegocios (
                    RepositorioSalaImpl.getInstancia()
            );
    private final SessaoNegocios sessaoNegocios =
            new SessaoNegocios(
                    RepositorioSessaoImpl.getInstancia()
            );
    private final FilmeNegocios filmeNegocios =
            new FilmeNegocios(
                    RepositorioFilmeImpl.getInstancia(), sessaoNegocios
            );;

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

    @FXML
    public void initialize() {

        cbIdioma.getItems().setAll(Idioma.values());
        cbStatus.getItems().setAll(StatusSessao.values());

        // Exibe o tipo real da sala (Comum, Imax, Vip) + número no ComboBox
        javafx.util.Callback<javafx.scene.control.ListView<Sala>, javafx.scene.control.ListCell<Sala>> salaFactory =
            lv -> new javafx.scene.control.ListCell<>() {
                @Override protected void updateItem(Sala item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.toString());
                }
            };
        cbSala.setCellFactory(salaFactory);
        cbSala.setButtonCell(salaFactory.call(null));

        // Exibe o título do filme no ComboBox
        javafx.util.Callback<javafx.scene.control.ListView<Filme>, javafx.scene.control.ListCell<Filme>> filmeFactory =
            lv -> new javafx.scene.control.ListCell<>() {
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

        carregarCombos();
        atualizarTabela();
    }

    @FXML
    public void cadastrarSessao() {

        try {
            Filme filme = cbFilme.getValue();
            Sala sala = cbSala.getValue();
            Idioma idioma = cbIdioma.getValue();

            String[] horaSplit = txtHora.getText().split(":");
            int hora = Integer.parseInt(horaSplit[0]);
            int minuto = Integer.parseInt(horaSplit[1]);

            LocalDateTime horario = dpData.getValue().atTime(hora, minuto);

            sessaoNegocios.cadastrarSessao(filme, sala, horario, idioma);

            atualizarTabela();
            limparCampos();

        } catch (Exception e) {
            System.out.println("Erro: preencha os campos corretamente.");
        }
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

    @FXML
    public void voltar() {
        ScreenManager.getInstance().showGerenteScreen();
    }
}