package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSessaoImpl;
import br.ufrpe.cine_rural.model.Filme;
import br.ufrpe.cine_rural.model.Sessao;
import br.ufrpe.cine_rural.negocios.FilmeNegocios;
import br.ufrpe.cine_rural.negocios.SessaoNegocios;
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

    @FXML
    private Button btnVoltar;

    private final SessaoNegocios sessaoNegocios =
            new SessaoNegocios(
                    RepositorioSessaoImpl.getInstancia()
            );
    private final FilmeNegocios filmeNegocios =
            new FilmeNegocios(
                    RepositorioFilmeImpl.getInstancia(), sessaoNegocios
            );


    @FXML
    public void initialize() {carregarFilmes();}

    @FXML
    private void voltarParaHome() {
        ScreenManager.getInstance().showAtendenteScreen();
    }

    private void carregarFilmes() {
        Map<String, List<Sessao>> porFilme = new LinkedHashMap<>();

        for (Sessao s : sessaoNegocios.listarSessoes()) {
            if (s.getStatus() == ABERTA) {
                String titulo = s.getFilme().getTitulo();
                porFilme.computeIfAbsent(titulo, k -> new ArrayList<>()).add(s);
            }
        }

        containerFilmes.getChildren().clear();

        for (List<Sessao> grupo : porFilme.values()) {
            criarCard(grupo);
        }
    }

    private void criarCard(List<Sessao> grupo) {
        if (grupo == null || grupo.isEmpty()) return;

        Sessao sessaoBase = grupo.get(0);
        Filme filme = sessaoBase.getFilme();

        HBox card = new HBox(15);

        VBox posterContainer = new VBox();
        String posterPath = filme.getCaminhoPoster();
        ImageView posterView = new ImageView();
        if (posterPath != null && !posterPath.isBlank()) {
            Image img = RepositorioFilmeImpl.carregarImagem(posterPath);
            if (img != null) posterView.setImage(img);
        }
        posterView.setFitWidth(150);
        posterView.setFitHeight(220);
        posterContainer.getChildren().add(posterView);

        VBox info = new VBox(5);

        Label titulo = new Label(filme.getTitulo());
        titulo.getStyleClass().add("titulo-filme");

        Label classificacao = new Label("Classificação: " + filme.getClassificacao());
        Label duracao = new Label("Duração: " + filme.getDuracao() + " min");
        Label idioma = new Label("Idioma: " + sessaoBase.getIdioma());

        HBox horariosContainer = new HBox(15);
        Map<String, HBox> salasMap = new LinkedHashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Sessao s : grupo) {
            String nomeSala = s.getSala().toString();

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
            // (mudança aqui) ao clicar no horário, navega para a tela de assentos
            btnHorario.setOnAction(event -> irParaAssentos(s));
            salasMap.get(nomeSala).getChildren().add(btnHorario);
        }

        info.getChildren().addAll(titulo, classificacao, duracao, idioma, horariosContainer);
        card.getChildren().addAll(posterContainer, info);
        containerFilmes.getChildren().add(card);
    }

    private void irParaAssentos(Sessao sessao) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/ufrpe/cine_rural/gui/Assentos.fxml")
            );
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(
                    getClass()
                            .getResource("/br/ufrpe/cine_rural/gui/EstiloAssentos.css")
                            .toExternalForm()
            );

            AssentoController  controller = loader.getController();
            controller.setNegocios(filmeNegocios, sessaoNegocios);

            int heranca = sessao.getSala().getId();
            int numeroSessao = sessaoNegocios.listarSessoes().indexOf(sessao) + 1;
            String nomeSala = sessao.getSala().toString();
            String dataHorario = sessao.getHorario()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            controller.setDados(
                    sessao,
                    heranca,
                    numeroSessao,
                    nomeSala,
                    dataHorario,
                    sessao.getIdioma(),
                    sessao.getFilme().getDuracao(),
                    sessao.getFilme().getClassificacao(),
                    sessao.getFilme().getCaminhoPoster(),
                    sessao.getFilme().getTitulo()
            );

            Stage stage = (Stage) containerFilmes.getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}