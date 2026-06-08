package br.ufrpe.cine_rural.model;

import br.ufrpe.cine_rural.enums.TipoAssento;

public class Assento {

    private String codigo;
    private TipoAssento status;
    private int frequenciaOcupacao;

    public Assento(String codigo) {
        this.codigo = codigo;
        this.status = TipoAssento.LIVRE;
        this.frequenciaOcupacao = 0;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoAssento getStatus() {
        return status;
    }

    public void ocupar() {
        status = TipoAssento.OCUPADO;
        frequenciaOcupacao++;
    }

    public void liberar() {
        status = TipoAssento.LIVRE;
    }

    public int getFrequenciaOcupacao() {
        return frequenciaOcupacao;
    }

    @Override
    public String toString() {
        return codigo;
    }
}