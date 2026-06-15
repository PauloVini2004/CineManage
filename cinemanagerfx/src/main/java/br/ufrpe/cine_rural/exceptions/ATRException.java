package br.ufrpe.cine_rural.exceptions;

import br.ufrpe.cine_rural.model.Filme;
import br.ufrpe.cine_rural.model.Sessao;
import br.ufrpe.cine_rural.model.tiposala.Sala;

import java.time.LocalDateTime;

public class ATRException extends RuntimeException {
    private Sala sala;
    private Filme filme;
    private Sessao sessao;
    private LocalDateTime horario;

    public ATRException(String message,  Sala sala, Filme filme,  LocalDateTime horario,  Sessao sessao) {
        super("Sala já possui uma sessão nesse horario");
        this.sala = sala;
        this.filme = filme;
        this.horario = horario;
        this.sessao = sessao;
    }

    public Filme getFilme() {
        return filme;
    }
    public Sessao getSessao() {
        return sessao;
    }
    public LocalDateTime getHorario() {
        return horario;
    }
    public Sala getSala() {
        return sala;
    }
}