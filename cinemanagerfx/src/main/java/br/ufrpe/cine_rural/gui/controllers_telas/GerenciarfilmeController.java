package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.enums.ClassificacaoIndicativa;
import br.ufrpe.cine_rural.enums.Genero;
import br.ufrpe.cine_rural.model.Filme;
import br.ufrpe.cine_rural.negocios.FilmeNegocios;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class GerenciarfilmeController {

    private FilmeNegocios filmeNegocios;

    @FXML private TableView<Filme> tabelaFilmes;
    @FXML private TableColumn<Filme, String> colTitulo;
    @FXML private TableColumn<Filme, Genero> colGenero;
    @FXML private TableColumn<Filme, Integer> colDuracao;
    @FXML private TableColumn<Filme, ClassificacaoIndicativa> colClassificacao;
    @FXML private ImageView imgPoster;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtDuracao;
    @FXML private TextArea txtSinopse;
    @FXML private ComboBox<Genero> cbGenero;
    @FXML private ComboBox<ClassificacaoIndicativa> cbClassificacao;

    private File arquivoImagem;

    // FilmeNegocios é injetado de fora (quem cria a tela passa o objeto pronto)
    public void setNegocios(FilmeNegocios filmeNegocios) {
        this.filmeNegocios = filmeNegocios;
        atualizarTabela();
    }

    @FXML
    public void initialize() {
        cbGenero.getItems().addAll(Genero.values());
        cbClassificacao.getItems().addAll(ClassificacaoIndicativa.values());

        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colDuracao.setCellValueFactory(new PropertyValueFactory<>("duracao"));
        colClassificacao.setCellValueFactory(new PropertyValueFactory<>("classificacao"));
    }

    private void atualizarTabela() {
        if (tabelaFilmes == null) return;

        ObservableList<Filme> filmesObservaveis =
                FXCollections.observableArrayList(filmeNegocios.listarFilmes());
        tabelaFilmes.setItems(filmesObservaveis);
        tabelaFilmes.refresh();
    }

    @FXML
    public void selecionarImagem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Poster");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg")
        );

        arquivoImagem = fileChooser.showOpenDialog(null);

        if (arquivoImagem != null) {
            // Só a camada de UI mexe com Image — aqui está correto
            imgPoster.setImage(new Image(arquivoImagem.toURI().toString()));
        }
    }

    @FXML
    public void clicarBotao() {
        String titulo = txtTitulo.getText().trim();
        String sinopse = txtSinopse.getText().trim();
        Genero genero = cbGenero.getValue();
        ClassificacaoIndicativa classificacao = cbClassificacao.getValue();

        if (titulo.isEmpty()) {
            mostrarAlerta("Título obrigatório.");
            return;
        }
        if (genero == null) {
            mostrarAlerta("Selecione um gênero.");
            return;
        }
        if (classificacao == null) {
            mostrarAlerta("Selecione uma classificação.");
            return;
        }

        int duracao;
        try {
            duracao = Integer.parseInt(txtDuracao.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("Duração inválida. Digite apenas números.");
            return;
        }

        String posterPath = (arquivoImagem != null) ? arquivoImagem.toURI().toString() : null;

        try {
            filmeNegocios.cadastrarFilme(titulo, sinopse, duracao, genero, classificacao, posterPath);
            mostrarAlerta("Filme \"" + titulo + "\" cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Erro: " + e.getMessage());
            return;
        }

        atualizarTabela();
        limparCampos();
    }

    private void limparCampos() {
        txtTitulo.clear();
        txtSinopse.clear();
        txtDuracao.clear();
        cbGenero.setValue(null);
        cbClassificacao.setValue(null);
        imgPoster.setImage(null);
        arquivoImagem = null;
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}