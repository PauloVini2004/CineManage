package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.enums.ClassificacaoIndicativa;
import br.ufrpe.cine_rural.enums.Idioma;
import br.ufrpe.cine_rural.gui.controllers_telas.PagamentoIngressoController;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Controller da tela de assentos.
 *
 * Persistência: o mapa de assentos ocupados (gerado aleatoriamente UMA ÚNICA VEZ
 * na primeira abertura de cada sessão) fica armazenado em layoutsPorSessao,
 * um Map estático indexado pelo horário da sessão.
 * Nas próximas aberturas da mesma sessão o layout já salvo é reutilizado.
 */
public class AssentoController {

    // -------------------------------------------------------------------------
    // Persistência de layouts por sessão (sobrevive a navegações dentro da JVM)
    // -------------------------------------------------------------------------
    private static final Map<LocalDateTime, int[][]> layoutsPorSessao = new HashMap<>();

    // -------------------------------------------------------------------------
    // Componentes FXML
    // -------------------------------------------------------------------------
    @FXML private AnchorPane painel;
    @FXML private Text textoSessaoInfo;
    @FXML private Text textoContador;
    @FXML private Button btnVoltar;
    @FXML private Button btnIngressos;

    // -------------------------------------------------------------------------
    // Estado da tela
    // -------------------------------------------------------------------------
    private List<String> nomeAssentosSelecionados = new ArrayList<>();

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

    private br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl repositorioFilmes;
    private br.ufrpe.cine_rural.dados.implemento.RepositorioSessaoImpl repositorioSessoes;

    @FXML
    public void initialize() {
        // Inicialização gerenciada pelo JavaFX — mantido limpo.
    }

    // Adiciona os repositorios para o FilmesController poder receber os dados e passar a tela
    public void setRepositorios(
            br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl filmes,
            br.ufrpe.cine_rural.dados.implemento.RepositorioSessaoImpl sessoes) {
        this.repositorioFilmes = filmes;
        this.repositorioSessoes = sessoes;
    }

    // a Geração aleatoria só ocorre uma única vez, depois persiste
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

        this.sessaoAtual    = sessao;
        this.heranca        = heranca;
        this.numeroSessao   = numeroSessao;
        this.nomeSala       = nomeSala;
        this.dataHorario    = dataHorario;
        this.idioma         = idioma;
        this.duracao        = duracao;
        this.classificacao  = classificacao;
        this.poster         = poster;
        this.tituloFilme    = tituloFilme;

        // Tenta recuperar layout já persistido para esta sessão
        LocalDateTime chave = sessao.getHorario();
        if (layoutsPorSessao.containsKey(chave)) {
            // Sessão já foi aberta antes — reutiliza o layout existente
            layoutAtual = layoutsPorSessao.get(chave);
        } else {
            // Primeira abertura desta sessão: copia o mapa base e gera ocupados
            layoutAtual = criarLayoutBase(heranca);
            ocuparAssentosAleatorios();
            // Persiste para usos futuros (dentro da mesma execução)
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

    // matrizes Layouts
    private int[][] criarLayoutBase(int heranca) {
        return switch (heranca) {
            case 2  -> SalasMapas.copiar(SalasMapas.salaImax);
            case 3  -> SalasMapas.copiar(SalasMapas.salaVip);
            default -> SalasMapas.copiar(SalasMapas.salaComum);
        };
    }

    // Ocupador aleatorio entre 10% a 30% dos Assentos disponiveis por sala
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
                layoutAtual[i][j] = 2; // 2 = ocupado
                ocupados++;
            }
        }
    }

    private void exibirPoster() {
        if (poster == null) return;
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
                                getClass().getResource("/br/ufrpe/cine_rural/gui/EstiloFilmes.css")
                                        .toExternalForm()
                        );

                        FilmesController fc = loader.getController();
                        if (repositorioFilmes != null && repositorioSessoes != null) {
                            fc.setRepositorios(repositorioFilmes, repositorioSessoes);
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
                PagamentoIngressoController.cenaAnterior =
                        painel.getScene();
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/br/ufrpe/cine_rural/gui/PagamentoIngresso.fxml")
                );
                Scene scene = new Scene(loader.load());
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

                scene.getStylesheets().add(
                        getClass().getResource(
                                "/br/ufrpe/cine_rural/gui/EstiloPagamentoIngresso.css"
                        ).toExternalForm()
                );

                Stage stageAtual = (Stage) painel.getScene().getWindow();
                stageAtual.setTitle("Pagamento");
                stageAtual.setScene(scene);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Organização de persistencia na geração das cadeiras e identificação de assentos ocupados após venda de ingresso
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

    public static void ocuparAssentos(
            LocalDateTime horarioSessao,
            List<String> assentos
    ) {
        int[][] layout = layoutsPorSessao.get(horarioSessao);

        if (layout == null) {
            return;
        }

        for (String nome : assentos) {

            int linha = nome.charAt(0) - 'A';

            int coluna = Integer.parseInt(
                    nome.substring(1)
            ) - 1;

            layout[linha][coluna] = 2;
        }
    }

}
