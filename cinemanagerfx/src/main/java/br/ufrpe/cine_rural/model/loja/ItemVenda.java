package br.ufrpe.cine_rural.model.loja;

public class ItemVenda
{
    private Produto produto;
    private int quantidade;
    private double subtotal;

    public ItemVenda(int quantidade, Produto produto)
    {
        this.produto = produto;
        this.quantidade = quantidade;
        this.subtotal = calcularSubtotal();
    }

    public final double calcularSubtotal() 
    {
       return this.produto.getPreco() * this.quantidade;
    }

    public int getQuantidade() { 
        return quantidade; 
    }
    public double getSubtotal() { 
        return subtotal; 
    }
    public Produto getProduto() { 
        return produto; 
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
        this.subtotal = calcularSubtotal();
    }
    public void setProduto(Produto produto) {
        this.produto = produto;
        this.subtotal = calcularSubtotal();
    }
}
