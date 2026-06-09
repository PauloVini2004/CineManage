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

    // Metodo CarregarCsv
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

                String titulo = dados[0].trim();
                Image  poster = carregarPoster(titulo);

                Filme filme = new Filme(
                        titulo,
                        dados[1].trim(),
                        Integer.parseInt(dados[2].trim()),
                        Genero.valueOf(dados[3].trim()),
                        ClassificacaoIndicativa.valueOf(dados[4].trim()),
                        poster
                );
                filmes.add(filme);
            }
        }
    }

    // Tenta carregar os posters, localiza na pasta e se não encontrar retorna NULL
    private Image carregarPoster(String titulo) {
        // Converte espaços em underscores e tenta .jpg
        String nomeArquivo = titulo.replace(" ", "_") + ".jpg";
        String caminho = "/br/ufrpe/cine_rural/gui/" + nomeArquivo;

        try (InputStream is = getClass().getResourceAsStream(caminho)) {
            if (is != null) {
                return new Image(is);
            }
        } catch (Exception ignored) {}

        // Tenta .png como fallback
        String caminhoAlternativo = "/br/ufrpe/cine_rural/gui/"
                + titulo.replace(" ", "_") + ".png";
        try (InputStream is2 = getClass().getResourceAsStream(caminhoAlternativo)) {
            if (is2 != null) {
                return new Image(is2);
            }
        } catch (Exception ignored) {}

        return null;
    }

    public void salvarCSV() throws IOException {
        System.out.println("Entrou em salvarCSV (filmes)");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/dados/arquivoscsv/filmes.csv"
        ))) {
            for (Filme filme : filmes) {
                writer.write(
                        filme.getTitulo()       + ";" +
                                filme.getSinopse()      + ";" +
                                filme.getDuracao()      + ";" +
                                filme.getGenero()       + ";" +
                                filme.getClassificacao()
                );
                writer.newLine();
            }
        }
    }
}
