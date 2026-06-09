package br.ufrpe.cine_rural.dados.implemento;

import br.ufrpe.cine_rural.dados.interfaces.IRepositorioFilme;
import br.ufrpe.cine_rural.enums.ClassificacaoIndicativa;
import br.ufrpe.cine_rural.enums.Genero;
import br.ufrpe.cine_rural.model.Filme;

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
            if (filmes.get(i).getTitulo().equalsIgnoreCase(filmeAtualizado.getTitulo())) {
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
                "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/dados/arquivoscsv/filmes.csv"
        );

        if (!arquivo.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length < 5) continue;

                String posterPath = null;
                if (dados.length >= 6 && !dados[5].trim().isEmpty()) {
                    posterPath = dados[5].trim();
                } else {
                    posterPath = resolverCaminhoPoster(dados[0].trim());
                }

                Filme filme = new Filme(
                        dados[0].trim(),
                        dados[1].trim(),
                        Integer.parseInt(dados[2].trim()),
                        Genero.valueOf(dados[3].trim()),
                        ClassificacaoIndicativa.valueOf(dados[4].trim()),
                        posterPath
                );
                filmes.add(filme);
            }
        }
    }

    private String resolverCaminhoPoster(String titulo) {
        String base = "/br/ufrpe/cine_rural/gui/" + titulo.replace(" ", "_");

        if (getClass().getResourceAsStream(base + ".jpg") != null) {
            return base + ".jpg";
        }
        if (getClass().getResourceAsStream(base + ".png") != null) {
            return base + ".png";
        }

        return null; // poster não encontrado
    }

    public void salvarCSV() throws IOException {
        System.out.println("Entrou em salvarCSV (filmes)");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/dados/arquivoscsv/filmes.csv"
        ))) {
            for (Filme filme : filmes) {
                writer.write(
                        filme.getTitulo()        + ";" +
                                filme.getSinopse()       + ";" +
                                filme.getDuracao()       + ";" +
                                filme.getGenero()        + ";" +
                                filme.getClassificacao() + ";" +
                                (filme.getCaminhoPoster() != null ? filme.getCaminhoPoster() : "")
                );
                writer.newLine();
            }
        }
    }
}