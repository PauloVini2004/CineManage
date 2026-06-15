package br.ufrpe.cine_rural.exceptions;

import br.ufrpe.cine_rural.model.loja.Produto;

public class ISQException extends RuntimeException {
    private int quantidade;
    private int qtdEstoque;
    private Produto produto;

    public ISQException(String message, int qtdEstoque, int quantidade, Produto produto) {
        super("QUANTIDADE INSUFICIENTE DE ESTOQUE");
        this.qtdEstoque = qtdEstoque;
        this.quantidade = quantidade;
        this.produto = produto;
    }

    public int getQtdEstoque () {
        return qtdEstoque;
    }
    public Produto getProduto () {
        return produto;
    }
    public int getQuantidade () {
        return quantidade;
    }
}