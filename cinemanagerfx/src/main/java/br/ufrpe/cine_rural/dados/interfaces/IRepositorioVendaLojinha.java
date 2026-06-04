package br.ufrpe.cine_rural.dados.interfaces;

import br.ufrpe.cine_rural.model.loja.VendaLojinha;
import java.util.ArrayList;

public interface IRepositorioVendaLojinha {

    void cadastrar(VendaLojinha venda);

    VendaLojinha buscar(int indice);

    void remover(int indice);

    ArrayList<VendaLojinha> listar();
}