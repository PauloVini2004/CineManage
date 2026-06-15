package br.ufrpe.cine_rural.exceptions;

import br.ufrpe.cine_rural.model.Assento;

public class ATSException extends RuntimeException {
    private Assento assento;

    public ATSException(String message,  Assento assento) {
        super("Assento já ocupado");
        this.assento = assento;
    }

    public Assento getAssento() {
        return assento;
    }
}