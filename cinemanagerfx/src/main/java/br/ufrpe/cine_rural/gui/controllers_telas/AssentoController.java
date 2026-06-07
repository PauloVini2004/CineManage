package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.enums.ClassificacaoIndicativa;
import br.ufrpe.cine_rural.enums.Idioma;
import br.ufrpe.cine_rural.gui.dto.SalasMapas;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AssentoController {

    @FXML private AnchorPane painel;
    @FXML private Text textoSessaoInfo;
    @FXML private Text textoContador;
    @FXML private Button btnVoltar;
    @FXML private Button btnIngressos;

    private List<String> nomeAssentosSelecionados = new ArrayList<>();

    // Variavéis para o setDados
    private int heranca;
    private int numeroSessao;
    private String nomeSala;
    private String dataHorario;
    private Idioma idioma;
    private int duracao;
    private ClassificacaoIndicativa classificacao;
    private Image poster;
    private String tituloFilme;

    // Matriz dos Mapas e variavel de seleção de assentos
    private int[][] layoutAtual;
    private int assentosSelecionados = 0;

    // Metodo chamado pelo FilmesController após load()
    public void setDados(int heranca,
                         int numeroSessao,
                         String nomeSala,
                         String dataHorario,
                         Idioma idioma,
                         int duracao,
                         ClassificacaoIndicativa classificacao,
                         Image poster,
                         String tituloFilme) {

        this.heranca = heranca;
        this.numeroSessao  = numeroSessao;
        this.nomeSala = nomeSala;
        this.dataHorario = dataHorario;
        this.idioma = idioma;
        this.duracao = duracao;
        this.classificacao = classificacao;
        this.poster = poster;
        this.tituloFilme = tituloFilme;


        // Switch case dos layouts dos mapas
        switch (heranca) {
            case 1 -> layoutAtual = SalasMapas.copiar(SalasMapas.salaComum);
            case 2 -> layoutAtual = SalasMapas.copiar(SalasMapas.salaImax);
            case 3 -> layoutAtual = SalasMapas.copiar(SalasMapas.salaVip);
            default -> layoutAtual = SalasMapas.copiar(SalasMapas.salaComum);
        }

        // Textos da tela Assento com concatenação de dados variavéis vindos do FilmesController
        textoSessaoInfo.setText(
                "Cinema Rural — Sessão " + numeroSessao
                        + " | " + nomeSala
                        + " | " + dataHorario
        );

        /*
           Texto base da seleção de cadeiras (Retirei do fxml para os controlladores)
           Objetivo : Pegar os dados dos cliques do usuario de um metodo/algoritmos e
           incrementar ou decrementar x00 Ingressos"
        */
        textoContador.setText("N. de cadeiras selecionadas  x00 Ingressos");

        // Chamando Métodos
        ocuparAssentosAleatorios();
        gerarAssentos();
        exibirPoster();
        configurarBotaoVoltar();
        configurarBotaoIngressos();
    }

    // Exibição do Poster do filme selecionado da tela Filmes para tela Assentos
    private void exibirPoster() {
        ImageView posterView = new ImageView(poster);
        posterView.setFitWidth(210);
        posterView.setFitHeight(280);
        posterView.setLayoutX(685);
        posterView.setLayoutY(65);
        painel.getChildren().add(posterView);
    }

    // Utilização do platform para retornar da tela Assentos para Filmes, tive que editar algumas coisas no fxml
    private void configurarBotaoVoltar() {

        Platform.runLater(() -> {

            Button btnVoltar = (Button) painel.lookup(".botao-vermelho");

            if (btnVoltar != null) {
                btnVoltar.setOnAction(event -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/br/ufrpe/cine_rural/gui/Filmes.fxml")
                        );

                        Scene scene = new Scene(loader.load());
                        scene.getStylesheets().add(
                                getClass().getResource("/br/ufrpe/cine_rural/gui/EstiloFilmes.css")
                                        .toExternalForm()
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

    // Utilização do platform para prosseguir da tela Assentos até a tela Ingressos, tive que editar algumas coisas no fxml também
    private void configurarBotaoIngressos() {
        btnIngressos.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/br/ufrpe/cine_rural/gui/EmissaoIngresso.fxml")
                );
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().add(
                        getClass().getResource("/br/ufrpe/cine_rural/gui/EstiloIngresso.css")
                                .toExternalForm()
                );


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /*
     Metodo para gerar aleatoriamente Assentos ocupados, tive que mudar certas coisas do SalaMapas
     Antes o mapeamento era de true ou false e mudei para 0, 1 e 2
     Já que são 3 tipos de cadeiras boolean não fez muito sentido da minha parte
     */

    private void ocuparAssentosAleatorios() {
        Random random = new Random();
        int totalAssentos = 0;

        for (int[] linha : layoutAtual) {
            for (int assento : linha) {
                if (assento == 1) totalAssentos++;  // só conta livres
            }
        }

        int quantidade = (int)(totalAssentos * (0.1 + random.nextDouble() * 0.2));

        int ocupados = 0;
        while (ocupados < quantidade) {
            int i = random.nextInt(layoutAtual.length);
            int j = random.nextInt(layoutAtual[i].length);

            if (layoutAtual[i][j] == 1) {   // só marca se for livre
                layoutAtual[i][j] = 2;      // 2 = ocupado
                ocupados++;
            }
        }
    }

    @FXML
    public void initialize() {

    }

    /*
    Metodo para Gerar, pintar e organizar nomes dos assentos, também
    receber informações pelo event executado pelo usuario, com verificador de assento ocupado
    e incremento e decremento da variavel de seleção no text da tela Assento
     */
    private void gerarAssentos() {

        int tamanho = layoutAtual.length;

        // Um caos enorme conseguir acertar a posição correta da matriz na tela, pelo menos não tive que usar gridpane
        double areaX = 40 ;
        double areaY = 90;
        double areaLargura = 620;
        double areaAltura = 340;
        double espacamento = 5;

        double larguraBotao = (areaLargura - ((tamanho - 1) * espacamento)) / tamanho;
        double alturaBotao  = (areaAltura  - ((tamanho - 1) * espacamento)) / tamanho;

        String verde    = "-fx-background-color: #00c853; -fx-text-fill: white; -fx-font-weight: bold;";
        String azul     = "-fx-background-color: #2962ff; -fx-text-fill: white; -fx-font-weight: bold;";
        String vermelho = "-fx-background-color: #fc4949; -fx-text-fill: white; -fx-font-weight: bold;";


        for (int i = 0; i < layoutAtual.length; i++) {
            for (int j = 0; j < layoutAtual[i].length; j++) {

                if (layoutAtual[i][j] == 0) continue;

                boolean estaOcupado = layoutAtual[i][j] == 2;

                Button botao = new Button((char)('A' + i) + "" + (j + 1));
                botao.setPrefSize(larguraBotao, alturaBotao);
                botao.setLayoutX(areaX + j * (larguraBotao + espacamento));
                botao.setLayoutY(areaY + i * (alturaBotao  + espacamento));

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