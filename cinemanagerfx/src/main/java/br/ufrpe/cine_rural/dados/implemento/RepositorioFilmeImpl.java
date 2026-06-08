package br.ufrpe.cine_rural.dados.implemento;

import br.ufrpe.cine_rural.dados.interfaces.IRepositorioFilme;
import br.ufrpe.cine_rural.enums.ClassificacaoIndicativa;
import br.ufrpe.cine_rural.enums.Genero;
import br.ufrpe.cine_rural.model.Filme;
import javafx.scene.image.Image;

import java.io.*;
import java.util.ArrayList;

public class RepositorioFilmeImpl implements IRepositorioFilme {

    private ArrayList<Filme> filmes;

    public RepositorioFilmeImpl() {
        this.filmes = new ArrayList<>();

        try {
            carregarCSV();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cadastrar(Filme filme) {
        filmes.add(filme);

        try {
            salvarCSV();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            if (filmes.get(i).getTitulo()
                    .equalsIgnoreCase(filmeAtualizado.getTitulo())) {

                filmes.set(i, filmeAtualizado);

                try {
                    salvarCSV();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return;
            }
        }
    }

    @Override
    public void remover(String titulo) {
        Filme filme = buscar(titulo);

        if (filme != null) {
            filmes.remove(filme);

            try {
                salvarCSV();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ArrayList<Filme> listar() {
        return filmes;
    }

    private void carregarCSV() throws IOException {

        File arquivo = new File(
                "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/csv/filmes.csv"
        );

        if (!arquivo.exists()) {
            return;
        }

        BufferedReader reader = new BufferedReader(
                new FileReader(arquivo)
        );

        String linha;

        while ((linha = reader.readLine()) != null) {

            String[] dados = linha.split(";");

            if (dados.length < 5) {
                continue;
            }

            Filme filme = new Filme(
                    dados[0],
                    dados[1],
                    Integer.parseInt(dados[2]),
                    Genero.valueOf(dados[3]),
                    ClassificacaoIndicativa.valueOf(dados[4]),
                    null
            );

            filmes.add(filme);
        }

        reader.close();
    }

    public void salvarCSV() throws IOException {

        System.out.println("Entrou em salvarCSV");

        BufferedWriter writer = new BufferedWriter(
                new FileWriter(
                        "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/csv/filmes.csv"
                )
        );

        for (Filme filme : filmes) {

            writer.write(
                    filme.getTitulo() + ";" +
                            filme.getSinopse() + ";" +
                            filme.getDuracao() + ";" +
                            filme.getGenero() + ";" +
                            filme.getClassificacao()
            );

            writer.newLine();
        }

        writer.close();
    }
}