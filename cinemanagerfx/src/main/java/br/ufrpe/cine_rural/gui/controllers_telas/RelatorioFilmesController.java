package br.ufrpe.cine_rural.gui.controllers_telas;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class RelatorioFilmesController extends RelatorioBaseController{
    // ── FXML ─────────────────────────────────────────────────────────────
    @FXML private ComboBox<String>               cbFilmes;

    @FXML private Label                          lblTotalIngressos;
    @FXML private Label                          lblTaxaOcupacao;
    @FXML private Label                          lblFaturamento;
    @FXML private ProgressBar                    barraOcupacao;

    @FXML private TableView<AssentoResumo>       tbAssentos;
    @FXML private TableColumn<AssentoResumo, String>  colCodigo;
    @FXML private TableColumn<AssentoResumo, Integer> colFrequencia;

    @FXML private ListView<String>               listaAlertasFilmes;

    // ── Modelo de linha ──────────────────────────────────────────────────

    public static class AssentoResumo {
        private final SimpleStringProperty  codigo;
        private final SimpleIntegerProperty frequencia;

        public AssentoResumo(String codigo, int frequencia) {
            this.codigo     = new SimpleStringProperty(codigo);
            this.frequencia = new SimpleIntegerProperty(frequencia);
        }
        public String getCodigo()     { return codigo.get(); }
        public int    getFrequencia() { return frequencia.get(); }
        public SimpleStringProperty  codigoProperty()     { return codigo; }
        public SimpleIntegerProperty frequenciaProperty() { return frequencia; }
    }

    // ── Inicialização ────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        configurarColunas();
        carregarCSVs();
        popularComboFilmes();
        atualizarTela();
    }

    private void configurarColunas() {
        colCodigo.setCellValueFactory(c -> c.getValue().codigoProperty());
        colFrequencia.setCellValueFactory(c -> c.getValue().frequenciaProperty().asObject());
    }

    private void popularComboFilmes() {
        ObservableList<String> opcoes = FXCollections.observableArrayList();
        opcoes.add("");
        opcoes.addAll(filmes);
        cbFilmes.setItems(opcoes);
    }

    // ── Handlers ─────────────────────────────────────────────────────────

    @FXML
    private void onAtualizar() {
        carregarCSVs();
        popularComboFilmes();
        atualizarTela();
    }

    @FXML
    private void onVoltar() {
        ScreenManager.getInstance().showGerenteScreen();
    }

    @FXML
    private void onProximo() {
        ScreenManager.getInstance().showRelatorioBomboniereScreen();
    }

    // ── REQ14 — Exportar CSV de filmes ───────────────────────────────────

    @FXML
    private void onExportarCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Salvar Faturamento de Filmes");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fc.setInitialFileName("faturamento_filmes.csv");

        File destino = fc.showSaveDialog(tbAssentos.getScene().getWindow());
        if (destino == null) return;

        LocalDate hoje = LocalDate.now();

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) {

            DateTimeFormatter fmtExib =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            pw.println("========================================");
            pw.println("RELATORIO DE BILHETERIA - CINE RURAL");
            pw.println("Gerado em: " + LocalDateTime.now().format(fmtExib));
            pw.println("========================================");
            pw.println();

            // Faturamento diário total de ingressos
            double totalDia = vendas.stream()
                    .filter(v -> v.dataVenda.toLocalDate().equals(hoje))
                    .mapToDouble(v -> v.preco)
                    .sum();
            pw.println("FATURAMENTO DIARIO TOTAL");
            pw.printf("Data;%s%n", hoje);
            pw.printf("Faturamento Total (Ingressos);%.2f%n", totalDia);
            pw.println();

            // Faturamento por filme no dia
            pw.println("FATURAMENTO DIARIO POR FILME");
            pw.println("Filme;Ingressos Vendidos;Faturamento");

            Map<String, List<VendaIngresso>> porFilme = vendas.stream()
                    .filter(v -> v.dataVenda.toLocalDate().equals(hoje))
                    .collect(Collectors.groupingBy(v -> v.filme));

            new TreeMap<>(porFilme).forEach((filme, lista) ->
                    pw.printf("%s;%d;%.2f%n",
                            filme,
                            lista.size(),
                            lista.stream().mapToDouble(v -> v.preco).sum()));

            pw.println();

            // Alertas REQ16
            pw.println("ALERTAS — BAIXA PROCURA (limiar: " + LIMIAR_BAIXA_PROCURA + " ingressos)");
            pw.println("Filme;Total Historico de Ingressos");
            filmes.forEach(filme -> {
                long total = vendas.stream()
                        .filter(v -> v.filme.equalsIgnoreCase(filme))
                        .count();
                if (total <= LIMIAR_BAIXA_PROCURA)
                    pw.printf("%s;%d%n", filme, total);
            });

            mostrarInfo("CSV exportado com sucesso para:\n" + destino.getAbsolutePath());

        } catch (IOException e) {
            alertaErro("Erro ao exportar CSV: " + e.getMessage());
        }
    }

    // ── REQ18 — Envio de e-mail ───────────────────────────────────────────

    @FXML
    private void onEnviarEmail() {
        carregarCSVs();
        LocalDate hoje = LocalDate.now();

        // Monta corpo idêntico ao DashboardController original
        StringBuilder corpo = new StringBuilder();
        corpo.append("=== RELATÓRIO DIÁRIO – CINE RURAL ===\n");
        corpo.append("Data: ").append(hoje).append("\n\n");

        // Bilheteria
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

        // Lojinha
        corpo.append("\n── LOJINHA DO DIA ──\n");
        Map<String, int[]> produtosHoje = new LinkedHashMap<>();
        for (VendaLojinha vl : vendasLojinha) {
            if (!vl.dataVenda.toLocalDate().equals(hoje)) continue;
            produtosHoje.computeIfAbsent(vl.produto, p -> new int[]{0, 0});
            produtosHoje.get(vl.produto)[0] += vl.quantidade;
            produtosHoje.get(vl.produto)[1]  =
                    (int)(produtosHoje.get(vl.produto)[1] + vl.subtotal * 100);
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
                        .filter(v -> v.filme.equalsIgnoreCase(f))
                        .count() <= LIMIAR_BAIXA_PROCURA)
                .count();
        if (baixaProcura > 0)
            corpo.append("  ⚠ ").append(baixaProcura)
                    .append(" filme(s) com baixa procura.\n");

        produtos.stream()
                .filter(p -> p.estoque <= ESTOQUE_BAIXO)
                .forEach(p -> corpo.append(String.format(
                        "  🔴 Estoque baixo: %s – %d unidade(s)%n",
                        p.nome, p.estoque)));

        if (baixaProcura == 0 && produtos.stream().noneMatch(p -> p.estoque <= ESTOQUE_BAIXO))
            corpo.append("  Nenhum alerta no momento.\n");

        String corpoFinal = corpo.toString();

        // Preview antes de enviar
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

        enviarEmailSMTP(corpoFinal, hoje.toString());
    }

    // ── Atualização da tela ──────────────────────────────────────────────

    private void atualizarTela() {
        String filme = cbFilmes.getValue();

        List<VendaIngresso> vendasFiltradas = vendas.stream()
                .filter(v -> filme == null || filme.isBlank()
                        || v.filme.equalsIgnoreCase(filme))
                .collect(Collectors.toList());

        List<SessaoCSV> sessoesFiltradas = sessoes.stream()
                .filter(s -> filme == null || filme.isBlank()
                        || s.tituloFilme.equalsIgnoreCase(filme))
                .collect(Collectors.toList());

        atualizarMetricas(vendasFiltradas, sessoesFiltradas);   // REQ12
        atualizarTabelaAssentos(vendasFiltradas);               // REQ15
        atualizarAlertasFilmes();                               // REQ16
    }

    // REQ12 — métricas de bilheteria
    private void atualizarMetricas(List<VendaIngresso> vendasFiltradas,
                                   List<SessaoCSV> sessoesFiltradas) {
        int    totalIngressos  = vendasFiltradas.size();
        double faturamento     = vendasFiltradas.stream().mapToDouble(v -> v.preco).sum();
        int    totalCapacidade = sessoesFiltradas.isEmpty()
                ? 1 : sessoesFiltradas.size() * CAPACIDADE_SALA;
        double taxa = totalIngressos == 0
                ? 0.0 : Math.min(1.0, (double) totalIngressos / totalCapacidade);

        lblTotalIngressos.setText(String.valueOf(totalIngressos));
        lblTaxaOcupacao.setText(String.format("%.1f%%", taxa * 100));
        lblFaturamento.setText(String.format("R$ %.2f", faturamento));
        barraOcupacao.setProgress(taxa);
    }

    // REQ15 — top 10 assentos por frequência
    private void atualizarTabelaAssentos(List<VendaIngresso> vendasFiltradas) {
        Map<String, Long> frequencia = vendasFiltradas.stream()
                .collect(Collectors.groupingBy(v -> v.assento, Collectors.counting()));

        ObservableList<AssentoResumo> top10 = FXCollections.observableArrayList(
                frequencia.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(10)
                        .map(e -> new AssentoResumo(e.getKey(), e.getValue().intValue()))
                        .collect(Collectors.toList()));

        tbAssentos.setItems(top10);
    }

    // REQ16 — alertas de baixa procura
    private void atualizarAlertasFilmes() {
        ObservableList<String> alertas = FXCollections.observableArrayList();

        filmes.forEach(filme -> {
            long ingressos = vendas.stream()
                    .filter(v -> v.filme.equalsIgnoreCase(filme))
                    .count();
            long sessoesCadastradas = sessoes.stream()
                    .filter(s -> s.tituloFilme.equalsIgnoreCase(filme))
                    .count();
            if (ingressos <= LIMIAR_BAIXA_PROCURA)
                alertas.add("⚠ Baixa procura: \"" + filme
                        + "\" — " + ingressos + " ingresso(s) em "
                        + sessoesCadastradas + " sessão(ões).");
        });

        if (alertas.isEmpty())
            alertas.add("✅ Nenhum filme com baixa procura no momento.");

        listaAlertasFilmes.setItems(alertas);
    }

    // ── Envio SMTP ───────────────────────────────────────────────────────

    private void enviarEmailSMTP(String corpoFinal, String dataLabel) {
        try {
            Properties cfg = new Properties();
            InputStream cfgStream = getClass().getResourceAsStream(
                    "/br/ufrpe/cine_rural/gui/email.properties");
            if (cfgStream == null) {
                File cfgFile = new File(
                        "cinemanagerfx/src/main/resources/br/ufrpe/cine_rural/gui/email.properties");
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
            msg.setSubject("Relatório Diário – " + dataLabel);
            msg.setText(corpoFinal + "UTF-8");
            Transport.send(msg);

            mostrarInfo("E-mail enviado com sucesso para:\n" + destinatario);

        } catch (Exception ex) {
            alertaErro("Erro ao enviar e-mail:\n" + ex.getMessage()
                    + "\n\nVerifique o email.properties e sua Senha de App do Google.");
        }
    }
}
