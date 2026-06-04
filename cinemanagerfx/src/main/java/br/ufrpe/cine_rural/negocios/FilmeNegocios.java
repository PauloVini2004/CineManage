package br.ufrpe.cine_rural.negocios;


import br.ufrpe.cine_rural.enums.ClassificacaoIndicativa;
import br.ufrpe.cine_rural.enums.Genero;
import br.ufrpe.cine_rural.model.Filme;
import br.ufrpe.cine_rural.model.Sessao;
import br.ufrpe.cine_rural.dados.interfaces.IRepositorioFilme;
import javafx.scene.image.Image;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalTime;

public class FilmeNegocios {
    private final IRepositorioFilme repositorioFilme;
    private final SessaoNegocios sessaoNegocios;

    // Limiar para considerar um filme com "baixa procura"
    private static final int LIMIAR_BAIXA_PROCURA = 10;

    public FilmeNegocios(IRepositorioFilme repositorioFilme, SessaoNegocios sessaoNegocios) {
        this.repositorioFilme = repositorioFilme;
        this.sessaoNegocios = sessaoNegocios;
    }


    public void cadastrarFilme(String titulo,
                               String sinopse,
                               int duracao,
                               Genero genero,
                               ClassificacaoIndicativa classificacao,
                               LocalTime localTime,
                               Image poster) {

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título do filme não pode ser vazio.");
        }

        if (duracao <= 0) {
            throw new IllegalArgumentException("Duração deve ser positiva.");
        }

        Filme filme = new Filme(
                titulo,
                sinopse,
                duracao,
                genero,
                classificacao,
                localTime,
                poster
        );

        repositorioFilme.cadastrar(filme);
    }

    public void atualizarClassificacao(String titulo, ClassificacaoIndicativa novaClassificacao) {
        Filme filme = repositorioFilme.buscar(titulo);
        if (filme == null) {
            throw new IllegalArgumentException("Filme não encontrado: " + titulo);
        }
        filme.setClassificacao(novaClassificacao);
        repositorioFilme.atualizar(filme);
    }


    //  Busca um filme pelo título.

    public Filme buscarFilme(String titulo) {
        Filme filme = repositorioFilme.buscar(titulo);
        if (filme == null) {
            throw new IllegalArgumentException("Filme não encontrado: " + titulo);
        }
        return filme;
    }


    //Lista todos os filmes cadastrados.

    public ArrayList<Filme> listarFilmes() {
        return repositorioFilme.listar();
    }


    public void removerFilme(String titulo) {
        Filme filme = repositorioFilme.buscar(titulo);
        if (filme == null) {
            throw new IllegalArgumentException("Filme não encontrado: " + titulo);
        }
        repositorioFilme.remover(titulo);
    }


    public Map<String, String> gerarRelatorioBilheteria() {
        Map<String, String> relatorio = new HashMap<>();

        ArrayList<Sessao> todasSessoes = sessaoNegocios.listarSessoes();


        Map<String, int[]> dados = new HashMap<>(); // [totalIngressos, capacidadeTotal]

        for (Sessao sessao : todasSessoes) {
            String tituloFilme = sessao.getFilme().getTitulo();
            int ingressos = sessao.getTotalIngressos();
            int capacidade = sessao.getSala().getCapacidade();

            dados.merge(tituloFilme,
                    new int[]{ingressos, capacidade},
                    (existing, novo) -> new int[]{existing[0] + novo[0], existing[1] + novo[1]});
        }

        for (Map.Entry<String, int[]> entry : dados.entrySet()) {
            int totalIngressos = entry.getValue()[0];
            int capacidadeTotal = entry.getValue()[1];
            double taxa = capacidadeTotal > 0 ? (totalIngressos * 100.0 / capacidadeTotal) : 0;

            String info = String.format(
                    "Ingressos vendidos: %d | Capacidade total: %d | Taxa de ocupação: %.1f%%",
                    totalIngressos, capacidadeTotal, taxa
            );
            relatorio.put(entry.getKey(), info);
        }

        return relatorio;
    }

    public List<String> listarFilmesComBaixaProcura() {
        List<String> filmesAlerta = new ArrayList<>();
        Map<String, Integer> ingressosPorFilme = new HashMap<>();

        for (Sessao sessao : sessaoNegocios.listarSessoes()) {
            String titulo = sessao.getFilme().getTitulo();
            ingressosPorFilme.merge(titulo, sessao.getTotalIngressos(), Integer::sum);
        }

        for (Map.Entry<String, Integer> entry : ingressosPorFilme.entrySet()) {
            if (entry.getValue() < LIMIAR_BAIXA_PROCURA) {
                filmesAlerta.add(entry.getKey());
            }
        }

        return filmesAlerta;
    }
}
