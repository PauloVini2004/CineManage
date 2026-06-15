package br.ufrpe.cine_rural.exceptions;

import br.ufrpe.cine_rural.model.tiposala.Sala;

public class NERException extends RuntimeException {
    private Sala sala;

    public NERException(String message, Sala sala) {
        super("Sala inexistente");
        this.sala = null;
    }

    public Sala getSala() {
        return sala;
    }
}