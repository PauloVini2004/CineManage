package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSessaoImpl;
import br.ufrpe.cine_rural.enums.ClassificacaoIndicativa;
import br.ufrpe.cine_rural.enums.Genero;
import br.ufrpe.cine_rural.model.Filme;
import br.ufrpe.cine_rural.negocios.FilmeNegocios;
import br.ufrpe.cine_rural.negocios.SessaoNegocios;
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
    @FXML private Button btnSair;
    @FXML private Button btnAdicionarFilme;
    @FXML private Button btnEditar;
    @FXML private Button btnRemover;

    private File arquivoImagem;
    // Filme sendo editado (null = modo cadastro)
    private Filme filmeEmEdicao = null;

    private final SessaoNegocios sessaoNegocios =
            new SessaoNegocios(RepositorioSessaoImpl.getInstancia());
    private final FilmeNegocios filmeNegocios =
            new FilmeNegocios(RepositorioFilmeImpl.getInstancia(), sessaoNegocios);

    @FXML
    public void initialize() {
        cbGenero.getItems().addAll(Genero.values());
        cbClassificacao.getItems().addAll(ClassificacaoIndicativa.values());

        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colDuracao.setCellValueFactory(new PropertyValueFactory<>("duracao"));
        colClassificacao.setCellValueFactory(new PropertyValueFactory<>("classificacao"));

        // Habilita/desabilita Editar e Remover conforme seleção na tabela
        btnEditar.setDisable(true);
        btnRemover.setDisable(true);
        tabelaFilmes.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> {
                boolean temSelecao = (novo != null);
                btnEditar.setDisable(!temSelecao);
                btnRemover.setDisable(!temSelecao);
            }
        );

        atualizarTabela();
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
            imgPoster.setImage(new Image(arquivoImagem.toURI().toString()));
        }
    }

    // Chamado pelo botão "Editar": carrega o filme selecionado no formulário.
    @FXML
    public void editarFilme() {
        Filme selecionado = tabelaFilmes.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        filmeEmEdicao = selecionado;

        txtTitulo.setText(selecionado.getTitulo());
        txtSinopse.setText(selecionado.getSinopse());
        txtDuracao.setText(String.valueOf(selecionado.getDuracao()));
        cbGenero.setValue(selecionado.getGenero());
        cbClassificacao.setValue(selecionado.getClassificacao());

        if (selecionado.getCaminhoPoster() != null && !selecionado.getCaminhoPoster().isBlank()) {
            try {
                imgPoster.setImage(new Image(selecionado.getCaminhoPoster()));
            } catch (Exception ignored) {}
        }

        // Troca o texto do botão para indicar modo edição
        btnAdicionarFilme.setText("Salvar Edição");
    }

    // Chamado pelo botão "Remover": remove o filme selecionado após confirmação.
    @FXML
    public void removerFilme() {
        Filme selecionado = tabelaFilmes.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Remoção");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText(
            "Deseja realmente remover o filme \"" + selecionado.getTitulo() + "\"?"
        );

        confirmacao.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.OK) {
                filmeNegocios.removerFilme(selecionado.getTitulo());
                atualizarTabela();
                cancelarEdicao();
                mostrarAlerta("Filme \"" + selecionado.getTitulo() + "\" removido com sucesso.");
            }
        });
    }

    /*
     * Cadastra um novo filme OU salva edição dependendo do modo atual.
     * Acionado pelo botão "Adicionar Filme" / "Salvar Edição".
     */
    @FXML
    public void clicarBotao() {
        String titulo = txtTitulo.getText().trim();
        String sinopse = txtSinopse.getText().trim();
        Genero genero = cbGenero.getValue();
        ClassificacaoIndicativa classificacao = cbClassificacao.getValue();

        if (titulo.isEmpty()) { mostrarAlerta("Título obrigatório."); return; }
        if (genero == null)   { mostrarAlerta("Selecione um gênero."); return; }
        if (classificacao == null) { mostrarAlerta("Selecione uma classificação."); return; }

        int duracao;
        try {
            duracao = Integer.parseInt(txtDuracao.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("Duração inválida. Digite apenas números.");
            return;
        }

        String posterPath = (arquivoImagem != null)
                ? arquivoImagem.toURI().toString()
                : (filmeEmEdicao != null ? filmeEmEdicao.getCaminhoPoster() : null);

        if (filmeEmEdicao != null) {
            // MODO EDIÇÃO
            filmeEmEdicao.setTitulo(titulo);
            filmeEmEdicao.setSinopse(sinopse);
            filmeEmEdicao.setDuracao(duracao);
            filmeEmEdicao.setGenero(genero);
            filmeEmEdicao.setClassificacao(classificacao);
            filmeEmEdicao.setCaminhoPoster(posterPath);
            RepositorioFilmeImpl.getInstancia().atualizar(filmeEmEdicao);
            mostrarAlerta("Filme \"" + titulo + "\" atualizado com sucesso!");
            cancelarEdicao();
        } else {
            // ── MODO CADASTRO ──
            try {
                filmeNegocios.cadastrarFilme(titulo, sinopse, duracao, genero, classificacao, posterPath);
                mostrarAlerta("Filme \"" + titulo + "\" cadastrado com sucesso!");
            } catch (IllegalArgumentException e) {
                mostrarAlerta("Erro: " + e.getMessage());
                return;
            }
            limparCampos();
        }

        atualizarTabela();
    }

    // Sai do modo edição, restaura o botão e limpa o formulário.
    private void cancelarEdicao() {
        filmeEmEdicao = null;
        btnAdicionarFilme.setText("Adicionar Filme");
        limparCampos();
        tabelaFilmes.getSelectionModel().clearSelection();
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

    @FXML
    public void btnSair_OnAction() {
        ScreenManager.getInstance().showGerenteScreen();
    }
}
