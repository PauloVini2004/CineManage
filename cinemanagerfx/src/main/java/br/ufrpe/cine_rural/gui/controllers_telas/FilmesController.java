package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.enums.ClassificacaoIndicativa;
import br.ufrpe.cine_rural.enums.Genero;
import br.ufrpe.cine_rural.enums.Idioma;
import br.ufrpe.cine_rural.enums.StatusSessao;
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static br.ufrpe.cine_rural.enums.StatusSessao.ABERTA;

public class FilmesController {

    @FXML
    private VBox containerFilmes;

    private Sessao sessaoSelecionada = null;

    private RepositorioFilmeImpl repositorioFilmes;
    private RepositorioSessaoImpl repositorioSessoes;

    public void setRepositorios(RepositorioFilmeImpl filmes,
                                RepositorioSessaoImpl sessoes) {

        this.repositorioFilmes = filmes;
        this.repositorioSessoes = sessoes;

        carregarFilmes();

    }

    private void carregarFilmes() {

        Map<String, List<Sessao>> porFilme = new LinkedHashMap<>();

        for (Sessao s : repositorioSessoes.listar()) {
            if(s.getStatus() == ABERTA) {
                String tituloFilme = s.getFilme().getTitulo();
                porFilme.computeIfAbsent(tituloFilme, k -> new ArrayList<>()).add(s);
            }
        }

        for (List<Sessao> grupo : porFilme.values()) {
            criarCard(grupo);
        }
    }


    @FXML
    public void initialize() {
    }


    // Organizando e criando fxml diretamente pelo código pelo metodo criarCard
    private void criarCard(List<Sessao> grupo) {
        if (grupo == null || grupo.isEmpty()) return;

        Sessao sessaoBase = grupo.get(0);
        Filme filme = sessaoBase.getFilme();

        HBox card = new HBox(15);
        VBox posterContainer = new VBox();

        ImageView posterImage = new ImageView(filme.getPoster());
        posterImage.setFitWidth(150);
        posterImage.setFitHeight(220);
        posterContainer.getChildren().addAll(posterImage);

        VBox info = new VBox(5);

        Label titulo = new Label(filme.getTitulo());
        titulo.getStyleClass().add("titulo-filme");

        Label classificacao = new Label("Classificação: " + filme.getClassificacao().toString());
        Label duracao = new Label("Duração: " + filme.getDuracao() + " min");
        Label idioma = new Label("Idioma: " + sessaoBase.getIdioma().toString());

        HBox horariosContainer = new HBox(15);
        Map<String, HBox> salasMap = new LinkedHashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Sessao s : grupo) {
            String nomeSala;
            nomeSala = s.getSala().toString();

            if (!salasMap.containsKey(nomeSala)) {
                VBox blocoSessao = new VBox(5);

                Label salaLabel = new Label(nomeSala);
                salaLabel.getStyleClass().add("sala");

                HBox horariosSala = new HBox(5);
                blocoSessao.getChildren().addAll(salaLabel, horariosSala);

                salasMap.put(nomeSala, horariosSala);
                horariosContainer.getChildren().add(blocoSessao);
            }

            Button btnHorario = new Button(s.getHorario().format(formatter));

            btnHorario.setOnAction(event -> {
                sessaoSelecionada = s;

                int heranca = switch (s.getSala()) {
                    case Comum c -> 1;
                    case Imax i -> 2;
                    case Vip v -> 3;
                    default -> 5;
                };

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

                    Stage stageAssentos = (Stage) containerFilmes.getScene().getWindow();
                    stageAssentos.setTitle("Assentos — " + s.getFilme().getTitulo());
                    stageAssentos.setScene(scene);
                    stageAssentos.setResizable(false);
                    stageAssentos.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            salasMap.get(nomeSala).getChildren().add(btnHorario);
        }

        info.getChildren().addAll(titulo, classificacao, duracao, idioma, horariosContainer);
        card.getChildren().addAll(posterContainer, info);

        containerFilmes.getChildren().add(card);
    }
}