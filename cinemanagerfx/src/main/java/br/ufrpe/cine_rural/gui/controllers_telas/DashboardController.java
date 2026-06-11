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

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.stream.Stream;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
 * Caminhos assumidos na pasta resources:
 *   filmes.csv         → titulo;tipo;duracao;genero;classificacao;imagem
 *   sessoes.csv        → titulo_filme;id_sala;horario;idioma;status
 *   produtos.csv       → id;nome;preco;estoque;imagem
 *   vendas_ingresso.csv→ DataVenda;FormaPagamento;Filme;Assento;Categoria;Preco;Cliente
 */
public class DashboardController {


    private static final String CSV_FILMES          = "filmes.csv";
    private static final String CSV_SESSOES         = "sessoes.csv";
    private static final String CSV_PRODUTOS        = "produtos.csv";
    private static final String CSV_VENDAS          = "vendas_ingresso.csv";
    private static final String CSV_VENDAS_LOJINHA  = "vendas_lojinha.csv";


    private static final String CLASSPATH_PREFIX = "/br/ufrpe/cine_rural/dados/arquivoscsv/";

    private static final String DEV_PATH_PREFIX  = "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/dados/arquivoscsv/";


    private static final int ESTOQUE_BAIXO = 15;

    private static final int SESSOES_BAIXA_PROCURA = 1;

    // Formatter flexível: aceita "2026-06-12T19:30" e "2026-06-12T19:30:00"
    private static final DateTimeFormatter FMT_HORARIO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");

    // ── FXML ─────────────────────────────────────────────────────────────────
    @FXML private ComboBox<String>              cbFilmes;
    @FXML private DatePicker                    dpInicio;
    @FXML private DatePicker                    dpFim;

    @FXML private BarChart<String, Number>      graficoBilheteria;
    @FXML private ProgressBar                   barraOcupacao;
    @FXML private Label                         lblTaxa;

    @FXML private TableView<FilmeReceita>        tbReceitaFilmes;
    @FXML private TableColumn<FilmeReceita, String>  colFilme;
    @FXML private TableColumn<FilmeReceita, String>  colReceita;

    @FXML private PieChart                      graficoBomboniere;

    @FXML private TableView<AssentoResumo>      tbAssentos;
    @FXML private TableColumn<AssentoResumo, String>  colCodigo;
    @FXML private TableColumn<AssentoResumo, Integer> colFrequencia;

    @FXML private ListView<String>              listaAlertas;




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


    public static class FilmeReceita {
        private final SimpleStringProperty filme;
        private final SimpleStringProperty receita;

        public FilmeReceita(String filme, double receita) {
            this.filme   = new SimpleStringProperty(filme);
            this.receita = new SimpleStringProperty(String.format("R$ %.2f", receita));
        }
        public String getFilme()   { return filme.get(); }
        public String getReceita() { return receita.get(); }
        public SimpleStringProperty filmeProperty()   { return filme; }
        public SimpleStringProperty receitaProperty() { return receita; }
    }


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


    private static class VendaIngresso {
        final LocalDateTime dataVenda;
        final String        filme;
        final String        assento;
        final double        preco;
        final String        cliente;

        VendaIngresso(LocalDateTime dataVenda, String filme, String assento, double preco, String cliente) {
            this.dataVenda = dataVenda;
            this.filme     = filme;
            this.assento   = assento;
            this.preco     = preco;
            this.cliente   = cliente;
        }
    }



    private static class VendaLojinha {
        final LocalDateTime dataVenda;
        final String        produto;
        final int           quantidade;
        final double        subtotal;
        final String        cliente;

        VendaLojinha(LocalDateTime dataVenda, String produto, int quantidade, double subtotal, String cliente) {
            this.dataVenda  = dataVenda;
            this.produto    = produto;
            this.quantidade = quantidade;
            this.subtotal   = subtotal;
            this.cliente    = cliente;
        }
    }


    private List<String>         filmes   = new ArrayList<>();
    private List<SessaoCSV>      sessoes  = new ArrayList<>();
    private List<Produto>        produtos = new ArrayList<>();
    private List<VendaIngresso>  vendas   = new ArrayList<>();
    private List<VendaLojinha>   vendasLojinha = new ArrayList<>();


    private static final int CAPACIDADE_SALA = 50;

    @FXML
    public void initialize() {
        configurarTabela();
        carregarCSVs();
        popularComboFilmes();
        atualizarDashboard();
    }


    private void configurarTabela() {
        colCodigo.setCellValueFactory(
                c -> c.getValue().codigoProperty());
        colFrequencia.setCellValueFactory(
                c -> c.getValue().frequenciaProperty().asObject());

        colFilme.setCellValueFactory(
                c -> c.getValue().filmeProperty());
        colReceita.setCellValueFactory(
                c -> c.getValue().receitaProperty());
    }



    private void carregarCSVs() {
        filmes        = lerFilmes();
        sessoes       = lerSessoes();
        produtos      = lerProdutos();
        vendas        = lerVendas();
        vendasLojinha = lerVendasLojinha();
    }


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


    private List<SessaoCSV> lerSessoes() {
        List<SessaoCSV> lista = new ArrayList<>();
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
                    // FMT_HORARIO aceita "HH:mm" e "HH:mm:ss"
                    LocalDateTime horario = LocalDateTime.parse(p[2].trim(), FMT_HORARIO);
                    String        idioma  = p[3].trim();
                    String        status  = p[4].trim();
                    lista.add(new SessaoCSV(titulo, sala, horario, idioma, status));
                } catch (Exception ignored) { /* linha malformada — ignora silenciosamente */ }
            }
        } catch (IOException e) {
            alertaErro("Erro ao ler sessoes.csv: " + e.getMessage());
        }
        return lista;
    }


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
                } catch (Exception ignored) { /* linha malformada — ignora silenciosamente */ }
            }
        } catch (IOException e) {
            alertaErro("Erro ao ler produtos.csv: " + e.getMessage());
        }
        return lista;
    }


    private List<VendaIngresso> lerVendas() {
        List<VendaIngresso> lista = new ArrayList<>();
        // Formatter flexivel: aceita segundos com ou sem fracoes (nanosegundos incluidos)
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS][.SSSSSS][.SSS]");
        try (BufferedReader br = abrirCSV(CSV_VENDAS)) {
            if (br == null) return lista;
            String linha;
            boolean primeiraLinha = true;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;
                if (primeiraLinha) { primeiraLinha = false; continue; } // pula cabecalho
                String[] p = linha.split(";", -1);
                if (p.length < 6) continue;
                try {
                    LocalDateTime data    = LocalDateTime.parse(p[0].trim(), fmt);
                    String        filme   = p[2].trim();
                    String        assento = p[3].trim();
                    double        preco   = Double.parseDouble(p[5].trim().replace(",", "."));
                    String        cliente = p.length > 6 ? p[6].trim() : "";
                    lista.add(new VendaIngresso(data, filme, assento, preco, cliente));
                } catch (Exception ignored) { /* linha malformada */ }
            }
        } catch (IOException e) {
            alertaErro("Erro ao ler vendas_ingresso.csv: " + e.getMessage());
        }
        return lista;
    }


    private List<VendaLojinha> lerVendasLojinha() {
        List<VendaLojinha> lista = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS][.SSSSSS][.SSS]");
        try (BufferedReader br = abrirCSV(CSV_VENDAS_LOJINHA)) {
            if (br == null) return lista;
            String linha;
            boolean primeiraLinha = true;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;
                if (primeiraLinha) { primeiraLinha = false; continue; }
                String[] p = linha.split(";", -1);
                if (p.length < 6) continue;
                try {
                    LocalDateTime data       = LocalDateTime.parse(p[0].trim(), fmt);
                    String        cliente    = p[2].trim();
                    String        produto    = p[3].trim();
                    int           quantidade = Integer.parseInt(p[4].trim());
                    double        subtotal   = Double.parseDouble(p[5].trim().replace(",", "."));
                    lista.add(new VendaLojinha(data, produto, quantidade, subtotal, cliente));
                } catch (Exception ignored) { /* linha malformada */ }
            }
        } catch (IOException e) {
            alertaErro("Erro ao ler vendas_lojinha.csv: " + e.getMessage());
        }
        return lista;
    }


    private BufferedReader abrirCSV(String nomeArquivo) {

        InputStream is = getClass().getResourceAsStream(CLASSPATH_PREFIX + nomeArquivo);
        if (is != null)
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));


        File f = new File(DEV_PATH_PREFIX + nomeArquivo);
        if (f.exists()) {
            try { return new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)); }
            catch (FileNotFoundException ignored) {}
        }

        alertaErro("Arquivo nao encontrado: " + nomeArquivo);
        return null;
    }



    @FXML
    private void onPesquisar() {
        atualizarDashboard();
    }


    @FXML
    private void onAtualizarDashboard() {
        carregarCSVs();
        popularComboFilmes();
        atualizarDashboard();
    }

    // Botão "Exportar CSV"
    @FXML
    private void onExportarCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Salvar Faturamento Diário");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fc.setInitialFileName("faturamento_diario.csv");

        File destino = fc.showSaveDialog(graficoBilheteria.getScene().getWindow());
        if (destino == null) return;


        // Não depende de bater com sessoes.csv (nomes e datas podem divergir)
        Map<LocalDate, Map<String, List<VendaIngresso>>> ingressosPorDiaFilme = vendas.stream()
                .collect(Collectors.groupingBy(
                        v -> v.dataVenda.toLocalDate(),
                        Collectors.groupingBy(v -> v.filme)));


        Map<LocalDate, Map<String, int[]>> lojinhaPorDiaProduto = new TreeMap<>();
        for (VendaLojinha vl : vendasLojinha) {
            LocalDate dia = vl.dataVenda.toLocalDate();
            lojinhaPorDiaProduto
                    .computeIfAbsent(dia, d -> new LinkedHashMap<>())
                    .computeIfAbsent(vl.produto, p -> new int[]{0, 0});
            int[] acc = lojinhaPorDiaProduto.get(dia).get(vl.produto);
            acc[0] += vl.quantidade;                       // total de unidades
            acc[1]  = (int)(acc[1] + vl.subtotal * 100);  // centavos para evitar float
        }


        Set<LocalDate> todosDias = new TreeSet<>();
        ingressosPorDiaFilme.keySet().forEach(todosDias::add);
        lojinhaPorDiaProduto.keySet().forEach(todosDias::add);

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) {

            // Cabeçalho
            pw.println("========================================");
            pw.println("RELATORIO GERAL - CINE RURAL");
            pw.println("Gerado em: " +
                    LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            pw.println("========================================");
            pw.println();

            double totalReceitaIngressos =
                    vendas.stream()
                            .mapToDouble(v -> v.preco)
                            .sum();

            double totalReceitaLojinha =
                    vendasLojinha.stream()
                            .mapToDouble(v -> v.subtotal)
                            .sum();

            long totalClientes =
                    Stream.concat(
                                    vendas.stream().map(v -> v.cliente),
                                    vendasLojinha.stream().map(v -> v.cliente)
                            )
                            .filter(c -> !c.isBlank())
                            .distinct()
                            .count();

            pw.println("RESUMO GERAL");
            pw.println("Indicador;Valor");

            pw.printf("Receita Ingressos;%.2f%n", totalReceitaIngressos);
            pw.printf("Receita Lojinha;%.2f%n", totalReceitaLojinha);
            pw.printf("Receita Total;%.2f%n",
                    totalReceitaIngressos + totalReceitaLojinha);

            pw.printf("Clientes Unicos;%d%n", totalClientes);

            pw.println();

            //filmes
            pw.println("FILMES");
            pw.println("Filme;Ingressos Vendidos;Receita");

            Map<String, List<VendaIngresso>> vendasPorFilme =
                    vendas.stream()
                            .collect(Collectors.groupingBy(v -> v.filme));

            for (Map.Entry<String, List<VendaIngresso>> entry :
                    new TreeMap<>(vendasPorFilme).entrySet()) {

                int quantidade = entry.getValue().size();

                double receita = entry.getValue()
                        .stream()
                        .mapToDouble(v -> v.preco)
                        .sum();

                pw.printf("%s;%d;%.2f%n",
                        entry.getKey(),
                        quantidade,
                        receita);
            }

            pw.println();


            //Produtos
            pw.println("PRODUTOS");
            pw.println("Produto;Quantidade Vendida;Receita");

            Map<String, List<VendaLojinha>> vendasPorProduto =
                    vendasLojinha.stream()
                            .collect(Collectors.groupingBy(v -> v.produto));

            for (Map.Entry<String, List<VendaLojinha>> entry :
                    new TreeMap<>(vendasPorProduto).entrySet()) {

                int quantidade = entry.getValue()
                        .stream()
                        .mapToInt(v -> v.quantidade)
                        .sum();

                double receita = entry.getValue()
                        .stream()
                        .mapToDouble(v -> v.subtotal)
                        .sum();

                pw.printf("%s;%d;%.2f%n",
                        entry.getKey(),
                        quantidade,
                        receita);
            }

            pw.println();



            // Coleta todos os clientes únicos de ambas as fontes
            Set<String> todosClientes = new TreeSet<>();
            vendas.stream()
                    .filter(v -> !v.cliente.isBlank())
                    .map(v -> v.cliente)
                    .forEach(todosClientes::add);
            vendasLojinha.stream()
                    .filter(vl -> !vl.cliente.isBlank())
                    .map(vl -> vl.cliente)
                    .forEach(todosClientes::add);

            if (!todosClientes.isEmpty()) {
                pw.println(); // linha em branco separando seções
                pw.println("CLIENTES");
                pw.println("Cliente;Ingressos Comprados;Compras Lojinha;Gasto Total");

                for (String cliente : todosClientes) {
                    // ── dados de ingresso deste cliente ──────────────────────────
                    List<VendaIngresso> ingCliente = vendas.stream()
                            .filter(v -> v.cliente.equalsIgnoreCase(cliente))
                            .collect(Collectors.toList());

                    int    qtdIngressos   = ingCliente.size();
                    double gastoIngressos = ingCliente.stream().mapToDouble(v -> v.preco).sum();
                    String filmesAssistidos = ingCliente.stream()
                            .map(v -> v.filme)
                            .distinct()
                            .sorted()
                            .collect(Collectors.joining(" | "));


                    List<VendaLojinha> lojCliente = vendasLojinha.stream()
                            .filter(vl -> vl.cliente.equalsIgnoreCase(cliente))
                            .collect(Collectors.toList());

                    int    qtdComprasLojinha = lojCliente.size();
                    double gastoLojinha      = lojCliente.stream().mapToDouble(vl -> vl.subtotal).sum();
                    String produtosComprados = lojCliente.stream()
                            .map(vl -> vl.produto)
                            .distinct()
                            .sorted()
                            .collect(Collectors.joining(" | "));

                    double gastoTotal = gastoIngressos + gastoLojinha;

                    pw.printf("%s;%d;%d;%.2f%n",
                            cliente,
                            qtdIngressos,
                            qtdComprasLojinha,
                            gastoTotal);
                }


                long   totalClientesUnicos = todosClientes.size();
                double totalGeralIngressos = vendas.stream().mapToDouble(v -> v.preco).sum();
                double totalGeralLojinha   = vendasLojinha.stream().mapToDouble(vl -> vl.subtotal).sum();
                pw.println();
                pw.println();
                pw.println("TOTAL CLIENTES");

                pw.printf("Clientes Unicos;%d%n",
                        totalClientesUnicos);

                pw.printf("Receita Ingressos;%.2f%n",
                        totalGeralIngressos);

                pw.printf("Receita Lojinha;%.2f%n",
                        totalGeralLojinha);

                pw.printf("Receita Total;%.2f%n",
                        totalGeralIngressos + totalGeralLojinha);
            }

            pw.println();
            pw.println("ALERTAS");
            pw.println("Tipo;Descricao");

            //baixa porcura
            filmes.forEach(filme -> {

                long ingressos = vendas.stream()
                        .filter(v -> v.filme.equalsIgnoreCase(filme))
                        .count();

                if (ingressos <= SESSOES_BAIXA_PROCURA) {

                    pw.printf("Baixa Procura;%s%n",
                            filme);
                }
            });

            //estoque biaxo

            produtos.stream()
                    .filter(p -> p.estoque <= ESTOQUE_BAIXO)
                    .forEach(p ->
                            pw.printf("Estoque Baixo;%s (%d unidades)%n",
                                    p.nome,
                                    p.estoque));


            mostrarInfo("CSV exportado com sucesso para:\n" + destino.getAbsolutePath());
        } catch (IOException e) {
            alertaErro("Erro ao exportar CSV: " + e.getMessage());
        }

    }


    @FXML
    private void onEnviarEmail() {

        LocalDate hoje = LocalDate.now();
        StringBuilder corpo = new StringBuilder();
        corpo.append("=== RELATÓRIO DIÁRIO – CINE RURAL ===\n");
        corpo.append("Data: ").append(hoje).append("\n\n");

        // Ingressos vendidos hoje por filme
        corpo.append("── BILHETERIA DO DIA ──\n");
        Map<String, List<VendaIngresso>> ingressosHoje = vendas.stream()
                .filter(v -> v.dataVenda.toLocalDate().equals(hoje))
                .collect(Collectors.groupingBy(v -> v.filme));

        if (ingressosHoje.isEmpty()) {
            corpo.append("  Nenhum ingresso vendido hoje.\n");
        } else {
            double totalIngressos = 0;
            for (Map.Entry<String, List<VendaIngresso>> e :
                    new TreeMap<>(ingressosHoje).entrySet()) {
                double receita = e.getValue().stream().mapToDouble(v -> v.preco).sum();
                totalIngressos += receita;
                corpo.append(String.format("  • %s – %d ingresso(s) – R$ %.2f%n",
                        e.getKey(), e.getValue().size(), receita));
            }
            corpo.append(String.format("  TOTAL INGRESSOS: R$ %.2f%n", totalIngressos));
        }

        // Produtos vendidos hoje
        corpo.append("\n── LOJINHA DO DIA ──\n");
        Map<String, int[]> produtosHoje = new LinkedHashMap<>();
        for (VendaLojinha vl : vendasLojinha) {
            if (!vl.dataVenda.toLocalDate().equals(hoje)) continue;
            produtosHoje.computeIfAbsent(vl.produto, p -> new int[]{0, 0});
            produtosHoje.get(vl.produto)[0] += vl.quantidade;
            produtosHoje.get(vl.produto)[1]  = (int)(produtosHoje.get(vl.produto)[1] + vl.subtotal * 100);
        }

        if (produtosHoje.isEmpty()) {
            corpo.append("  Nenhum produto vendido hoje.\n");
        } else {
            double totalLojinha = 0;
            for (Map.Entry<String, int[]> e : new TreeMap<>(produtosHoje).entrySet()) {
                double receita = e.getValue()[1] / 100.0;
                totalLojinha += receita;
                corpo.append(String.format("  • %s – %d unidade(s) – R$ %.2f%n",
                        e.getKey(), e.getValue()[0], receita));
            }
            corpo.append(String.format("  TOTAL LOJINHA: R$ %.2f%n", totalLojinha));
        }

        // Alertas
        corpo.append("\n── ALERTAS ──\n");
        long baixaProcura = filmes.stream()
                .filter(f -> vendas.stream()
                        .filter(v -> v.filme.equalsIgnoreCase(f)).count() <= SESSOES_BAIXA_PROCURA)
                .count();
        if (baixaProcura > 0)
            corpo.append("  ⚠ ").append(baixaProcura).append(" filme(s) com baixa procura.\n");

        produtos.stream()
                .filter(p -> p.estoque <= ESTOQUE_BAIXO)
                .forEach(p -> corpo.append(String.format(
                        "  🔴 Estoque baixo: %s – %d unidade(s)%n", p.nome, p.estoque)));

        if (baixaProcura == 0 && produtos.stream().noneMatch(p -> p.estoque <= ESTOQUE_BAIXO))
            corpo.append("  Nenhum alerta no momento.\n");

        String corpoFinal = corpo.toString();


        Alert preview = new Alert(Alert.AlertType.CONFIRMATION);
        preview.setTitle("Confirmar Envio de E-mail");
        preview.setHeaderText("Revise o relatório antes de enviar:");
        TextArea ta = new TextArea(corpoFinal);
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setPrefSize(500, 340);
        preview.getDialogPane().setContent(ta);

        Optional<ButtonType> resultado = preview.showAndWait();
        if (resultado.isEmpty() || resultado.get() != ButtonType.OK) return;


        try {
            Properties cfg = new Properties();
            // Procura o email.properties na mesma pasta dos recursos da GUI
            InputStream cfgStream = getClass().getResourceAsStream("/br/ufrpe/cine_rural/gui/email.properties");
            if (cfgStream == null) {
                // fallback desenvolvimento — caminho relativo ao working directory
                File cfgFile = new File("cinemanagerfx/src/main/resources/br/ufrpe/cine_rural/gui/email.properties");
                if (cfgFile.exists()) cfgStream = new FileInputStream(cfgFile);
            }
            if (cfgStream == null) {
                alertaErro("Arquivo email.properties não encontrado.\n"
                        + "Crie-o em src/main/resources/br/ufrpe/cine_rural/gui/");
                return;
            }
            cfg.load(new InputStreamReader(cfgStream, StandardCharsets.UTF_8));

            String remetente    = cfg.getProperty("email.remetente");
            String senha        = cfg.getProperty("email.senha");
            String destinatario = cfg.getProperty("email.destinatario");

            Properties smtp = new Properties();
            smtp.put("mail.smtp.host",            "smtp.gmail.com");
            smtp.put("mail.smtp.port",            "587");
            smtp.put("mail.smtp.auth",            "true");
            smtp.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(smtp, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(remetente, senha);
                }
            });

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(remetente, "Cine Rural – Sistema"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            msg.setSubject("Relatório Diário – " + hoje);
            msg.setText(corpoFinal + "UTF-8");
            Transport.send(msg);

            mostrarInfo("E-mail enviado com sucesso para:\n" + destinatario);

        } catch (Exception ex) {
            alertaErro("Erro ao enviar e-mail:\n" + ex.getMessage()
                    + "\n\nVerifique o email.properties e sua Senha de App do Google.");
        }
    }

    public void atualizarDashboard() {
        List<SessaoCSV> sessoesFiltradas = filtrarSessoes();

        carregarBilheteria(sessoesFiltradas);   // REQ12
        carregarReceitaFilmes();                // REQ12 – tabela receita por filme
        carregarBomboniere();                   // REQ13
        carregarAssentos(sessoesFiltradas);     // REQ15
        carregarAlertas();                      // REQ16 + REQ17
    }


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



    private void carregarBilheteria(List<SessaoCSV> sessoesFiltradas) {
        graficoBilheteria.getData().clear();
        graficoBilheteria.setAnimated(false);

        // Filtrar vendas pelo mesmo intervalo de datas e filme selecionados
        String    filme  = cbFilmes.getValue();
        LocalDate inicio = dpInicio.getValue();
        LocalDate fim    = dpFim.getValue();

        List<VendaIngresso> vendasFiltradas = vendas.stream()
                .filter(v -> filme == null || filme.isBlank()
                        || v.filme.equalsIgnoreCase(filme))
                .filter(v -> inicio == null
                        || !v.dataVenda.toLocalDate().isBefore(inicio))
                .filter(v -> fim == null
                        || !v.dataVenda.toLocalDate().isAfter(fim))
                .collect(Collectors.toList());

        // REQ12: receita real por filme (soma dos precos dos ingressos vendidos)
        Map<String, Double> receitaFilme = vendasFiltradas.stream()
                .collect(Collectors.groupingBy(
                        v -> v.filme, Collectors.summingDouble(v -> v.preco)));

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Receita (R$)");

        receitaFilme.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(e -> serie.getData().add(
                        new XYChart.Data<>(e.getKey(), e.getValue())));

        graficoBilheteria.getData().add(serie);

        // Taxa de ocupacao real: ingressos vendidos / capacidade total das sessoes filtradas
        int totalIngressos  = vendasFiltradas.size();
        int totalCapacidade = sessoesFiltradas.isEmpty() ? 1
                : sessoesFiltradas.size() * CAPACIDADE_SALA;
        double taxa = totalIngressos == 0 ? 0.0
                : Math.min(1.0, (double) totalIngressos / totalCapacidade);

        barraOcupacao.setProgress(taxa);
        lblTaxa.setText(String.format("Taxa de Ocupacao: %.1f%% (%d ingressos / %d lugares)",
                taxa * 100, totalIngressos, totalCapacidade));
    }



    private void carregarReceitaFilmes() {
        String    filme  = cbFilmes.getValue();
        LocalDate inicio = dpInicio.getValue();
        LocalDate fim    = dpFim.getValue();

        Map<String, Double> receitaPorFilme = vendas.stream()
                .filter(v -> filme == null || filme.isBlank()
                        || v.filme.equalsIgnoreCase(filme))
                .filter(v -> inicio == null || !v.dataVenda.toLocalDate().isBefore(inicio))
                .filter(v -> fim    == null || !v.dataVenda.toLocalDate().isAfter(fim))
                .collect(Collectors.groupingBy(
                        v -> v.filme,
                        Collectors.summingDouble(v -> v.preco)));

        ObservableList<FilmeReceita> linhas = FXCollections.observableArrayList(
                receitaPorFilme.entrySet().stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                        .map(e -> new FilmeReceita(e.getKey(), e.getValue()))
                        .collect(Collectors.toList()));

        tbReceitaFilmes.setItems(linhas);
    }

    private void carregarBomboniere() {
        ObservableList<PieChart.Data> dados = FXCollections.observableArrayList();

        LocalDate inicio = dpInicio.getValue();
        LocalDate fim    = dpFim.getValue();

        // REQ13: receita real por produto da lojinha no período
        Map<String, Double> receitaPorProduto = vendasLojinha.stream()
                .filter(vl -> inicio == null || !vl.dataVenda.toLocalDate().isBefore(inicio))
                .filter(vl -> fim    == null || !vl.dataVenda.toLocalDate().isAfter(fim))
                .collect(Collectors.groupingBy(
                        vl -> vl.produto,
                        Collectors.summingDouble(vl -> vl.subtotal)));

        if (receitaPorProduto.isEmpty()) {
            // Sem vendas no período: exibe estoque como fallback
            produtos.forEach(p -> dados.add(new PieChart.Data(
                    p.nome + " (estoque: " + p.estoque + ")", p.estoque)));
            graficoBomboniere.setTitle("Estoque Bomboniere (sem vendas no periodo)");
        } else {
            double totalLojinha = receitaPorProduto.values().stream()
                    .mapToDouble(Double::doubleValue).sum();
            receitaPorProduto.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .forEach(e -> dados.add(new PieChart.Data(
                            e.getKey() + " (R$" + String.format("%.2f", e.getValue()) + ")",
                            e.getValue())));
            graficoBomboniere.setTitle(String.format(
                    "Faturamento Bomboniere no Período – Total: R$ %.2f", totalLojinha));
        }

        graficoBomboniere.setData(dados);
        graficoBomboniere.setAnimated(true);
    }



    private void carregarAssentos(List<SessaoCSV> sessoesFiltradas) {
        // REQ15: frequencia real de cada assento a partir das vendas de ingresso
        String    filme  = cbFilmes.getValue();
        LocalDate inicio = dpInicio.getValue();
        LocalDate fim    = dpFim.getValue();

        Map<String, Long> frequencia = vendas.stream()
                .filter(v -> filme == null || filme.isBlank()
                        || v.filme.equalsIgnoreCase(filme))
                .filter(v -> inicio == null
                        || !v.dataVenda.toLocalDate().isBefore(inicio))
                .filter(v -> fim == null
                        || !v.dataVenda.toLocalDate().isAfter(fim))
                .collect(Collectors.groupingBy(v -> v.assento, Collectors.counting()));

        ObservableList<AssentoResumo> top10 = FXCollections.observableArrayList(
                frequencia.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(10)
                        .map(e -> new AssentoResumo(e.getKey(), e.getValue().intValue()))
                        .collect(Collectors.toList()));

        tbAssentos.setItems(top10);
    }


    private void carregarAlertas() {
        ObservableList<String> alertas = FXCollections.observableArrayList();

        // REQ16 – filmes com baixa procura: baseado em ingressos vendidos reais
        filmes.forEach(filme -> {
            long ingressos = vendas.stream()
                    .filter(v -> v.filme.equalsIgnoreCase(filme))
                    .count();
            long sessoesCadastradas = sessoes.stream()
                    .filter(s -> s.tituloFilme.equalsIgnoreCase(filme))
                    .count();
            if (ingressos <= SESSOES_BAIXA_PROCURA) {
                alertas.add("⚠ Baixa procura: \"" + filme
                        + "\" — " + ingressos + " ingresso(s) vendido(s) em "
                        + sessoesCadastradas + " sessão(ões).");
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
    }@FXML
    private void onVoltar() {
        ScreenManager.getInstance().showGerenteScreen();
    }


}