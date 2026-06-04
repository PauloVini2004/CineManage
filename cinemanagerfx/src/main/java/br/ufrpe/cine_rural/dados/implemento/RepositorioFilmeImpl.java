package br.ufrpe.cine_rural.dados.implemento;

import br.ufrpe.cine_rural.dados.interfaces.IRepositorioFilme;
import br.ufrpe.cine_rural.model.Filme;

import java.util.ArrayList;

public class RepositorioFilmeImpl implements IRepositorioFilme {
    private ArrayList<Filme> filmes;

    public RepositorioFilmeImpl() {
        this.filmes = new ArrayList<>();
    }

    @Override
    public void cadastrar(Filme filme) {
        filmes.add(filme);
    }

    @Override
    public Filme buscar(String titulo) {
        for (Filme filme : filmes) {
            if (filme.getTitulo().equalsIgnoreCase(titulo)) {
                return filme;
            }
        }
        return null;
    }

    @Override
    public void atualizar(Filme filmeAtualizado) {
        for (int i = 0; i < filmes.size(); i++) {
            if (filmes.get(i).getTitulo().equalsIgnoreCase(filmeAtualizado.getTitulo())) {
                filmes.set(i, filmeAtualizado);
                return;
            }
        }
    }

    @Override
    public void remover(String titulo) {
        Filme filme = buscar(titulo);
        if (filme != null) {
            filmes.remove(filme);
        }
    }

    @Override
    public ArrayList<Filme> listar() {
        return filmes;
    }
}