package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.enums.ClassificacaoIndicativa;
import br.ufrpe.cine_rural.enums.Idioma;
import br.ufrpe.cine_rural.gui.controllers_telas.emergencia.PagamentoIngressoController;
import br.ufrpe.cine_rural.gui.dto.SalasMapas;
import br.ufrpe.cine_rural.model.Assento;
import br.ufrpe.cine_rural.model.Ingresso;
import br.ufrpe.cine_rural.model.Sessao;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import br.ufrpe.cine_rural.enums.CategoriaMeiaEntrada;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static br.ufrpe.cine_rural.enums.CategoriaMeiaEntrada.ESTUDANTE;

public class AssentoController {

    @FXML private AnchorPane painel;
    @FXML private Text textoSessaoInfo;
    @FXML private Text textoContador;
    @FXML private Button btnVoltar;
    @FXML private Button btnIngressos;

    private List<String> nomeAssentosSelecionados = new ArrayList<>();

    // Guarda o objeto sessão completo recebido do FilmesController
    private Sessao sessaoAtual;

    private int heranca;
    private int numeroSessao;
    private String nomeSala;
    private String dataHorario;
    private Idioma idioma;
    private int duracao;
    private ClassificacaoIndicativa classificacao;
    private Image poster;
    private String tituloFilme;

    private int[][] layoutAtual;
    private int assentosSelecionados = 0;

    @FXML
    public void initialize() {
        // Inicialização padrão gerenciada pelo JavaFX (mantido limpo)
    }

    public void setDados(Sessao sessao,
                         int heranca,
                         int numeroSessao,
                         String nomeSala,
                         String dataHorario,
                         Idioma idioma,
                         int duracao,
                         ClassificacaoIndicativa classificacao,
                         Image poster,
                         String tituloFilme) {

        this.sessaoAtual = sessao;
        this.heranca = heranca;
        this.numeroSessao = numeroSessao;
        this.nomeSala = nomeSala;
        this.dataHorario = dataHorario;
        this.idioma = idioma;
        this.duracao = duracao;
        this.classificacao = classificacao;
        this.poster = poster;
        this.tituloFilme = tituloFilme;

        switch (heranca) {
            case 1 -> layoutAtual = SalasMapas.copiar(SalasMapas.salaComum);
            case 2 -> layoutAtual = SalasMapas.copiar(SalasMapas.salaImax);
            case 3 -> layoutAtual = SalasMapas.copiar(SalasMapas.salaVip);
            default -> layoutAtual = SalasMapas.copiar(SalasMapas.salaComum);
        }

        textoSessaoInfo.setText(
                "Cinema Rural — Sessão " + numeroSessao
                        + " | " + nomeSala
                        + " | " + dataHorario
        );

        textoContador.setText("N. de cadeiras selecionadas  x00 Ingressos");

        ocuparAssentosAleatorios();
        gerarAssentos();
        exibirPoster();
        configurarBotaoVoltar();
        configurarBotaoIngressos();
    }

    private void exibirPoster() {
        ImageView posterView = new ImageView(poster);
        posterView.setFitWidth(210);
        posterView.setFitHeight(280);
        posterView.setLayoutX(685);
        posterView.setLayoutY(65);
        painel.getChildren().add(posterView);
    }

    private void configurarBotaoVoltar() {
        Platform.runLater(() -> {
            Button btnVoltarReal = (Button) painel.lookup(".botao-vermelho");
            if (btnVoltarReal != null) {
                btnVoltarReal.setOnAction(event -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/br/ufrpe/cine_rural/gui/Filmes.fxml")
                        );
                        Scene scene = new Scene(loader.load());
                        scene.getStylesheets().add(
                                getClass().getResource("/br/ufrpe/cine_rural/gui/EstiloFilmes.css").toExternalForm()
                        );
                        Stage stageAtual = (Stage) painel.getScene().getWindow();
                        stageAtual.setTitle("Filmes");
                        stageAtual.setScene(scene);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void configurarBotaoIngressos() {
        btnIngressos.setOnAction(event -> {
            if (nomeAssentosSelecionados.isEmpty()) {
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/br/ufrpe/cine_rural/gui/Emergencia/PagamentoIngresso.fxml")
                );

                Scene scene = new Scene(loader.load());
                PagamentoIngressoController controller = loader.getController();


                double precoDinamico = 10.0;
                if (this.sessaoAtual != null && this.sessaoAtual.getSala() != null) {
                    precoDinamico = this.sessaoAtual.getSala().getPreco();
                }

                ArrayList<Ingresso> ingressos = new ArrayList<>();
                for (String nomeAssento : nomeAssentosSelecionados) {

                    Assento objetoAssento = new Assento(nomeAssento);

                    Ingresso ingresso = new Ingresso(
                            this.sessaoAtual,
                            objetoAssento,
                            precoDinamico,
                            CategoriaMeiaEntrada.INTEIRA
                    );

                    sessaoAtual.adicionarIngressos(ingresso);

                    ingressos.add(ingresso);
                }

                controller.receberIngressos(ingressos);

                scene.getStylesheets().add(
                        getClass().getResource("/br/ufrpe/cine_rural/gui/Emergencia/EstiloPagamentoIngresso.css").toExternalForm()
                );

                Stage stageAtual = (Stage) painel.getScene().getWindow();
                stageAtual.setTitle("Pagamento");
                stageAtual.setScene(scene);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void ocuparAssentosAleatorios() {
        Random random = new Random();
        int totalAssentos = 0;

        for (int[] linha : layoutAtual) {
            for (int assento : linha) {
                if (assento == 1) totalAssentos++;
            }
        }

        int quantidade = (int) (totalAssentos * (0.1 + random.nextDouble() * 0.2));
        int ocupados = 0;

        while (ocupados < quantidade) {
            int i = random.nextInt(layoutAtual.length);
            int j = random.nextInt(layoutAtual[i].length);

            if (layoutAtual[i][j] == 1) {
                layoutAtual[i][j] = 2; // 2 representa ocupado
                ocupados++;
            }
        }
    }

    private void gerarAssentos() {
        int tamanho = layoutAtual.length;
        double areaX = 40;
        double areaY = 90;
        double areaLargura = 620;
        double areaAltura = 340;
        double espacamento = 5;

        double larguraBotao = (areaLargura - ((tamanho - 1) * espacamento)) / tamanho;
        double alturaBotao = (areaAltura - ((tamanho - 1) * espacamento)) / tamanho;

        String verde = "-fx-background-color: #00c853; -fx-text-fill: white; -fx-font-weight: bold;";
        String azul = "-fx-background-color: #2962ff; -fx-text-fill: white; -fx-font-weight: bold;";
        String vermelho = "-fx-background-color: #fc4949; -fx-text-fill: white; -fx-font-weight: bold;";

        for (int i = 0; i < layoutAtual.length; i++) {
            for (int j = 0; j < layoutAtual[i].length; j++) {
                if (layoutAtual[i][j] == 0) continue;

                boolean estaOcupado = layoutAtual[i][j] == 2;

                Button botao = new Button((char) ('A' + i) + "" + (j + 1));
                botao.setPrefSize(larguraBotao, alturaBotao);
                botao.setLayoutX(areaX + j * (larguraBotao + espacamento));
                botao.setLayoutY(areaY + i * (alturaBotao + espacamento));

                if (estaOcupado) {
                    botao.setStyle(vermelho);
                } else {
                    botao.setStyle(verde);
                }

                botao.setOnAction(event -> {
                    if (estaOcupado) return;

                    boolean estaSelecionado = botao.getStyle().equals(azul);

                    if (estaSelecionado) {
                        botao.setStyle(verde);
                        assentosSelecionados--;
                        nomeAssentosSelecionados.remove(botao.getText());
                    } else {
                        botao.setStyle(azul);
                        assentosSelecionados++;
                        nomeAssentosSelecionados.add(botao.getText());
                    }

                    textoContador.setText(
                            "N. de cadeiras selecionadas  x"
                                    + String.format("%02d", assentosSelecionados)
                                    + " Ingressos"
                    );
                });

                painel.getChildren().add(botao);
            }
        }
    }
}