package br.ufrpe.cine_rural.exceptions;

import br.ufrpe.cine_rural.model.Filme;

public class NEFException extends RuntimeException {
    private Filme filme;
    public NEFException(String message,  Filme filme) {
        super("Filme Inexistente Em Catalogo");
        this.filme = null;
    }

    public Filme getFilme() {
        return filme;
    }
}