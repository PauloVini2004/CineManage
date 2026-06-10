package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.enums.ClassificacaoIndicativa;
import br.ufrpe.cine_rural.enums.Idioma;
import br.ufrpe.cine_rural.gui.controllers_telas.PagamentoIngressoController;
import br.ufrpe.cine_rural.gui.dto.SalasMapas;
import br.ufrpe.cine_rural.model.Assento;
import br.ufrpe.cine_rural.model.Ingresso;
import br.ufrpe.cine_rural.model.Sessao;
import br.ufrpe.cine_rural.negocios.FilmeNegocios;
import br.ufrpe.cine_rural.negocios.SessaoNegocios;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AssentoController {

    private static final Map<LocalDateTime, int[][]> layoutsPorSessao = new HashMap<>();

    @FXML private AnchorPane painel;
    @FXML private Text textoSessaoInfo;
    @FXML private Text textoContador;
    @FXML private Button btnVoltar;
    @FXML private Button btnIngressos;

    private List<String> nomeAssentosSelecionados = new ArrayList<>();

    private Sessao sessaoAtual;
    private int heranca;
    private int numeroSessao;
    private String nomeSala;
    private String dataHorario;
    private Idioma idioma;
    private int duracao;
    private ClassificacaoIndicativa classificacao;
    private String posterPath; // ✅ era Image poster
    private String tituloFilme;

    private int[][] layoutAtual;
    private int assentosSelecionados = 0;

    // ✅ Camadas de negócio no lugar dos repositórios
    private FilmeNegocios filmeNegocios;
    private SessaoNegocios sessaoNegocios;

    @FXML
    public void initialize() {}

    // ✅ Recebe FilmeNegocios e SessaoNegocios, não os repositórios
    public void setNegocios(FilmeNegocios filmeNegocios, SessaoNegocios sessaoNegocios) {
        this.filmeNegocios = filmeNegocios;
        this.sessaoNegocios = sessaoNegocios;
    }

    // ✅ poster agora é String
    public void setDados(Sessao sessao,
                         int heranca,
                         int numeroSessao,
                         String nomeSala,
                         String dataHorario,
                         Idioma idioma,
                         int duracao,
                         ClassificacaoIndicativa classificacao,
                         String posterPath,
                         String tituloFilme) {

        this.sessaoAtual   = sessao;
        this.heranca       = heranca;
        this.numeroSessao  = numeroSessao;
        this.nomeSala      = nomeSala;
        this.dataHorario   = dataHorario;
        this.idioma        = idioma;
        this.duracao       = duracao;
        this.classificacao = classificacao;
        this.posterPath    = posterPath; // ✅ String
        this.tituloFilme   = tituloFilme;

        LocalDateTime chave = sessao.getHorario();
        if (layoutsPorSessao.containsKey(chave)) {
            layoutAtual = layoutsPorSessao.get(chave);
        } else {
            layoutAtual = criarLayoutBase(heranca);
            ocuparAssentosAleatorios();
            layoutsPorSessao.put(chave, layoutAtual);
        }

        textoSessaoInfo.setText(
                "Cinema Rural — Sessão " + numeroSessao
                        + " | " + nomeSala
                        + " | " + dataHorario
        );

        textoContador.setText("N. de cadeiras selecionadas  x00 Ingressos");

        gerarAssentos();
        exibirPoster();
        configurarBotaoVoltar();
        configurarBotaoIngressos();
    }

    private int[][] criarLayoutBase(int heranca) {
        return switch (heranca) {
            case 2  -> SalasMapas.copiar(SalasMapas.salaImax);
            case 3  -> SalasMapas.copiar(SalasMapas.salaVip);
            default -> SalasMapas.copiar(SalasMapas.salaComum);
        };
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
                layoutAtual[i][j] = 2;
                ocupados++;
            }
        }
    }

    private void exibirPoster() {
        // ✅ Converte String → Image aqui, na camada de UI
        if (posterPath == null || posterPath.isBlank()) return;

        try {
            Image imagem = new Image(posterPath);
            ImageView posterView = new ImageView(imagem);
            posterView.setFitWidth(210);
            posterView.setFitHeight(280);
            posterView.setLayoutX(685);
            posterView.setLayoutY(65);
            painel.getChildren().add(posterView);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                                getClass().getResource("/br/ufrpe/cine_rural/gui/EstiloFilmes.css")
                                        .toExternalForm()
                        );

                        FilmesController fc = loader.getController();
                        // ✅ Passa FilmeNegocios e SessaoNegocios, não os repositórios
                        if (filmeNegocios != null && sessaoNegocios != null) {
                            fc.setNegocios(filmeNegocios, sessaoNegocios);
                        }

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
            if (nomeAssentosSelecionados.isEmpty()) return;

            try {
                PagamentoIngressoController.cenaAnterior = painel.getScene();
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/br/ufrpe/cine_rural/gui/PagamentoIngresso.fxml")
                );


                Scene scene = new Scene(loader.load());

                scene.getStylesheets().add(
                        getClass().getResource(
                                "/br/ufrpe/cine_rural/gui/EstiloPagamentoIngresso.css"
                        ).toExternalForm()
                );

                PagamentoIngressoController controller = loader.getController();

                double precoDinamico = 10.0;
                if (sessaoAtual != null && sessaoAtual.getSala() != null) {
                    precoDinamico = sessaoAtual.getSala().getPreco();
                }

                ArrayList<Ingresso> ingressos = new ArrayList<>();
                for (String nomeAssento : nomeAssentosSelecionados) {
                    Assento objetoAssento = new Assento(nomeAssento);
                    Ingresso ingresso = new Ingresso(
                            sessaoAtual,
                            objetoAssento,
                            precoDinamico,
                            CategoriaMeiaEntrada.INTEIRA
                    );
                    sessaoAtual.adicionarIngressos(ingresso);
                    ingressos.add(ingresso);
                }

                controller.receberIngressos(ingressos);

                Stage stageAtual = (Stage) painel.getScene().getWindow();
                stageAtual.setTitle("Pagamento");
                stageAtual.setScene(scene);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void gerarAssentos() {
        int tamanho = layoutAtual.length;
        double areaX       = 40;
        double areaY       = 90;
        double areaLargura = 620;
        double areaAltura  = 340;
        double espacamento = 5;

        double larguraBotao = (areaLargura - ((tamanho - 1) * espacamento)) / tamanho;
        double alturaBotao  = (areaAltura  - ((tamanho - 1) * espacamento)) / tamanho;

        String verde    = "-fx-background-color: #00c853; -fx-text-fill: white; -fx-font-weight: bold;";
        String azul     = "-fx-background-color: #2962ff; -fx-text-fill: white; -fx-font-weight: bold;";
        String vermelho = "-fx-background-color: #fc4949; -fx-text-fill: white; -fx-font-weight: bold;";

        for (int i = 0; i < layoutAtual.length; i++) {
            for (int j = 0; j < layoutAtual[i].length; j++) {
                if (layoutAtual[i][j] == 0) continue;

                boolean estaOcupado = (layoutAtual[i][j] == 2);

                Button botao = new Button((char) ('A' + i) + "" + (j + 1));
                botao.setPrefSize(larguraBotao, alturaBotao);
                botao.setLayoutX(areaX + j * (larguraBotao + espacamento));
                botao.setLayoutY(areaY + i * (alturaBotao + espacamento));
                botao.setStyle(estaOcupado ? vermelho : verde);

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

    public static void ocuparAssentos(LocalDateTime horarioSessao, List<String> assentos) {
        int[][] layout = layoutsPorSessao.get(horarioSessao);
        if (layout == null) return;

        for (String nome : assentos) {
            int linha  = nome.charAt(0) - 'A';
            int coluna = Integer.parseInt(nome.substring(1)) - 1;
            layout[linha][coluna] = 2;
        }
    }
}