package br.ufrpe.cine_rural.model.loja;

import br.ufrpe.cine_rural.model.Cliente;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VendaLojinha {

    private final List<ItemVenda> itens;

    private Cliente cliente;
    private String formaPagamento;
    private LocalDateTime dataVenda;

    public VendaLojinha() {
        this.itens = new ArrayList<>();
    }

    public VendaLojinha(List<ItemVenda> itens) {
        this.itens = new ArrayList<>(itens);
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public void adicionarItem(Produto produto, int quantidade) {

        verificarEstoque(produto);

        ItemVenda novoItem =
                new ItemVenda(
                        quantidade,
                        produto
                );

        itens.add(novoItem);

        produto.reduzirEstoque(quantidade);
    }

    public void removerItem(Produto produto, int quantidade) {

        for (ItemVenda i : itens) {

            if (i.getProduto() == produto) {

                if (quantidade >= i.getQuantidade()) {

                    itens.remove(i);

                    produto.aumentarEstoque(
                            i.getQuantidade()
                    );

                } else {

                    i.setQuantidade(
                            i.getQuantidade() - quantidade
                    );

                    produto.aumentarEstoque(
                            quantidade
                    );
                }

                return;
            }
        }

        System.out.println(
                "Produto não encontrado na venda."
        );
    }

    public void verificarEstoque(Produto produto) {

        if (produto.getQtdEstoque() <= 0) {

            throw new RuntimeException(
                    "Estoque insuficiente para: "
                            + produto.getNome()
            );
        }
    }

    public double calcularTotal() {

        double total = 0.0;

        for (ItemVenda i : itens) {
            total += i.getSubtotal();
        }

        return total;
    }

    public void finalizarVenda() {

        for (ItemVenda i : itens) {

            System.out.println(
                    "- "
                            + i.getProduto().getNome()
                            + " x"
                            + i.getQuantidade()
                            + " = R$ "
                            + i.getSubtotal()
            );
        }

        System.out.println(
                "TOTAL: R$ "
                        + calcularTotal()
        );
    }
}