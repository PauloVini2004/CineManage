package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.dados.implemento.RepositorioFilmeImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioVendaIngressoImpl;
import br.ufrpe.cine_rural.enums.CategoriaMeiaEntrada;
import br.ufrpe.cine_rural.enums.ClassificacaoIndicativa;
import br.ufrpe.cine_rural.enums.Idioma;
import br.ufrpe.cine_rural.gui.dto.SalasMapas;
import br.ufrpe.cine_rural.model.Assento;
import br.ufrpe.cine_rural.model.Ingresso;
import br.ufrpe.cine_rural.model.Sessao;
import br.ufrpe.cine_rural.negocios.FilmeNegocios;
import br.ufrpe.cine_rural.negocios.SessaoNegocios;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.*;

public class AssentoController {

    private static final Map<LocalDateTime, int[][]> layoutsPorSessao = new HashMap<>();

    @FXML private AnchorPane painel;
    @FXML private Text textoSessaoInfo;
    @FXML private Text textoContador;
    @FXML private Button btnVoltar;
    @FXML private Button btnIngressos;

    // Mapa: código do assento -> categoria/idade do cliente
    private final Map<String, CategoriaMeiaEntrada> categoriasPorAssento = new LinkedHashMap<>();
    private final Map<String, Integer>              idadesPorAssento      = new LinkedHashMap<>();
    private final List<String>                      nomeAssentosSelecionados = new ArrayList<>();

    private Sessao sessaoAtual;
    private int heranca;
    private int numeroSessao;
    private String nomeSala;
    private String dataHorario;
    private Idioma idioma;
    private int duracao;
    private ClassificacaoIndicativa classificacao;
    private String posterPath;
    private String tituloFilme;

    private int[][] layoutAtual;
    private int assentosSelecionados = 0;

    private FilmeNegocios filmeNegocios;
    private SessaoNegocios sessaoNegocios;

    @FXML
    public void initialize() {}

    public void setNegocios(FilmeNegocios filmeNegocios, SessaoNegocios sessaoNegocios) {
        this.filmeNegocios  = filmeNegocios;
        this.sessaoNegocios = sessaoNegocios;
    }

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
        this.posterPath    = posterPath;
        this.tituloFilme   = tituloFilme;

        LocalDateTime chave = sessao.getHorario();

        if (layoutsPorSessao.containsKey(chave)) {
            // Layout já existe em memória nesta execução — reutiliza.
            layoutAtual = layoutsPorSessao.get(chave);
        } else {
            // Primeira vez que esta sessão é aberta nesta execução:
            // cria o layout limpo e aplica os assentos já vendidos no CSV.
            layoutAtual = criarLayoutBase(heranca);
            carregarOcupadosDoCSV(chave);
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

    private void carregarOcupadosDoCSV(LocalDateTime horarioSessao) {
        Set<String> ocupados = RepositorioVendaIngressoImpl
                .getInstancia()
                .carregarAssentosOcupados(horarioSessao);

        for (String codigo : ocupados) {
            if (codigo == null || codigo.length() < 2) continue;
            try {
                int linha  = Character.toUpperCase(codigo.charAt(0)) - 'A';
                int coluna = Integer.parseInt(codigo.substring(1)) - 1;

                if (linha  >= 0 && linha  < layoutAtual.length &&
                    coluna >= 0 && coluna < layoutAtual[linha].length &&
                    layoutAtual[linha][coluna] != 0) {
                    layoutAtual[linha][coluna] = 2;
                }
            } catch (NumberFormatException e) {
                System.err.println("Código de assento inválido no CSV: " + codigo);
            }
        }
    }

    // Layouts
    private int[][] criarLayoutBase(int heranca) {
        return switch (heranca) {
            case 2  -> SalasMapas.copiar(SalasMapas.salaImax);
            case 3  -> SalasMapas.copiar(SalasMapas.salaVip);
            default -> SalasMapas.copiar(SalasMapas.salaComum);
        };
    }

    // Poster
    private void exibirPoster() {
        if (posterPath == null || posterPath.isBlank()) return;
        Image imagem = RepositorioFilmeImpl.carregarImagem(posterPath);
        if (imagem == null) return;
        ImageView posterView = new ImageView(imagem);
        posterView.setFitWidth(200);
        posterView.setFitHeight(270);
        posterView.setLayoutX(690);
        posterView.setLayoutY(55);
        painel.getChildren().add(posterView);
    }

    // Botões
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
                        Stage stageAtual = (Stage) painel.getScene().getWindow();
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

            int idadeLimite = getIdadeLimiteClassificacao(classificacao);

            if (idadeLimite > 0) {
                boolean temMenor        = false;
                boolean temMaiorOuIgual = false;

                for (String nome : nomeAssentosSelecionados) {
                    int idade = idadesPorAssento.getOrDefault(nome, 99);
                    if (idade < idadeLimite) temMenor = true;
                    else                     temMaiorOuIgual = true;
                }

                if (temMenor && !temMaiorOuIgual) {
                    Alert alerta = new Alert(Alert.AlertType.WARNING);
                    alerta.setTitle("Atenção");
                    alerta.setHeaderText("Menor de idade sem acompanhante");
                    alerta.setContentText(
                            "Este filme possui classificação indicativa de " + idadeLimite + " anos.\n"
                                    + "Menores de " + idadeLimite + " anos não podem assistir sem um acompanhante maior.\n"
                                    + "Adicione um assento para um acompanhante maior de " + idadeLimite + " anos."
                    );
                    alerta.showAndWait();
                    return;
                }
            }

            // Montar ingressos com categoria/idade de cada assento
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
                    CategoriaMeiaEntrada cat = categoriasPorAssento
                            .getOrDefault(nomeAssento, CategoriaMeiaEntrada.INTEIRA);

                    Ingresso ingresso = new Ingresso(
                            sessaoAtual,
                            objetoAssento,
                            precoDinamico,
                            cat
                    );
                    sessaoAtual.adicionarIngressos(ingresso);
                    ingressos.add(ingresso);
                }

                controller.receberIngressos(ingressos, new java.util.HashMap<>(idadesPorAssento));

                Stage stageAtual = (Stage) painel.getScene().getWindow();
                // title kept as "Cine Manager" by ScreenManager
                stageAtual.setScene(scene);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Geração dos botões de assento

    private void gerarAssentos() {
        int    tamanho    = layoutAtual.length;
        double areaX      = 40;
        double areaY      = 90;
        double areaLarg   = 620;
        double areaAltura = 340;
        double esp        = 5;

        double largBotao = (areaLarg   - ((tamanho - 1) * esp)) / tamanho;
        double altBotao  = (areaAltura - ((tamanho - 1) * esp)) / tamanho;

        String verde    = "-fx-background-color: #00c853; -fx-text-fill: white; -fx-font-weight: bold;";
        String azul     = "-fx-background-color: #2962ff; -fx-text-fill: white; -fx-font-weight: bold;";
        String vermelho = "-fx-background-color: #fc4949; -fx-text-fill: white; -fx-font-weight: bold;";

        for (int i = 0; i < layoutAtual.length; i++) {
            for (int j = 0; j < layoutAtual[i].length; j++) {
                if (layoutAtual[i][j] == 0) continue;

                boolean estaOcupado = (layoutAtual[i][j] == 2);

                Button botao = new Button((char) ('A' + i) + "" + (j + 1));
                botao.setPrefSize(largBotao, altBotao);
                botao.setLayoutX(areaX + j * (largBotao + esp));
                botao.setLayoutY(areaY + i * (altBotao  + esp));
                botao.setStyle(estaOcupado ? vermelho : verde);

                botao.setOnAction(ev -> {
                    if (estaOcupado) return;

                    boolean estaSelecionado = botao.getStyle().equals(azul);

                    if (estaSelecionado) {
                        // Desselecionar
                        botao.setStyle(verde);
                        assentosSelecionados--;
                        nomeAssentosSelecionados.remove(botao.getText());
                        categoriasPorAssento.remove(botao.getText());
                        idadesPorAssento.remove(botao.getText());
                    } else {
                        // Abrir diálogo de categoria/idade
                        boolean confirmado = abrirDialogoCategoria(botao.getText());
                        if (confirmado) {
                            botao.setStyle(azul);
                            assentosSelecionados++;
                            nomeAssentosSelecionados.add(botao.getText());
                        }
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

    // Diálogo de categoria / idade

    /*
     * Exibe um diálogo modal pedindo a categoria (Inteira / Meia Entrada)
     * e a idade do cliente para o assento informado.
     */

    private boolean abrirDialogoCategoria(String nomeAssento) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Assento " + nomeAssento + " — Tipo de Ingresso");
        dialog.setResizable(false);

        Label lblTitulo = new Label("Assento: " + nomeAssento);
        lblTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label lblCategoria = new Label("Tipo de ingresso:");
        ComboBox<CategoriaMeiaEntrada> comboCategoria = new ComboBox<>(
                FXCollections.observableArrayList(CategoriaMeiaEntrada.INTEIRA, CategoriaMeiaEntrada.MEIA_ENTRADA)
        );
        comboCategoria.setValue(CategoriaMeiaEntrada.INTEIRA);
        comboCategoria.setMaxWidth(Double.MAX_VALUE);

        comboCategoria.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(CategoriaMeiaEntrada item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : labelCategoria(item));
            }
        });
        comboCategoria.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(CategoriaMeiaEntrada item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : labelCategoria(item));
            }
        });

        Label lblIdade = new Label("Idade do espectador:");
        TextField txtIdade = new TextField();
        txtIdade.setPromptText("Ex: 25");
        txtIdade.setMaxWidth(Double.MAX_VALUE);

        double preco = (sessaoAtual != null && sessaoAtual.getSala() != null)
                ? sessaoAtual.getSala().getPreco()
                : 10.0;

        Label lblPrecoInfo = new Label("Valor: R$ " + String.format("%.2f", preco));
        lblPrecoInfo.setStyle("-fx-text-fill: #555555;");

        comboCategoria.setOnAction(e -> {
            CategoriaMeiaEntrada cat = comboCategoria.getValue();
            double valorFinal = (cat == CategoriaMeiaEntrada.MEIA_ENTRADA) ? preco / 2 : preco;
            lblPrecoInfo.setText("Valor: R$ " + String.format("%.2f", valorFinal)
                    + (cat == CategoriaMeiaEntrada.MEIA_ENTRADA ? "  (Meia Entrada)" : "  (Inteira)"));
        });

        Button btnConfirmar = new Button("Confirmar");
        btnConfirmar.setStyle("-fx-background-color: #2962ff; -fx-text-fill: white; -fx-font-weight: bold;");
        Button btnCancelar = new Button("Cancelar");

        final boolean[] resultado = {false};

        btnConfirmar.setOnAction(e -> {
            String idadeStr = txtIdade.getText().trim();
            if (!idadeStr.matches("\\d+")) {
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setHeaderText(null);
                err.setContentText("Informe uma idade válida (apenas números).");
                err.showAndWait();
                return;
            }
            int idade = Integer.parseInt(idadeStr);
            if (idade < 0 || idade > 120) {
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setHeaderText(null);
                err.setContentText("Idade inválida.");
                err.showAndWait();
                return;
            }
            categoriasPorAssento.put(nomeAssento, comboCategoria.getValue());
            idadesPorAssento.put(nomeAssento, idade);
            resultado[0] = true;
            dialog.close();
        });

        btnCancelar.setOnAction(e -> dialog.close());

        HBox hbBotoes = new HBox(10, btnCancelar, btnConfirmar);
        hbBotoes.setAlignment(Pos.CENTER_RIGHT);

        VBox vbox = new VBox(12,
                lblTitulo,
                lblCategoria, comboCategoria,
                lblIdade, txtIdade,
                lblPrecoInfo,
                hbBotoes
        );
        vbox.setPadding(new Insets(20));
        vbox.setPrefWidth(320);

        Scene cena = new Scene(vbox);
        dialog.setScene(cena);
        dialog.showAndWait();

        return resultado[0];
    }

    // Utilitários

    private String labelCategoria(CategoriaMeiaEntrada cat) {
        return switch (cat) {
            case INTEIRA      -> "Inteira";
            case MEIA_ENTRADA -> "Meia Entrada";
        };
    }

    // Retorna a idade mínima exigida pela classificação indicativa.

    private int getIdadeLimiteClassificacao(ClassificacaoIndicativa cl) {
        if (cl == null) return 0;
        return switch (cl) {
            case LIVRE       -> 0;
            case DEZ         -> 10;
            case DOZE        -> 12;
            case QUATORZE    -> 14;
            case DEZESSEIS   -> 16;
            case DEZOITO     -> 18;
        };
    }


    public static void ocuparAssentos(LocalDateTime horarioSessao, List<String> assentos) {
        int[][] layout = layoutsPorSessao.get(horarioSessao);
        if (layout == null) return;

        for (String nome : assentos) {
            if (nome == null || nome.length() < 2) continue;
            try {
                int linha  = Character.toUpperCase(nome.charAt(0)) - 'A';
                int coluna = Integer.parseInt(nome.substring(1)) - 1;

                if (linha  >= 0 && linha  < layout.length &&
                    coluna >= 0 && coluna < layout[linha].length &&
                    layout[linha][coluna] != 0) {
                    layout[linha][coluna] = 2;
                }
            } catch (NumberFormatException e) {
                System.err.println("Código de assento inválido: " + nome);
            }
        }
    }


    // Metodo pra liberar/cancelar o Assento
    public static void liberarAssentoNoCache(LocalDateTime horarioSessao, String codigoAssento) {
        int[][] layout = layoutsPorSessao.get(horarioSessao);
        if (layout == null || codigoAssento == null || codigoAssento.length() < 2) return;
        try {
            int linha  = Character.toUpperCase(codigoAssento.charAt(0)) - 'A';
            int coluna = Integer.parseInt(codigoAssento.substring(1)) - 1;

            if (linha  >= 0 && linha  < layout.length &&
                coluna >= 0 && coluna < layout[linha].length) {
                layout[linha][coluna] = 1;
            }
        } catch (NumberFormatException e) {
            System.err.println("Código de assento inválido ao liberar cache: " + codigoAssento);
        }
    }
}
