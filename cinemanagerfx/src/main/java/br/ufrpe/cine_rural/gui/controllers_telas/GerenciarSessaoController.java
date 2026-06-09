package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.dados.implemento.RepositorioSessaoImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSalaImpl;
import br.ufrpe.cine_rural.enums.Idioma;
import br.ufrpe.cine_rural.enums.StatusSessao;
import br.ufrpe.cine_rural.model.Filme;
import br.ufrpe.cine_rural.model.Sessao;
import br.ufrpe.cine_rural.model.tiposala.Sala;

import br.ufrpe.cine_rural.negocios.FilmeNegocios;
import br.ufrpe.cine_rural.negocios.SessaoNegocios;
import br.ufrpe.cine_rural.negocios.SalaNegocios;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;

public class GerenciarSessaoController {

    private FilmeNegocios negociosFilmes;
    private SessaoNegocios negociosSessaos;
    private SalaNegocios negociosSalas;

    @FXML
    private TableView<Sessao> tabelaSessoes;

    @FXML
    private TableColumn<Sessao, String> colFilme;

    @FXML
    private TableColumn<Sessao, String> colSala;

    @FXML
    private TableColumn<Sessao, LocalDateTime> colHorario;

    @FXML
    private TableColumn<Sessao, Idioma> colIdioma;

    @FXML
    private TableColumn<Sessao, StatusSessao> colStatus;

    @FXML
    private ComboBox<Filme> cbFilme;

    @FXML
    private ComboBox<Sala> cbSala;

    @FXML
    private ComboBox<Idioma> cbIdioma;

    @FXML
    private ComboBox<StatusSessao> cbStatus;

    @FXML
    private DatePicker dpData;

    @FXML
    private TextField txtHora;

    private ObservableList<Sessao> sessoes = FXCollections.observableArrayList();

    public void setRepositorios(FilmeNegocios negociosFilmes,
                                SessaoNegocios negociosSessaos,
                                SalaNegocios negociosSalas) {

        this.negociosFilmes = negociosFilmes;
        this.negociosSessaos = negociosSessaos;
        this.negociosSalas = negociosSalas;

        carregarCombos();
        atualizarTabela();
    }

    @FXML
    public void initialize() {

        cbIdioma.getItems().addAll(Idioma.values());
        cbStatus.getItems().addAll(StatusSessao.values());

        colFilme.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getFilme().getTitulo()
                )
        );

        colSala.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getSala().toString()
                )
        );

        colHorario.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("horario")
        );

        colIdioma.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("idioma")
        );

        colStatus.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("status")
        );

        tabelaSessoes.setItems(sessoes);
    }

    @FXML
    public void cadastrarSessao() {

        try {

            Filme filme = cbFilme.getValue();
            Sala sala = cbSala.getValue();
            Idioma idioma = cbIdioma.getValue();
            StatusSessao status = cbStatus.getValue();

            String[] horarioSeparado = txtHora.getText().split(":");

            int hora = Integer.parseInt(horarioSeparado[0]);
            int minuto = Integer.parseInt(horarioSeparado[1]);

            LocalDateTime horario = dpData.getValue().atTime(hora, minuto);

            negociosSessaos.cadastrarSessao(filme,
                    sala,
                    horario,
                    idioma);

            atualizarTabela();
            limparCampos();

        } catch (Exception e) {
            System.out.println("Preencha os campos corretamente.");
        }
    }

    private void limparCampos() {
        cbFilme.setValue(null);
        cbSala.setValue(null);
        cbIdioma.setValue(null);
        cbStatus.setValue(null);
        dpData.setValue(null);
        txtHora.clear();
    }

    private void atualizarTabela() {
        sessoes.setAll(negociosSessaos.listarSessoes());
    }

    private void carregarCombos() {
        cbFilme.getItems().setAll(negociosFilmes.listarFilmes());
        cbSala.getItems().setAll(negociosSalas.listarSalas());
    }
}