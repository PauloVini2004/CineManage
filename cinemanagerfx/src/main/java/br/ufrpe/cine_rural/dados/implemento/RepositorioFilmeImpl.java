package br.ufrpe.cine_rural.dados.implemento;

import br.ufrpe.cine_rural.dados.interfaces.IRepositorioFilme;
import br.ufrpe.cine_rural.enums.ClassificacaoIndicativa;
import br.ufrpe.cine_rural.enums.Genero;
import br.ufrpe.cine_rural.model.Filme;
import javafx.scene.image.Image;
import java.io.*;
import java.io.InputStream;
import java.util.ArrayList;

public class RepositorioFilmeImpl implements IRepositorioFilme {

    private static final String PASTA_POSTERS = "posters";

    private static RepositorioFilmeImpl instancia;
    private ArrayList<Filme> filmes;

    private RepositorioFilmeImpl() {
        this.filmes = new ArrayList<>();
        garantirPastaPosters();
        try {
            carregarCSV();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void garantirPastaPosters() {
        File pasta = new File(PASTA_POSTERS);
        if (!pasta.exists()) pasta.mkdirs();

        String[] recursosPoster = {
                "/br/ufrpe/cine_rural/gui/AEsperaDeUmMilagre.png",
                "/br/ufrpe/cine_rural/gui/OExorcista.png",
                "/br/ufrpe/cine_rural/gui/Odisseia.jpg",
                "/br/ufrpe/cine_rural/gui/Praticalmagic.jpg",
                "/br/ufrpe/cine_rural/gui/Project_Hail_Mary_poster.jpg",
                "/br/ufrpe/cine_rural/gui/Pulp_Fiction_cover.jpg",
                "/br/ufrpe/cine_rural/gui/TodoMundoEmPanico.png",
                "/br/ufrpe/cine_rural/gui/Zootopia_2.jpg",
                "/br/ufrpe/cine_rural/gui/Odisseia.jpg"
        };

        for (String recurso : recursosPoster) {
            String nome = recurso.substring(recurso.lastIndexOf('/') + 1);
            File destino = new File(pasta, nome);
            if (destino.exists()) continue;
            try (InputStream is = RepositorioFilmeImpl.class.getResourceAsStream(recurso)) {
                if (is != null) {
                    java.nio.file.Files.copy(is, destino.toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException ignored) {}
        }
    }

    public static Image carregarImagem(String nomeOuCaminho) {
        if (nomeOuCaminho == null || nomeOuCaminho.isBlank()) return null;

        String nome = extrairNomeArquivo(nomeOuCaminho);

        File filePoster = new File(PASTA_POSTERS, nome);
        if (filePoster.exists()) {
            try {
                return new Image(filePoster.toURI().toString());
            } catch (Exception ignored) {}
        }

        String[] prefixos = {
                "/br/ufrpe/cine_rural/gui/",
                "/br/ufrpe/cine_rural/gui/ImagensProduto/"
        };
        for (String prefixo : prefixos) {
            try (InputStream is = RepositorioFilmeImpl.class.getResourceAsStream(prefixo + nome)) {
                if (is != null) return new Image(is);
            } catch (Exception ignored) {}
        }

        return null;
    }


    private static String extrairNomeArquivo(String caminho) {
        String s = caminho.replaceFirst("^file:[/\\\\]*", "");
        int posSlash = Math.max(s.lastIndexOf('/'), s.lastIndexOf('\\'));
        return posSlash >= 0 ? s.substring(posSlash + 1) : s;
    }



    @Override
    public void cadastrar(Filme filme) {
        filmes.add(filme);
        try { salvarCSV(); } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public Filme buscar(String titulo) {
        for (Filme filme : filmes)
            if (filme.getTitulo().equalsIgnoreCase(titulo)) return filme;
        return null;
    }

    @Override
    public void atualizar(Filme filmeAtualizado) {
        for (int i = 0; i < filmes.size(); i++) {
            if (filmes.get(i).getTitulo().equalsIgnoreCase(filmeAtualizado.getTitulo())) {
                filmes.set(i, filmeAtualizado);
                try { salvarCSV(); } catch (IOException e) { e.printStackTrace(); }
                return;
            }
        }
    }

    @Override
    public void remover(String titulo) {
        Filme filme = buscar(titulo);
        if (filme != null) {
            filmes.remove(filme);
            try { salvarCSV(); } catch (IOException e) { e.printStackTrace(); }
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

                String posterBruto = (dados.length >= 6 && !dados[5].trim().isEmpty())
                        ? dados[5].trim()
                        : null;

                String posterNome = (posterBruto != null) ? extrairNomeArquivo(posterBruto) : null;

                Filme filme = new Filme(
                        dados[0].trim(),
                        dados[1].trim(),
                        Integer.parseInt(dados[2].trim()),
                        Genero.valueOf(dados[3].trim()),
                        ClassificacaoIndicativa.valueOf(dados[4].trim()),
                        posterNome
                );
                filmes.add(filme);
            }
        }
    }

    public void salvarCSV() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/dados/arquivoscsv/filmes.csv"
        ))) {
            for (Filme filme : filmes) {
                String poster = filme.getCaminhoPoster() != null
                        ? extrairNomeArquivo(filme.getCaminhoPoster())
                        : "";
                writer.write(
                        filme.getTitulo()        + ";" +
                                filme.getSinopse()       + ";" +
                                filme.getDuracao()       + ";" +
                                filme.getGenero()        + ";" +
                                filme.getClassificacao() + ";" +
                                poster
                );
                writer.newLine();
            }
        }
    }

    public static RepositorioFilmeImpl getInstancia() {
        if (instancia == null) instancia = new RepositorioFilmeImpl();
        return instancia;
    }
}
