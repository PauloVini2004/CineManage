package br.ufrpe.cine_rural.gui.controllers_telas;

import javafx.scene.control.Alert;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class RelatorioBaseController {

    protected static final String CLASSPATH_PREFIX =
            "/br/ufrpe/cine_rural/dados/arquivoscsv/";
    protected static final String DEV_PATH_PREFIX  =
            "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/dados/arquivoscsv/";

    protected static final String CSV_FILMES         = "filmes.csv";
    protected static final String CSV_SESSOES        = "sessoes.csv";
    protected static final String CSV_PRODUTOS       = "produtos.csv";
    protected static final String CSV_VENDAS         = "vendas_ingresso.csv";
    protected static final String CSV_VENDAS_LOJINHA = "vendas_lojinha.csv";


    protected static final int ESTOQUE_BAIXO = 5;

    protected static final int LIMIAR_BAIXA_PROCURA = 10;

    protected static final int CAPACIDADE_SALA = 50;

    private static final DateTimeFormatter FMT_HORARIO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");

    private static final DateTimeFormatter FMT_VENDA =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS][.SSSSSS][.SSS]");


    protected static class Produto {
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

    protected static class SessaoCSV {
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

    protected static class VendaIngresso {
        final LocalDateTime dataVenda;
        final String        filme;
        final String        assento;
        final double        preco;
        final String        cliente;

        VendaIngresso(LocalDateTime dataVenda, String filme,
                      String assento, double preco, String cliente) {
            this.dataVenda = dataVenda;
            this.filme     = filme;
            this.assento   = assento;
            this.preco     = preco;
            this.cliente   = cliente;
        }
    }

    protected static class VendaLojinha {
        final LocalDateTime dataVenda;
        final String        produto;
        final int           quantidade;
        final double        subtotal;
        final String        cliente;

        VendaLojinha(LocalDateTime dataVenda, String produto,
                     int quantidade, double subtotal, String cliente) {
            this.dataVenda  = dataVenda;
            this.produto    = produto;
            this.quantidade = quantidade;
            this.subtotal   = subtotal;
            this.cliente    = cliente;
        }
    }


    protected List<String>        filmes        = new ArrayList<>();
    protected List<SessaoCSV>     sessoes       = new ArrayList<>();
    protected List<Produto>       produtos      = new ArrayList<>();
    protected List<VendaIngresso> vendas        = new ArrayList<>();
    protected List<VendaLojinha>  vendasLojinha = new ArrayList<>();


    protected void carregarCSVs() {
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
                String[] p = linha.split(";", -1);
                if (p.length >= 1) lista.add(p[0].trim());
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
                    LocalDateTime horario = LocalDateTime.parse(p[2].trim(), FMT_HORARIO);
                    String        idioma  = p[3].trim();
                    String        status  = p[4].trim();
                    lista.add(new SessaoCSV(titulo, sala, horario, idioma, status));
                } catch (Exception ignored) {}
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
                } catch (Exception ignored) {}
            }
        } catch (IOException e) {
            alertaErro("Erro ao ler produtos.csv: " + e.getMessage());
        }
        return lista;
    }

    private List<VendaIngresso> lerVendas() {
        List<VendaIngresso> lista = new ArrayList<>();
        try (BufferedReader br = abrirCSV(CSV_VENDAS)) {
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
                    LocalDateTime data    = LocalDateTime.parse(p[0].trim(), FMT_VENDA);
                    String        filme   = p[2].trim();
                    String        assento = p[3].trim();
                    double        preco   = Double.parseDouble(p[5].trim().replace(",", "."));
                    String        cliente = p.length > 6 ? p[6].trim() : "";
                    lista.add(new VendaIngresso(data, filme, assento, preco, cliente));
                } catch (Exception ignored) {}
            }
        } catch (IOException e) {
            alertaErro("Erro ao ler vendas_ingresso.csv: " + e.getMessage());
        }
        return lista;
    }

    private List<VendaLojinha> lerVendasLojinha() {
        List<VendaLojinha> lista = new ArrayList<>();
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
                    LocalDateTime data       = LocalDateTime.parse(p[0].trim(), FMT_VENDA);
                    String        cliente    = p[2].trim();
                    String        produto    = p[3].trim();
                    int           quantidade = Integer.parseInt(p[4].trim());
                    double        subtotal   = Double.parseDouble(p[5].trim().replace(",", "."));
                    lista.add(new VendaLojinha(data, produto, quantidade, subtotal, cliente));
                } catch (Exception ignored) {}
            }
        } catch (IOException e) {
            alertaErro("Erro ao ler vendas_lojinha.csv: " + e.getMessage());
        }
        return lista;
    }


    protected BufferedReader abrirCSV(String nomeArquivo) {
        InputStream is = getClass().getResourceAsStream(CLASSPATH_PREFIX + nomeArquivo);
        if (is != null)
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        File f = new File(DEV_PATH_PREFIX + nomeArquivo);
        if (f.exists()) {
            try {
                return new BufferedReader(
                        new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8));
            } catch (FileNotFoundException ignored) {}
        }

        alertaErro("Arquivo não encontrado: " + nomeArquivo);
        return null;
    }

    protected boolean noIntervalo(LocalDateTime dataVenda, LocalDate inicio, LocalDate fim) {
        LocalDate data = dataVenda.toLocalDate();
        if (inicio != null && data.isBefore(inicio)) return false;
        if (fim    != null && data.isAfter(fim))     return false;
        return true;
    }

    protected void alertaErro(String mensagem) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Erro");
        a.setHeaderText(null);
        a.setContentText(mensagem);
        a.showAndWait();
    }

    protected void mostrarInfo(String mensagem) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Sucesso");
        a.setHeaderText(null);
        a.setContentText(mensagem);
        a.showAndWait();
    }
}
