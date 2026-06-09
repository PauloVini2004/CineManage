package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.enums.ClassificacaoIndicativa;
import br.ufrpe.cine_rural.enums.Idioma;
import br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSessaoImpl;
import br.ufrpe.cine_rural.model.Filme;
import br.ufrpe.cine_rural.model.Sessao;
import br.ufrpe.cine_rural.model.tiposala.Comum;
import br.ufrpe.cine_rural.model.tiposala.Imax;
import br.ufrpe.cine_rural.model.tiposala.Vip;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static br.ufrpe.cine_rural.enums.StatusSessao.ABERTA;

public class FilmesController {

    @FXML
    private VBox containerFilmes;

    private RepositorioFilmeImpl repositorioFilmes;
    private RepositorioSessaoImpl repositorioSessoes;

    // Recebe os repositorios e carrega os csvs para a tela de assento
    public void setRepositorios(RepositorioFilmeImpl filmes,
                                RepositorioSessaoImpl sessoes) {
        this.repositorioFilmes  = filmes;
        this.repositorioSessoes = sessoes;
        carregarFilmes();
    }

    @FXML
    public void initialize() {
        // Os repositórios chegam via setRepositorios()
    }

    private void carregarFilmes() {
        // Agrupa as sessões abertas por título do filme
        Map<String, List<Sessao>> porFilme = new LinkedHashMap<>();

        for (Sessao s : repositorioSessoes.listar()) {
            if (s.getStatus() == ABERTA) {
                String titulo = s.getFilme().getTitulo();
                porFilme.computeIfAbsent(titulo, k -> new ArrayList<>()).add(s);
            }
        }

        // Garante que o containerFilmes esteja limpo antes de popular
        containerFilmes.getChildren().clear();

        for (List<Sessao> grupo : porFilme.values()) {
            criarCard(grupo);
        }
    }

    // Criação de cards dos filmes dinamica ao csv

    private void criarCard(List<Sessao> grupo) {
        if (grupo == null || grupo.isEmpty()) return;

        Sessao sessaoBase = grupo.get(0);
        Filme  filme      = sessaoBase.getFilme();

        HBox card = new HBox(15);

        // Poster
        VBox posterContainer = new VBox();
        Image posterImg = filme.getPoster();
        ImageView posterView = new ImageView(posterImg);
        posterView.setFitWidth(150);
        posterView.setFitHeight(220);
        posterContainer.getChildren().add(posterView);

        // Informações textuais
        VBox info = new VBox(5);

        Label titulo         = new Label(filme.getTitulo());
        titulo.getStyleClass().add("titulo-filme");

        Label classificacao  = new Label("Classificação: " + filme.getClassificacao());
        Label duracao        = new Label("Duração: " + filme.getDuracao() + " min");
        Label idioma         = new Label("Idioma: " + sessaoBase.getIdioma());

        // Horários agrupados por sala
        HBox horariosContainer = new HBox(15);
        Map<String, HBox> salasMap = new LinkedHashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Sessao s : grupo) {
            String nomeSala = s.getSala().toString();

            if (!salasMap.containsKey(nomeSala)) {
                VBox blocoSessao = new VBox(5);
                Label salaLabel  = new Label(nomeSala);
                salaLabel.getStyleClass().add("sala");
                HBox horariosSala = new HBox(5);
                blocoSessao.getChildren().addAll(salaLabel, horariosSala);
                salasMap.put(nomeSala, horariosSala);
                horariosContainer.getChildren().add(blocoSessao);
            }

            Button btnHorario = new Button(s.getHorario().format(formatter));

            btnHorario.setOnAction(event -> abrirAssentos(s));

            salasMap.get(nomeSala).getChildren().add(btnHorario);
        }

        info.getChildren().addAll(titulo, classificacao, duracao, idioma, horariosContainer);
        card.getChildren().addAll(posterContainer, info);
        containerFilmes.getChildren().add(card);
    }

    // Navegação pra tela de Assentos

    private void abrirAssentos(Sessao s) {
        // Determina o tipo da sala (herança): 1=Comum, 2=Imax, 3=Vip
        int heranca = switch (s.getSala()) {
            case Comum c -> 1;
            case Imax  i -> 2;
            case Vip   v -> 3;
            default      -> 1;
        };

        // Número de ordem da sessão na lista
        int numeroSessao = repositorioSessoes.listar().indexOf(s) + 1;

        // Data/horário formatado
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dataHorario = s.getHorario().format(dtf);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/ufrpe/cine_rural/gui/Assentos.fxml")
            );
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    getClass().getResource("/br/ufrpe/cine_rural/gui/EstiloAssentos.css")
                            .toExternalForm()
            );

            AssentoController ac = loader.getController();

            // Injeta repositórios para que o botão Voltar funcione
            ac.setRepositorios(repositorioFilmes, repositorioSessoes);

            // Passa todos os dados da sessão
            ac.setDados(
                    s,
                    heranca,
                    numeroSessao,
                    s.getSala().toString(),
                    dataHorario,
                    s.getIdioma(),
                    s.getFilme().getDuracao(),
                    s.getFilme().getClassificacao(),
                    s.getFilme().getPoster(),
                    s.getFilme().getTitulo()
            );

            Stage stage = (Stage) containerFilmes.getScene().getWindow();
            stage.setTitle("Assentos — " + s.getFilme().getTitulo());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
