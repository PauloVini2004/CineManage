package br.ufrpe.cine_rural.model;

import br.ufrpe.cine_rural.enums.Idioma;
import br.ufrpe.cine_rural.enums.StatusSessao;
import br.ufrpe.cine_rural.model.tiposala.Sala;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Sessao {

    private Filme filme;
    private Sala sala;
    private LocalDateTime horario;
    private Idioma idioma;
    private StatusSessao status;

    private ArrayList<Ingresso> ingressos;

    public Sessao(Filme filme,
                  Sala sala,
                  LocalDateTime horario,
                  Idioma idioma,
                  StatusSessao status) {

        this.filme = filme;
        this.sala = sala;
        this.horario = horario;
        this.idioma = idioma;
        this.status = status;
        this.ingressos = new ArrayList<>();
    }

    // Métodos de negócio para Ingressos
    public void adicionarIngressos(Ingresso ingresso) {
        String codigoNovo = ingresso.getAssento().getCodigo();
        boolean assentoJaReservado = ingressos.stream()
                .anyMatch(i -> i.getAssento().getCodigo().equals(codigoNovo));
        if (assentoJaReservado) {
            throw new IllegalStateException(
                    "Assento '" + codigoNovo + "' já reservado nesta sessão. "
            );
        }
        ingressos.add(ingresso);
    }

    public ArrayList<Ingresso> getIngressos() {
        return ingressos;
    }

    public int getTotalIngressos() {
        return ingressos.size();
    }

    public void setIngressos(ArrayList<Ingresso> ingressos) {
        this.ingressos = ingressos;
    }


    public Filme getFilme() {
        return filme;
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }

    public StatusSessao getStatus() {
        return status;
    }

    public void setStatus(StatusSessao status) {
        this.status = status;
    }
}