package br.ufrpe.cine_rural.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VendaIngresso {

    private List<Ingresso> ingressos;
    private String formaPagamento;
    private LocalDateTime dataVenda;

    public VendaIngresso() {
        this.ingressos = new ArrayList<>();
        this.dataVenda = LocalDateTime.now();
    }

    public VendaIngresso(List<Ingresso> ingressos) {
        this.ingressos = new ArrayList<>(ingressos);
        this.dataVenda = LocalDateTime.now();
    }

    public List<Ingresso> getIngressos() {
        return ingressos;
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
}