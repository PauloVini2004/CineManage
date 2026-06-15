package br.ufrpe.cine_rural.exceptions;

import br.ufrpe.cine_rural.model.Filme;

public class AEFException extends RuntimeException {
    private Filme filme;

    public AEFException(String message,  Filme filme) {
        super(message);
        this.filme = filme;
    }

    public Filme getFilme() {
        return filme;
    }
}