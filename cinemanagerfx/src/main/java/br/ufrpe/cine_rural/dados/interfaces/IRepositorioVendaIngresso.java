package br.ufrpe.cine_rural.dados.interfaces;

import br.ufrpe.cine_rural.model.VendaIngresso;

import java.util.ArrayList;

public interface IRepositorioVendaIngresso {

    void cadastrar(VendaIngresso venda);

    ArrayList<VendaIngresso> listar();
}
