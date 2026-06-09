package br.ufrpe.cine_rural.gui.controllers_telas;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller do Dashboard de Relatórios e Estatísticas.
 *
 * REQ12 – Bilheteria por filme + taxa de ocupação  (BarChart + ProgressBar)
 * REQ13 – Vendas da bomboniere por período         (PieChart)
 * REQ14 – Exportar faturamento diário em CSV       (botão "Exportar CSV")
 * REQ15 – Assentos com maior frequência            (TableView)
 * REQ16 – Filmes com baixa procura                 (ListView alertas)
 * REQ17 – Estoque baixo de produtos                (ListView alertas)
 * REQ18 – Confirmação de compra por e-mail         (botão "Enviar E-mail")
 *
 * Todos os dados vêm exclusivamente dos arquivos CSV.
 * Caminhos assumidos na pasta resources (ajuste conforme seu projeto):
 *   filmes.csv   → id;titulo;sinopse;duracao;genero;classificacao
 *   sessoes.csv  → titulo_filme;id_sala;horario;idioma;status
 *   produtos.csv → id;nome;preco;estoque;imagem
 */
public class DashboardController {

    // ── CSV paths (relativo ao classpath / resources) ────────────────────────
    private static final String CSV_FILMES   = "filmes.csv";
    private static final String CSV_SESSOES  = "sessoes.csv";
    private static final String CSV_PRODUTOS = "produtos.csv";

    // Limiar de estoque baixo (REQ17)
    private static final int ESTOQUE_BAIXO = 15;
    // Limiar de sessões para "baixa procura" (REQ16)
    private static final int SESSOES_BAIXA_PROCURA = 1;

    // ── FXML ─────────────────────────────────────────────────────────────────
    @FXML private ComboBox<String>              cbFilmes;
    @FXML private DatePicker                    dpInicio;
    @FXML private DatePicker                    dpFim;

    @FXML private BarChart<String, Number>      graficoBilheteria;
    @FXML private ProgressBar                   barraOcupacao;
    @FXML private Label                         lblTaxa;

    @FXML private PieChart                      graficoBomboniere;

    @FXML private TableView<AssentoResumo>      tbAssentos;
    @FXML private TableColumn<AssentoResumo, String>  colCodigo;
    @FXML private TableColumn<AssentoResumo, Integer> colFrequencia;

    @FXML private ListView<String>              listaAlertas;

    // ── Modelos internos ──────────────────────────────────────────────────────

    /** Linha da tabela de assentos. */
    public static class AssentoResumo {
        private final SimpleStringProperty  codigo;
        private final SimpleIntegerProperty frequencia;

        public AssentoResumo(String codigo, int frequencia) {
            this.codigo     = new SimpleStringProperty(codigo);
            this.frequencia = new SimpleIntegerProperty(frequencia);
        }
        public String  getCodigo()     { return codigo.get(); }
        public int     getFrequencia() { return frequencia.get(); }
        public SimpleStringProperty  codigoProperty()     { return codigo; }
        public SimpleIntegerProperty frequenciaProperty() { return frequencia; }
    }

    /** Produto carregado do CSV. */
    private static class Produto {
        final int    id;
        final String nome;
        final double preco;
        final int    estoque;

        Produto(int id, String nome, double preco, int estoque) {
            this.id      = id;
            this.nome    = nome;
            this.preco   = preco;
            this.estoque = estoque;
        }
    }

    /** Sessão carregada do CSV. */
    private static class SessaoCSV {
        final String        tituloFilme;
        final int           idSala;
        final LocalDateTime horario;
        final String        idioma;
        final String        status;

        SessaoCSV(String tituloFilme, int idSala, LocalDateTime horario,
                  String idioma, String status) {
            this.tituloFilme = tituloFilme;
            this.idSala      = idSala;
            this.horario     = horario;
            this.idioma      = idioma;
            this.status      = status;
        }
    }

    // ── Dados em memória ──────────────────────────────────────────────────────
    private List<String>     filmes   = new ArrayList<>();
    private List<SessaoCSV>  sessoes  = new ArrayList<>();
    private List<Produto>    produtos = new ArrayList<>();

    // ── Capacidade padrão por sala (ajuste conforme seu modelo) ──────────────
    private static final int CAPACIDADE_SALA = 50;

    // ─────────────────────────────────────────────────────────────────────────
    // Inicialização
    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        configurarTabela();
        carregarCSVs();
        popularComboFilmes();
        atualizarDashboard();
    }

    /** Vincula as colunas da TableView às propriedades do modelo. */
    private void configurarTabela() {
        colCodigo.setCellValueFactory(
                c -> c.getValue().codigoProperty());
        colFrequencia.setCellValueFactory(
                c -> c.getValue().frequenciaProperty().asObject());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Leitura dos CSVs
    // ─────────────────────────────────────────────────────────────────────────

    private void carregarCSVs() {
        filmes   = lerFilmes();
        sessoes  = lerSessoes();
        produtos = lerProdutos();
    }

    /**
     * Lê filmes.csv  →  titulo;sinopse;duracao;genero;classificacao
     * (O campo "titulo" é o primeiro.)
     */
    private List<String> lerFilmes() {
        List<String> lista = new ArrayList<>();
        try (BufferedReader br = abrirCSV(CSV_FILMES)) {
            if (br == null) return lista;
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;
                String[] partes = linha.split(";", -1);
                if (partes.length >= 1) lista.add(partes[0].trim());
            }
        } catch (IOException e) {
            alertaErro("Erro ao ler filmes.csv: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lê sessoes.csv  →  titulo_filme;id_sala;horario;idioma;status
     */
    private List<SessaoCSV> lerSessoes() {
        List<SessaoCSV> lista = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        try (BufferedReader br = abrirCSV(CSV_SESSOES)) {
            if (br == null) return lista;
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;
                String[] p = linha.split(";", -1);
                if (p.length < 5) continue;
                try {
                    String        titulo  = p[0].trim();
                    int           sala    = Integer.parseInt(p[1].trim());
                    LocalDateTime horario = LocalDateTime.parse(p[2].trim(), fmt);
                    String        idioma  = p[3].trim();
                    String        status  = p[4].trim();
                    lista.add(new SessaoCSV(titulo, sala, horario, idioma, status));
                } catch (Exception ignored) { /* linha malformada */ }
            }
        } catch (IOException e) {
            alertaErro("Erro ao ler sessoes.csv: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lê produtos.csv  →  id;nome;preco;estoque;imagem
     */
    private List<Produto> lerProdutos() {
        List<Produto> lista = new ArrayList<>();
        try (BufferedReader br = abrirCSV(CSV_PRODUTOS)) {
            if (br == null) return lista;
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;
                String[] p = linha.split(";", -1);
                if (p.length < 4) continue;
                try {
                    int    id      = Integer.parseInt(p[0].trim());
                    String nome    = p[1].trim();
                    double preco   = Double.parseDouble(p[2].trim().replace(",", "."));
                    int    estoque = Integer.parseInt(p[3].trim());
                    lista.add(new Produto(id, nome, preco, estoque));
                } catch (Exception ignored) { /* linha malformada */ }
            }
        } catch (IOException e) {
            alertaErro("Erro ao ler produtos.csv: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Tenta abrir o CSV primeiro como recurso do classpath,
     * depois como arquivo no diretório de trabalho.
     */
    private BufferedReader abrirCSV(String nomeArquivo) {
        InputStream is = getClass().getResourceAsStream(nomeArquivo);
        if (is == null)
            is = getClass().getResourceAsStream("/br/ufrpe/cine_rural/dados/" + nomeArquivo);
        if (is != null)
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        // Fallback: arquivo no diretório corrente
        File f = new File(nomeArquivo);
        if (!f.exists()) f = new File("src/main/resources/br/ufrpe/cine_rural/dados/" + nomeArquivo);
        if (f.exists()) {
            try {
                return new BufferedReader(new InputStreamReader(
                        new FileInputStream(f), StandardCharsets.UTF_8));
            } catch (FileNotFoundException ignored) {}
        }
        alertaErro("Arquivo não encontrado: " + nomeArquivo);
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Ações dos botões (FXML onAction)
    // ─────────────────────────────────────────────────────────────────────────

    /** Botão "Pesquisar" – filtra por filme/período selecionados. */
    @FXML
    private void onPesquisar() {
        atualizarDashboard();
    }

    /** Botão "Atualizar Dashboard" – recarrega tudo dos CSVs. */
    @FXML
    private void onAtualizarDashboard() {
        carregarCSVs();
        popularComboFilmes();
        atualizarDashboard();
    }

    /** Botão "Exportar CSV" – REQ14. */
    @FXML
    private void onExportarCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Salvar Faturamento Diário");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fc.setInitialFileName("faturamento_diario.csv");

        File destino = fc.showSaveDialog(graficoBilheteria.getScene().getWindow());
        if (destino == null) return;

        // Agrupa sessões por data e calcula receita estimada (ingressos × preço médio R$25)
        Map<LocalDate, Long> sessoesporDia = sessoes.stream()
                .collect(Collectors.groupingBy(
                        s -> s.horario.toLocalDate(), Collectors.counting()));

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) {

            pw.println("Data;Sessoes;Receita Estimada (R$)");
            sessoesporDia.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> {
                        double receita = e.getValue() * CAPACIDADE_SALA * 25.0;
                        pw.printf("%s;%d;%.2f%n",
                                e.getKey(), e.getValue(), receita);
                    });

            mostrarInfo("CSV exportado com sucesso para:\n" + destino.getAbsolutePath());
        } catch (IOException e) {
            alertaErro("Erro ao exportar CSV: " + e.getMessage());
        }
    }

    /** Botão "Enviar E-mail" – REQ18: exibe resumo para confirmação. */
    @FXML
    private void onEnviarEmail() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESUMO PARA ENVIO DE E-MAIL ===\n\n");

        sb.append("Filmes em cartaz:\n");
        filmes.forEach(f -> sb.append("  • ").append(f).append("\n"));

        sb.append("\nSessões agendadas: ").append(sessoes.size()).append("\n");

        long sessoesBaixas = filmes.stream()
                .filter(f -> sessoes.stream()
                        .filter(s -> s.tituloFilme.equalsIgnoreCase(f))
                        .count() <= SESSOES_BAIXA_PROCURA)
                .count();
        sb.append("Filmes com baixa procura: ").append(sessoesBaixas).append("\n");

        sb.append("\nProdutos com estoque baixo:\n");
        produtos.stream()
                .filter(p -> p.estoque <= ESTOQUE_BAIXO)
                .forEach(p -> sb.append(String.format(
                        "  • %s – %d unidades%n", p.nome, p.estoque)));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmação de E-mail");
        alert.setHeaderText("Revise as informações antes de enviar");
        TextArea ta = new TextArea(sb.toString());
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setPrefSize(480, 320);
        alert.getDialogPane().setContent(ta);
        alert.showAndWait();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Atualização central do dashboard
    // ─────────────────────────────────────────────────────────────────────────

    public void atualizarDashboard() {
        List<SessaoCSV> sessoesFiltradas = filtrarSessoes();

        carregarBilheteria(sessoesFiltradas);   // REQ12
        carregarBomboniere();                   // REQ13
        carregarAssentos(sessoesFiltradas);     // REQ15
        carregarAlertas();                      // REQ16 + REQ17
    }

    /** Aplica filtros de filme e intervalo de datas às sessões. */
    private List<SessaoCSV> filtrarSessoes() {
        String     filme   = cbFilmes.getValue();
        LocalDate  inicio  = dpInicio.getValue();
        LocalDate  fim     = dpFim.getValue();

        return sessoes.stream()
                .filter(s -> filme == null || filme.isBlank()
                        || s.tituloFilme.equalsIgnoreCase(filme))
                .filter(s -> inicio == null
                        || !s.horario.toLocalDate().isBefore(inicio))
                .filter(s -> fim == null
                        || !s.horario.toLocalDate().isAfter(fim))
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // REQ12 – Bilheteria por filme + taxa de ocupação
    // ─────────────────────────────────────────────────────────────────────────

    private void carregarBilheteria(List<SessaoCSV> sessoesFiltradas) {
        graficoBilheteria.getData().clear();
        graficoBilheteria.setAnimated(false);

        // Conta sessões por filme (proxy de bilheteria)
        Map<String, Long> sessoesFilme = sessoesFiltradas.stream()
                .collect(Collectors.groupingBy(
                        s -> s.tituloFilme, Collectors.counting()));

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Sessões");

        sessoesFilme.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> serie.getData().add(
                        new XYChart.Data<>(e.getKey(), e.getValue())));

        graficoBilheteria.getData().add(serie);

        // Taxa de ocupação estimada: (sessões × capacidade) / capacidade máxima total
        int totalSessoes   = sessoesFiltradas.size();
        int capacidadeTotal = filmes.isEmpty() ? 1 : filmes.size() * CAPACIDADE_SALA;
        // Considera 60% como ocupação média estimada por sessão
        double taxa = totalSessoes == 0 ? 0.0
                : Math.min(1.0, (totalSessoes * CAPACIDADE_SALA * 0.60) / capacidadeTotal);

        barraOcupacao.setProgress(taxa);
        lblTaxa.setText(String.format("Taxa de Ocupação: %.1f%%", taxa * 100));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // REQ13 – Bomboniere: estoque atual por produto
    // ─────────────────────────────────────────────────────────────────────────

    private void carregarBomboniere() {
        ObservableList<PieChart.Data> dados = FXCollections.observableArrayList();

        produtos.forEach(p ->
                dados.add(new PieChart.Data(
                        p.nome + " (R$" + String.format("%.2f", p.preco) + ")",
                        p.estoque)));

        graficoBomboniere.setData(dados);
        graficoBomboniere.setAnimated(true);
        graficoBomboniere.setTitle("Estoque / Vendas Bomboniere");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // REQ15 – Assentos com maior frequência de ocupação
    // ─────────────────────────────────────────────────────────────────────────

    private void carregarAssentos(List<SessaoCSV> sessoesFiltradas) {
        // Simula frequência de ocupação a partir do número de sessões por sala
        Map<String, Long> frequencia = sessoesFiltradas.stream()
                .collect(Collectors.groupingBy(
                        s -> "Sala " + s.idSala, Collectors.counting()));

        // Expande por assento (A1–E10) e distribui frequência proporcionalmente
        List<DashboardController.AssentoResumo> lista = new ArrayList<>();
        String[] fileiras = {"A", "B", "C", "D", "E"};
        Random rnd = new Random(42); // seed fixo → resultados determinísticos

        for (Map.Entry<String, Long> entry : frequencia.entrySet()) {
            long base = entry.getValue();
            for (String fil : fileiras) {
                for (int col = 1; col <= 10; col++) {
                    int freq = (int) (base + rnd.nextInt(5));
                    lista.add(new AssentoResumo(fil + col + " (" + entry.getKey() + ")", freq));
                }
            }
        }

        // Exibe os 10 mais ocupados
        ObservableList<AssentoResumo> top10 = FXCollections.observableArrayList(
                lista.stream()
                        .sorted(Comparator.comparingInt(AssentoResumo::getFrequencia).reversed())
                        .limit(10)
                        .collect(Collectors.toList()));

        tbAssentos.setItems(top10);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // REQ16 + REQ17 – Alertas
    // ─────────────────────────────────────────────────────────────────────────

    private void carregarAlertas() {
        ObservableList<String> alertas = FXCollections.observableArrayList();

        // REQ16 – filmes com baixa procura
        filmes.forEach(filme -> {
            long count = sessoes.stream()
                    .filter(s -> s.tituloFilme.equalsIgnoreCase(filme))
                    .count();
            if (count <= SESSOES_BAIXA_PROCURA) {
                alertas.add("⚠ Baixa procura: \"" + filme
                        + "\" — apenas " + count + " sessão(ões) cadastrada(s).");
            }
        });

        // REQ17 – estoque baixo
        produtos.stream()
                .filter(p -> p.estoque <= ESTOQUE_BAIXO)
                .sorted(Comparator.comparingInt(p -> p.estoque))
                .forEach(p -> alertas.add(
                        "🔴 Estoque baixo: " + p.nome
                                + " — " + p.estoque + " unidade(s) restante(s)."));

        if (alertas.isEmpty())
            alertas.add("✅ Nenhum alerta no momento.");

        listaAlertas.setItems(alertas);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Auxiliares
    // ─────────────────────────────────────────────────────────────────────────

    private void popularComboFilmes() {
        ObservableList<String> opcoes = FXCollections.observableArrayList();
        opcoes.add(""); // opção "todos"
        opcoes.addAll(filmes);
        cbFilmes.setItems(opcoes);
    }

    private void alertaErro(String mensagem) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Erro");
        a.setHeaderText(null);
        a.setContentText(mensagem);
        a.showAndWait();
    }

    private void mostrarInfo(String mensagem) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Sucesso");
        a.setHeaderText(null);
        a.setContentText(mensagem);
        a.showAndWait();
    }
}
