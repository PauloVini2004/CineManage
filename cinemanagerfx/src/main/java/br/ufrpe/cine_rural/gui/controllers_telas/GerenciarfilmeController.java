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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class GerenciarfilmeController {

    // Pasta de armazenamento de posters
    static final String PASTA_POSTERS = "posters";

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

    // Guarda apenas o NOME do arquivo após a cópia (ex: "Odisseia.jpg")
    private String nomeArquivoImagemSelecionado;
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
        File arquivoOrigem = fileChooser.showOpenDialog(null);
        if (arquivoOrigem == null) return;

        // Copia o arquivo para a pasta local "posters/" do projeto
        try {
            File pastaDestino = new File(PASTA_POSTERS);
            if (!pastaDestino.exists()) pastaDestino.mkdirs();

            File arquivoDestino = new File(pastaDestino, arquivoOrigem.getName());
            Files.copy(arquivoOrigem.toPath(), arquivoDestino.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            nomeArquivoImagemSelecionado = arquivoOrigem.getName();
            imgPoster.setImage(new Image(arquivoDestino.toURI().toString()));

        } catch (IOException e) {
            mostrarAlerta("Erro ao copiar imagem: " + e.getMessage());
        }
    }

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

        // Carrega preview usando o mesmo mecanismo portátil
        String nomeArquivo = selecionado.getCaminhoPoster();
        if (nomeArquivo != null && !nomeArquivo.isBlank()) {
            Image img = RepositorioFilmeImpl.carregarImagem(nomeArquivo);
            if (img != null) imgPoster.setImage(img);
        }

        nomeArquivoImagemSelecionado = null; // reseta para não sobrescrever se não mudar a imagem
        btnAdicionarFilme.setText("Salvar Edição");
    }

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

    @FXML
    public void clicarBotao() {
        String titulo = txtTitulo.getText().trim();
        String sinopse = txtSinopse.getText().trim();
        Genero genero = cbGenero.getValue();
        ClassificacaoIndicativa classificacao = cbClassificacao.getValue();

        if (titulo.isEmpty())      { mostrarAlerta("Título obrigatório."); return; }
        if (genero == null)        { mostrarAlerta("Selecione um gênero."); return; }
        if (classificacao == null) { mostrarAlerta("Selecione uma classificação."); return; }

        int duracao;
        try {
            duracao = Integer.parseInt(txtDuracao.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("Duração inválida. Digite apenas números.");
            return;
        }

        // Usa o novo nome de arquivo se foi selecionado, senão mantém o existente
        String posterNome = (nomeArquivoImagemSelecionado != null)
                ? nomeArquivoImagemSelecionado
                : (filmeEmEdicao != null ? filmeEmEdicao.getCaminhoPoster() : null);

        if (filmeEmEdicao != null) {
            filmeEmEdicao.setTitulo(titulo);
            filmeEmEdicao.setSinopse(sinopse);
            filmeEmEdicao.setDuracao(duracao);
            filmeEmEdicao.setGenero(genero);
            filmeEmEdicao.setClassificacao(classificacao);
            filmeEmEdicao.setCaminhoPoster(posterNome);
            RepositorioFilmeImpl.getInstancia().atualizar(filmeEmEdicao);
            mostrarAlerta("Filme \"" + titulo + "\" atualizado com sucesso!");
            cancelarEdicao();
        } else {
            try {
                filmeNegocios.cadastrarFilme(titulo, sinopse, duracao, genero, classificacao, posterNome);
                mostrarAlerta("Filme \"" + titulo + "\" cadastrado com sucesso!");
            } catch (IllegalArgumentException e) {
                mostrarAlerta("Erro: " + e.getMessage());
                return;
            }
            limparCampos();
        }

        atualizarTabela();
    }

    private void cancelarEdicao() {
        filmeEmEdicao = null;
        nomeArquivoImagemSelecionado = null;
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
        nomeArquivoImagemSelecionado = null;
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
