package br.ufrpe.cine_rural.dados.interfaces;

import br.ufrpe.cine_rural.model.loja.Produto;
import java.util.ArrayList;

public interface IRepositorioProduto {

    void cadastrar(Produto produto);

    Produto buscar(int id);

    void atualizar(Produto produto);

    void remover(int id);

    ArrayList<Produto> listar();
}