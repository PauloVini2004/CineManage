package br.ufrpe.cine_rural.dados.implemento;

import br.ufrpe.cine_rural.dados.interfaces.IRepositorioVendaIngresso;
import br.ufrpe.cine_rural.model.Ingresso;
import br.ufrpe.cine_rural.model.VendaIngresso;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RepositorioVendaIngressoImpl implements IRepositorioVendaIngresso {

    private static RepositorioVendaIngressoImpl instancia;
    private final ArrayList<VendaIngresso> vendas;

    // Caminho relativo do CSV (igual ao padrão do projeto)
    private static final String CAMINHO_CSV =
            "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/dados/arquivoscsv/vendas_ingresso.csv";

    // Cabeçalho com a nova coluna HorarioSessao
    private static final String CABECALHO =
            "DataVenda;FormaPagamento;Filme;Assento;Categoria;Preco;Cliente;Idade;HorarioSessao";

    private RepositorioVendaIngressoImpl() {
        vendas = new ArrayList<>();
    }

    public static synchronized RepositorioVendaIngressoImpl getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioVendaIngressoImpl();
        }
        return instancia;
    }

    @Override
    public void cadastrar(VendaIngresso venda) {
        vendas.add(venda);
        salvarVendaCSV(venda);
    }

    @Override
    public ArrayList<VendaIngresso> listar() {
        return vendas;
    }

    /*
     * Retorna o conjunto de códigos de assento (ex: "E6", "F5") que já foram
     * vendidos para a sessão identificada pelo {@code horarioSessao} informado.
     * Lê diretamente o CSV para garantir persistência mesmo após reinicialização.
     *
     * horarioSessao horário exato da sessão (chave única em RepositorioSessaoImpl)
     * @return conjunto imutável de códigos de assento ocupados
     */
    public Set<String> carregarAssentosOcupados(LocalDateTime horarioSessao) {
        Set<String> ocupados = new HashSet<>();
        File arquivo = new File(CAMINHO_CSV);
        if (!arquivo.exists()) return ocupados;

        String chave = horarioSessao.toString();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            boolean primeiraLinha = true;
            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) { primeiraLinha = false; continue; } // pula cabeçalho
                if (linha.isBlank()) continue;

                String[] col = linha.split(";", -1);
                // Formato novo: [0]DataVenda [1]FormaPagamento [2]Filme [3]Assento
                //               [4]Categoria [5]Preco [6]Cliente [7]Idade [8]HorarioSessao
                if (col.length >= 9) {
                    String horarioCsv = col[8].trim();
                    if (chave.equals(horarioCsv)) {
                        String assento = col[3].trim();
                        if (!assento.isEmpty()) ocupados.add(assento);
                    }
                }
                // Retrocompatibilidade: linhas antigas (sem HorarioSessao) são ignoradas
                // para ocupação — elas não têm sessão identificável.
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler assentos ocupados do CSV: " + e.getMessage());
        }

        return ocupados;
    }

    // Persistência

    private void salvarVendaCSV(VendaIngresso venda) {

        System.out.println("ENTROU NO salvarVendaCSV");
        System.out.println("Ingressos: " + venda.getIngressos().size());

        File arquivo = new File(CAMINHO_CSV);
        System.out.println("CSV EM: " + arquivo.getAbsolutePath());

        File pasta = arquivo.getParentFile();
        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs();
        }

        boolean arquivoNovo = !arquivo.exists();

        try (FileWriter fw = new FileWriter(arquivo, true);
             PrintWriter pw = new PrintWriter(fw)) {

            if (arquivoNovo) {
                pw.println(CABECALHO);
            }

            for (Ingresso ingresso : venda.getIngressos()) {

                String nomeCliente  = "";
                String idadeCliente = "";
                String horarioSessao = "";

                if (ingresso.getCliente() != null) {
                    nomeCliente  = ingresso.getCliente().getNome();
                    idadeCliente = String.valueOf(ingresso.getCliente().getIdade());
                }

                if (ingresso.getSessao() != null && ingresso.getSessao().getHorario() != null) {
                    horarioSessao = ingresso.getSessao().getHorario().toString();
                }

                pw.println(
                        venda.getDataVenda()                          + ";" +
                        venda.getFormaPagamento()                     + ";" +
                        ingresso.getSessao().getFilme().getTitulo()   + ";" +
                        ingresso.getAssento().getCodigo()             + ";" +
                        ingresso.getCategoria()                       + ";" +
                        ingresso.getPreco()                           + ";" +
                        nomeCliente                                   + ";" +
                        idadeCliente                                  + ";" +
                        horarioSessao
                );
            }

            pw.flush();
            System.out.println("Arquivo existe? " + arquivo.exists());

        } catch (Exception e) {
            System.out.println("ERRO AO SALVAR CSV");
            e.printStackTrace();
        }
    }
}
