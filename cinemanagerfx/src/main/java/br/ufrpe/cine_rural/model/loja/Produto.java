package br.ufrpe.cine_rural.model.loja;

public class Produto {
    private int id;
    private double preco;
    private String nome;
    private int qtdEstoque;
    private String caminhoImagem;

    public Produto(int id, String nome, double preco, int qtdEstoque, String caminhoImagem)

    {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.qtdEstoque = qtdEstoque;
        this.caminhoImagem = caminhoImagem;
    }

    public void reduzirEstoque(int qtd) {
        this.qtdEstoque -= qtd;
    }

    public void aumentarEstoque(int qtd) {
        this.qtdEstoque += qtd;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getQtdEstoque() {
        return qtdEstoque;
    }

    public String getCaminhoImagem() {
        return caminhoImagem;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public void setQtdEstoque(int qtdEstoque) {
        this.qtdEstoque = qtdEstoque;
    }

    public void setCaminhoImagem(String caminhoImagem) {this.caminhoImagem = caminhoImagem;}
}
