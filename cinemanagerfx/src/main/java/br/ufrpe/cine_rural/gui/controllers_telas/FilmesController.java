package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.enums.ClassificacaoIndicativa;
import br.ufrpe.cine_rural.enums.Genero;
import br.ufrpe.cine_rural.enums.Idioma;
import br.ufrpe.cine_rural.enums.StatusSessao;
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

public class FilmesController {

    @FXML
    private VBox containerFilmes;

    private Sessao sessaoSelecionada = null;

    @FXML
    public void initialize() {

        // Imagens dos Posters da Tela Filmes tirados do resource (o que isso gerou de incompatibilidade foi brincadeira)
        Image poster1 = new Image(getClass().getResourceAsStream("/br/ufrpe/cine_rural/gui/Project_Hail_Mary_poster.jpg"));
        Image poster2 = new Image(getClass().getResourceAsStream("/br/ufrpe/cine_rural/gui/Odisseia.jpg"));
        Image poster3 = new Image(getClass().getResourceAsStream("/br/ufrpe/cine_rural/gui/Zootopia_2.jpg"));

        // Criando Filmes
        Filme devoradores = new Filme(
                "Devoradores de Estrelas",
                "Sinopse...",
                130,
                Genero.FICCAO,
                ClassificacaoIndicativa.QUATORZE,
                LocalTime.of(2, 10),
                poster1
        );

        Filme odisseia = new Filme(
                "A Odisseia",
                "Sinopse...",
                150,
                Genero.DRAMA,
                ClassificacaoIndicativa.DEZESSEIS,
                LocalTime.of(2, 30),
                poster2
        );

        Filme zootopia2 = new Filme(
                "Zootopia 2",
                "Sinopse...",
                90,
                Genero.COMEDIA,
                ClassificacaoIndicativa.LIVRE,
                LocalTime.of(1, 30),
                poster3
        );

        // Listando Sessões
        List<Sessao> sessoes = List.of(
                new Sessao(Idioma.DUBLADO, StatusSessao.ABERTA, LocalDateTime.of(2026, 5, 30, 14, 30), devoradores, new Vip(1, 20)),
                new Sessao(Idioma.LEGENDADO, StatusSessao.ABERTA, LocalDateTime.of(2026, 5, 30, 18, 0), odisseia, new Imax(2,40)),
                new Sessao(Idioma.DUBLADO, StatusSessao.ABERTA, LocalDateTime.of(2026, 5, 30, 20, 0), zootopia2, new Comum(3,20)),
                new Sessao(Idioma.DUBLADO, StatusSessao.ABERTA, LocalDateTime.of(2026, 5, 30, 18, 0), zootopia2, new Comum(3,20)),
                new Sessao(Idioma.DUBLADO, StatusSessao.ABERTA, LocalDateTime.of(2026, 5, 30, 20, 0), zootopia2, new Imax(5,60)),
                new Sessao(Idioma.DUBLADO, StatusSessao.ABERTA, LocalDateTime.of(2026, 5, 30, 10, 0), zootopia2, new Vip(6, 10)),
                new Sessao(Idioma.DUBLADO, StatusSessao.ABERTA, LocalDateTime.of(2026, 5, 30, 20, 0), odisseia, new Vip(7, 20))
                );

        // Corrigido: Agrupando por Título do filme (String) para evitar conflitos de hash de objetos
        Map<String, List<Sessao>> porFilme = new LinkedHashMap<>();

        for (Sessao s : sessoes) {
            String tituloFilme = s.getFilme().getTitulo();
            porFilme.computeIfAbsent(tituloFilme, k -> new ArrayList<>()).add(s);
        }

        for (List<Sessao> grupo : porFilme.values()) {
            criarCard(grupo);
        }
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
                    ac.setDados(
                            s,
                            heranca,
                            s.getSala().getId(),
                            s.getSala().toString(),
                            s.getHorario().format(DateTimeFormatter.ofPattern("HH:mm")),
                            s.getIdioma(),
                            s.getFilme().getDuracao(),
                            s.getFilme().getClassificacao(),
                            s.getFilme().getPoster(),
                            s.getFilme().getTitulo()
                    );

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