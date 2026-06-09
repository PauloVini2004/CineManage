package br.ufrpe.cine_rural.negocios;

import br.ufrpe.cine_rural.dados.interfaces.IRepositorioSessao;

import br.ufrpe.cine_rural.enums.Idioma;
import br.ufrpe.cine_rural.enums.StatusSessao;
import br.ufrpe.cine_rural.model.Filme;
import br.ufrpe.cine_rural.model.Ingresso;
import br.ufrpe.cine_rural.model.Sessao;
import br.ufrpe.cine_rural.model.tiposala.Sala;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class SessaoNegocios {
    private final IRepositorioSessao repositorioSessao;

    public SessaoNegocios(IRepositorioSessao repositorioSessao) {
        this.repositorioSessao = repositorioSessao;
    }


    public void cadastrarSessao(Filme filme, Sala sala, LocalDateTime horario, Idioma idioma) {
        if (filme == null || sala == null || horario == null || idioma == null) {
            throw new IllegalArgumentException("Todos os campos da sessão são obrigatórios.");
        }
        if (horario.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Horário da sessão não pode ser no passado.");
        }

        // verifica sobreposição de horário na mesma sala
        for (Sessao s : repositorioSessao.listar()) {
            if (s.getSala().getId() == sala.getId()) {
                long diffMinutos = Math.abs(java.time.Duration.between(s.getHorario(), horario).toMinutes());
                if (diffMinutos < filme.getDuracao()) {
                    throw new IllegalStateException(
                            "Conflito de horário: já existe sessão na sala " + sala.getId()
                                    + " às " + s.getHorario()
                    );
                }
            }
        }

        Sessao sessao = new Sessao(filme,
                sala,
                horario,
                idioma,
                StatusSessao.ABERTA);
        repositorioSessao.cadastrar(sessao);
    }


    //- Atualiza o status de uma sessão (ABERTA, EM_EXIBICAO, ENCERRADA).

    public void atualizarStatus(LocalDateTime horario, StatusSessao novoStatus) {
        Sessao sessao = buscarSessao(horario);
        sessao.setStatus(novoStatus);
        repositorioSessao.atualizar(sessao);
    }


    // Impede alteração de filme em sessões com ingressos já vendidos

    public void atualizarFilme(LocalDateTime horario, Filme novoFilme) {
        Sessao sessao = buscarSessao(horario);
        if (!sessao.getIngressos().isEmpty()) {
            throw new IllegalStateException(
                    "Não é possível alterar o filme de uma sessão com ingressos já vendidos."
            );
        }
        sessao.setFilme(novoFilme);
        repositorioSessao.atualizar(sessao);
    }


    // Adiciona um ingresso a uma sessão.
    void adicionarIngresso(LocalDateTime horario, Ingresso ingresso) {
        Sessao sessao = buscarSessao(horario);
        sessao.adicionarIngressos(ingresso);
        repositorioSessao.atualizar(sessao);
    }


    //Busca uma sessão pelo horário.

    public Sessao buscarSessao(LocalDateTime horario) {
        Sessao sessao = repositorioSessao.buscar(horario);
        if (sessao == null) {
            throw new IllegalArgumentException("Sessão não encontrada para o horário: " + horario);
        }
        return sessao;
    }

    /**
     * Lista todas as sessões cadastradas.
     */
    public ArrayList<Sessao> listarSessoes() {
        return repositorioSessao.listar();
    }

    /**
     * Remove uma sessão pelo horário.
     */
    public void removerSessao(LocalDateTime horario) {
        buscarSessao(horario); // garante existência
        repositorioSessao.remover(horario);
    }

    /**
     * Verifica se a sessão já iniciou (útil para REQ23).
     */
    public boolean sessaoJaIniciou(LocalDateTime horario) {
        Sessao sessao = buscarSessao(horario);
        return sessao.getStatus() == StatusSessao.EM_EXIBICAO
                || sessao.getStatus() == StatusSessao.ENCERRADA
                || LocalDateTime.now().isAfter(sessao.getHorario());
    }
}