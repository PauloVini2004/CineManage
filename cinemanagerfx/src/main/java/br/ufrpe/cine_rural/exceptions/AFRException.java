package br.ufrpe.cine_rural.exceptions;

import br.ufrpe.cine_rural.model.tiposala.Sala;

public class AFRException extends RuntimeException {
    private Sala sala;

    public AFRException(String message, Sala sala) {
        super("Esta sala ja esta cheia");
        this.sala = sala;
    }

    public Sala getSala() {
        return sala;
    }
}