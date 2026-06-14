package br.ufrpe.cine_rural.gui.controllers_telas;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class RelatorioBomboniereController extends RelatorioBaseController{

    @FXML private DatePicker               dpInicio;
    @FXML private DatePicker               dpFim;

    @FXML private BarChart<String, Number> graficoBomboniere;

    @FXML private TableView<ProdutoResumo>           tbProdutos;
    @FXML private TableColumn<ProdutoResumo, String>  colProduto;
    @FXML private TableColumn<ProdutoResumo, Integer> colQuantidade;
    @FXML private TableColumn<ProdutoResumo, String>  colFaturamento;

    @FXML private ListView<String>         listaAlertasEstoque;


    public static class ProdutoResumo {
        private final SimpleStringProperty  produto;
        private final SimpleIntegerProperty quantidade;
        private final SimpleStringProperty  faturamento;

        public ProdutoResumo(String produto, int quantidade, double faturamento) {
            this.produto     = new SimpleStringProperty(produto);
            this.quantidade  = new SimpleIntegerProperty(quantidade);
            this.faturamento = new SimpleStringProperty(String.format("R$ %.2f", faturamento));
        }
        public String getProduto()     { return produto.get(); }
        public int    getQuantidade()  { return quantidade.get(); }
        public String getFaturamento() { return faturamento.get(); }
        public SimpleStringProperty  produtoProperty()     { return produto; }
        public SimpleIntegerProperty quantidadeProperty()  { return quantidade; }
        public SimpleStringProperty  faturamentoProperty() { return faturamento; }
    }



    @FXML
    public void initialize() {
        configurarColunas();
        carregarCSVs();
        atualizarTela();
    }

    private void configurarColunas() {
        colProduto.setCellValueFactory(c    -> c.getValue().produtoProperty());
        colQuantidade.setCellValueFactory(c -> c.getValue().quantidadeProperty().asObject());
        colFaturamento.setCellValueFactory(c -> c.getValue().faturamentoProperty());
    }



    @FXML
    private void onAtualizar() {
        carregarCSVs();
        atualizarTela();
    }

    @FXML
    private void onVoltar() {
        ScreenManager.getInstance().showRelatorioFilmesScreen();
    }


    @FXML
    private void onExportarCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Salvar Faturamento de Produtos");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fc.setInitialFileName("faturamento_produtos.csv");

        File destino = fc.showSaveDialog(tbProdutos.getScene().getWindow());
        if (destino == null) return;

        LocalDate hoje   = LocalDate.now();
        LocalDate inicio = dpInicio.getValue();
        LocalDate fim    = dpFim.getValue();


        LocalDate periodoInicio = inicio != null ? inicio : hoje;
        LocalDate periodoFim    = fim    != null ? fim    : hoje;

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) {

            DateTimeFormatter fmtExib =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


            pw.println("RELATORIO DE PRODUTOS CINEMANAGER");
            pw.println( LocalDateTime.now().format(fmtExib));

            pw.println();


            double totalIngressosDia = vendas.stream()
                    .filter(v -> v.dataVenda.toLocalDate().equals(hoje))
                    .mapToDouble(v -> v.preco)
                    .sum();

            double totalLojinhaDia = vendasLojinha.stream()
                    .filter(vl -> vl.dataVenda.toLocalDate().equals(hoje))
                    .mapToDouble(vl -> vl.subtotal)
                    .sum();

            pw.println("FATURAMENTO DIARIO TOTAL");
            pw.printf("Data;%s%n", hoje);
            pw.printf("Faturamento Ingressos;%.2f%n", totalIngressosDia);
            pw.printf("Faturamento Loja;%.2f%n",   totalLojinhaDia);
            pw.printf("Faturamento Total Geral;%.2f%n",
                    totalIngressosDia + totalLojinhaDia);
            pw.println();


            pw.printf("FATURAMENTO POR PRODUTO (%s a %s)%n", periodoInicio, periodoFim);
            pw.println("Produto;Quantidade Vendida;Valor Arrecadado");

            Map<String, List<VendaLojinha>> porProduto = vendasLojinha.stream()
                    .filter(vl -> noIntervalo(vl.dataVenda, periodoInicio, periodoFim))
                    .collect(Collectors.groupingBy(vl -> vl.produto));

            new TreeMap<>(porProduto).forEach((produto, lista) -> {
                int    qtd     = lista.stream().mapToInt(vl -> vl.quantidade).sum();
                double receita = lista.stream().mapToDouble(vl -> vl.subtotal).sum();
                pw.printf("%s;%d;%.2f%n", produto, qtd, receita);
            });

            pw.println();


            Set<String> clientesLojinha = new TreeSet<>();
            vendasLojinha.stream()
                    .filter(vl -> noIntervalo(vl.dataVenda, periodoInicio, periodoFim))
                    .filter(vl -> !vl.cliente.isBlank())
                    .map(vl -> vl.cliente)
                    .forEach(clientesLojinha::add);

            if (!clientesLojinha.isEmpty()) {
                pw.printf("CLIENTES %n", periodoInicio, periodoFim);
                pw.println("Cliente;Compras no Periodo;Gasto Total");
                for (String cliente : clientesLojinha) {
                    List<VendaLojinha> compras = vendasLojinha.stream()
                            .filter(vl -> vl.cliente.equalsIgnoreCase(cliente))
                            .filter(vl -> noIntervalo(vl.dataVenda, periodoInicio, periodoFim))
                            .collect(Collectors.toList());
                    int    qtdCompras = compras.size();
                    double gasto      = compras.stream().mapToDouble(vl -> vl.subtotal).sum();
                    pw.printf("%s;%d;%.2f%n", cliente, qtdCompras, gasto);
                }
                pw.println();
            }


            mostrarInfo(String.format(
                    "CSV exportado com sucesso para:%n%s%n%nFaturamento Diário Total (Loja + Ingressos): R$ %.2f",
                    destino.getAbsolutePath(),
                    totalIngressosDia + totalLojinhaDia));

        } catch (IOException e) {
            alertaErro("Erro ao exportar CSV: " + e.getMessage());
        }
    }


    @FXML
    private void onExportarCSVClientes() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Selecionar pasta para exportar os relatórios de clientes");

        File pasta = dc.showDialog(tbProdutos.getScene().getWindow());
        if (pasta == null) return;

        LocalDate inicio = dpInicio.getValue();
        LocalDate fim    = dpFim.getValue();

        List<VendaLojinha> vendasLojinhaFiltradas = vendasLojinha.stream()
                .filter(vl -> noIntervalo(vl.dataVenda, inicio, fim))
                .collect(Collectors.toList());

        List<VendaIngresso> vendasIngressoFiltradas = vendas.stream()
                .filter(v -> noIntervalo(v.dataVenda, inicio, fim))
                .collect(Collectors.toList());

        try {
            exportarCSVProdutos(new File(pasta, "produtos.csv"), vendasLojinhaFiltradas);
            exportarCSVFilmes(new File(pasta, "filmes.csv"), vendasIngressoFiltradas);
            exportarCSVClientes(new File(pasta, "clientes.csv"),
                    vendasLojinhaFiltradas, vendasIngressoFiltradas);

            LocalDate hoje = LocalDate.now();

            double totalIngressosDia = vendas.stream()
                    .filter(v -> v.dataVenda.toLocalDate().equals(hoje))
                    .mapToDouble(v -> v.preco)
                    .sum();

            double totalLojinhaDia = vendasLojinha.stream()
                    .filter(vl -> vl.dataVenda.toLocalDate().equals(hoje))
                    .mapToDouble(vl -> vl.subtotal)
                    .sum();

            mostrarInfo(String.format(
                    "CSVs exportados com sucesso para:%n%s%n%nFaturamento Diário Total (Loja + Ingressos): R$ %.2f",
                    pasta.getAbsolutePath(),
                    totalIngressosDia + totalLojinhaDia));

        } catch (IOException e) {
            alertaErro("Erro ao exportar CSVs de clientes: " + e.getMessage());
        }
    }


    private void exportarCSVProdutos(File destino, List<VendaLojinha> vendasFiltradas) throws IOException {
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) {

            pw.println("Produto(s);Quantidade Vendida;Faturamento por produto");

            Map<String, int[]> acumulado = new LinkedHashMap<>();
            for (VendaLojinha vl : vendasFiltradas) {
                acumulado.computeIfAbsent(vl.produto, p -> new int[]{0, 0});
                acumulado.get(vl.produto)[0] += vl.quantidade;
                acumulado.get(vl.produto)[1]  =
                        (int)(acumulado.get(vl.produto)[1] + vl.subtotal * 100);
            }

            new TreeMap<>(acumulado).forEach((produto, valores) ->
                    pw.printf("%s;%d;%.2f%n", produto, valores[0], valores[1] / 100.0));
        }
    }


    private void exportarCSVFilmes(File destino, List<VendaIngresso> vendasFiltradas) throws IOException {
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) {

            pw.println("Filme(s);Ingresso(s) Vendido(s);Faturamento por filme");

            Map<String, Integer> qtdPorFilme      = new LinkedHashMap<>();
            Map<String, Double>  receitaPorFilme  = new LinkedHashMap<>();

            for (VendaIngresso v : vendasFiltradas) {
                qtdPorFilme.merge(v.filme, 1, Integer::sum);
                receitaPorFilme.merge(v.filme, v.preco, Double::sum);
            }

            new TreeMap<>(qtdPorFilme).forEach((filme, qtd) ->
                    pw.printf("%s;%d;%.2f%n", filme, qtd, receitaPorFilme.getOrDefault(filme, 0.0)));
        }
    }


    private void exportarCSVClientes(File destino,
                                     List<VendaLojinha>  vendasLojinhaFiltradas,
                                     List<VendaIngresso> vendasIngressoFiltradas) throws IOException {

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) {

            pw.println("Cliente(s);Produto(s) Comprado(s);Quantidade;Ingresso(s) Comprado(s);"
                    + "Filme;Gasto por produto;Gasto por Filme;Gasto Total");

            Map<String, ClienteResumo> resumos = new TreeMap<>();

            for (VendaLojinha vl : vendasLojinhaFiltradas) {
                if (vl.cliente == null || vl.cliente.isBlank()) continue;
                ClienteResumo r = resumos.computeIfAbsent(vl.cliente, c -> new ClienteResumo());
                r.produtos.add(vl.produto);
                r.quantidadeProdutos += vl.quantidade;
                r.gastoProduto       += vl.subtotal;
            }

            for (VendaIngresso v : vendasIngressoFiltradas) {
                if (v.cliente == null || v.cliente.isBlank()) continue;
                ClienteResumo r = resumos.computeIfAbsent(v.cliente, c -> new ClienteResumo());
                r.filmes.add(v.filme);
                r.ingressosComprados++;
                r.gastoFilme += v.preco;
            }

            resumos.forEach((cliente, r) -> pw.printf("%s;%s;%d;%d;%s;%.2f;%.2f;%.2f%n",
                    cliente,
                    String.join(", ", r.produtos),
                    r.quantidadeProdutos,
                    r.ingressosComprados,
                    String.join(", ", r.filmes),
                    r.gastoProduto,
                    r.gastoFilme,
                    r.gastoProduto + r.gastoFilme));
        }
    }


    private static class ClienteResumo {
        Set<String> produtos = new TreeSet<>();
        Set<String> filmes   = new TreeSet<>();
        int    quantidadeProdutos = 0;
        int    ingressosComprados = 0;
        double gastoProduto       = 0;
        double gastoFilme         = 0;
    }



    private void atualizarTela() {
        LocalDate inicio = dpInicio.getValue();
        LocalDate fim    = dpFim.getValue();

        List<VendaLojinha> vendasFiltradas = vendasLojinha.stream()
                .filter(vl -> noIntervalo(vl.dataVenda, inicio, fim))
                .collect(Collectors.toList());

        atualizarGrafico(vendasFiltradas);    // REQ13
        atualizarTabela(vendasFiltradas);     // REQ13
        atualizarAlertasEstoque();            // REQ17
    }


    private void atualizarGrafico(List<VendaLojinha> vendasFiltradas) {
        graficoBomboniere.getData().clear();
        graficoBomboniere.setAnimated(false);

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Faturamento (R$)");

        Map<String, Double> receitaPorProduto = vendasFiltradas.stream()
                .collect(Collectors.groupingBy(
                        vl -> vl.produto,
                        Collectors.summingDouble(vl -> vl.subtotal)));

        if (receitaPorProduto.isEmpty()) {
            // Sem vendas no período: exibe estoque como fallback visual
            produtos.forEach(p ->
                    serie.getData().add(new XYChart.Data<>(
                            p.nome + " (estoque)", (Number) p.estoque)));
        } else {
            receitaPorProduto.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .forEach(e -> serie.getData().add(
                            new XYChart.Data<>(e.getKey(), e.getValue())));
        }

        graficoBomboniere.getData().add(serie);
    }


    private void atualizarTabela(List<VendaLojinha> vendasFiltradas) {
        // Acumula em centavos para evitar erro de ponto flutuante
        Map<String, int[]> acumulado = new LinkedHashMap<>();
        for (VendaLojinha vl : vendasFiltradas) {
            acumulado.computeIfAbsent(vl.produto, p -> new int[]{0, 0});
            acumulado.get(vl.produto)[0] += vl.quantidade;
            acumulado.get(vl.produto)[1]  =
                    (int)(acumulado.get(vl.produto)[1] + vl.subtotal * 100);
        }

        ObservableList<ProdutoResumo> linhas = FXCollections.observableArrayList(
                acumulado.entrySet().stream()
                        .sorted((a, b) ->
                                Integer.compare(b.getValue()[1], a.getValue()[1]))
                        .map(e -> new ProdutoResumo(
                                e.getKey(),
                                e.getValue()[0],
                                e.getValue()[1] / 100.0))
                        .collect(Collectors.toList()));

        tbProdutos.setItems(linhas);
    }


    private void atualizarAlertasEstoque() {
        ObservableList<String> alertas = FXCollections.observableArrayList();

        produtos.stream()
                .filter(p -> p.estoque <= ESTOQUE_BAIXO)
                .sorted(Comparator.comparingInt(p -> p.estoque))
                .forEach(p -> alertas.add(
                        " Estoque baixo: " + p.nome
                                + " — " + p.estoque + " unidade(s) restante(s)."));

        if (alertas.isEmpty())
            alertas.add("✅ Estoque normal em todos os produtos.");

        listaAlertasEstoque.setItems(alertas);
    }
}