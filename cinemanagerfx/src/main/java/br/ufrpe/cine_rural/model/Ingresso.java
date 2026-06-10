package br.ufrpe.cine_rural.model;

import br.ufrpe.cine_rural.enums.CategoriaMeiaEntrada;
import br.ufrpe.cine_rural.enums.TipoAssento;

public class Ingresso {

    private Sessao sessao;
    private Assento assento;
    private double preco;
    private CategoriaMeiaEntrada categoria;
    private Cliente cliente;

    public Ingresso(Sessao sessao,
                    Assento assento,
                    double valor,
                    CategoriaMeiaEntrada categoria) {

        if (assento.getStatus() == TipoAssento.OCUPADO) {

            throw new IllegalArgumentException(
                    "Assento ja ocupado."
            );
        }

        this.sessao = sessao;
        this.assento = assento;
        this.preco = calcularValor(valor, categoria);
        this.categoria = categoria;

        assento.ocupar();
    }

    private double calcularValor(double valor,
                                 CategoriaMeiaEntrada categoria) {

        if (categoria == CategoriaMeiaEntrada.MEIA_ENTRADA) {
            return valor / 2;
        }

        return valor;
    }

    public Sessao getSessao() {
        return sessao;
    }

    public Assento getAssento() {
        return assento;
    }

    public double getPreco() {
        return preco;
    }

    public CategoriaMeiaEntrada getCategoria() {
        return categoria;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}