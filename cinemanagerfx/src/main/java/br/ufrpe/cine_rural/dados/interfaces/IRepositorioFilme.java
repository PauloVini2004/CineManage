package br.ufrpe.cine_rural.dados.interfaces;

import br.ufrpe.cine_rural.model.Filme;

import java.util.ArrayList;

public interface IRepositorioFilme {
    void cadastrar(Filme filme);
    Filme buscar(String titulo);
    void atualizar(Filme filme);
    void remover(String titulo);
    ArrayList<Filme> listar();
}
