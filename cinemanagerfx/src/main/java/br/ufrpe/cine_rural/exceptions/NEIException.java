package br.ufrpe.cine_rural.exceptions;

import br.ufrpe.cine_rural.model.loja.Produto;

public class NEIException extends RuntimeException {
    private Produto produto;

    public NEIException(String message, Produto produto) {
        super("Item Inexistente no Estoque");
        this.produto = null;
    }

    public Produto getProduto() {
        return produto;
    }
}