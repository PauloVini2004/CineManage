package br.ufrpe.cine_rural.exceptions;

import br.ufrpe.cine_rural.model.Filme;
import br.ufrpe.cine_rural.model.Sessao;
import br.ufrpe.cine_rural.model.tiposala.Sala;

import java.time.LocalDateTime;

public class NESException extends RuntimeException {
    private Sala sala;
    private Sessao sessao;
    private Filme filme;
    private LocalDateTime horario;

    public NESException(String message, Sala sala, Sessao sessao, Filme filme, LocalDateTime horario) {
        super("Na sala não há uma sessão deste filme nesse horário");
        this.sessao = sessao;
        this.sala = sala;
        this.filme = filme;
        this.horario = horario;
    }

    public Sala getSala() {
        return sala;
    }
    public Sessao getSessao() {
        return sessao;
    }
    public Filme getFilme() {
        return filme;
    }
    public LocalDateTime getHorario() {
        return horario;
    }
}