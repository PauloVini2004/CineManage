package br.ufrpe.cine_rural.util;

import br.ufrpe.cine_rural.model.loja.Produto;

import java.io.*;
import java.util.ArrayList;

public class ProdutoArquivo {

    private static final String ARQUIVO = "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/dados/arquivoscsv/produtos.csv";

    public static void salvar(ArrayList<Produto> produtos) {

        File arquivo = new File(ARQUIVO);
        File pasta = arquivo.getParentFile();

        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs();
        }

        try (BufferedWriter bw =
                     new BufferedWriter(
                             new FileWriter(ARQUIVO))) {

            for (Produto p : produtos) {

                bw.write(
                        p.getId() + ";" +
                                p.getNome() + ";" +
                                p.getPreco() + ";" +
                                p.getQtdEstoque() + ";" +
                                p.getCaminhoImagem()
                );

                bw.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



        System.out.println(
                "Arquivo CSV: "
                        + arquivo.getAbsolutePath()
        );
    }

    public static ArrayList<Produto> carregar() {

        ArrayList<Produto> produtos =
                new ArrayList<>();

        File arquivo = new File(ARQUIVO);

        System.out.println(
                "Carregando CSV: "
                        + arquivo.getAbsolutePath()
        );

        System.out.println(
                "Existe? "
                        + arquivo.exists()
        );

        System.out.println(
                "Tamanho: "
                        + arquivo.length()
        );

        if (!arquivo.exists()) {
            return produtos;
        }

        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(arquivo))) {

            String linha;

            while ((linha = br.readLine()) != null) {

                String[] dados =
                        linha.split(";", -1);

                Produto produto =
                        new Produto(
                                Integer.parseInt(dados[0]),
                                dados[1],
                                Double.parseDouble(dados[2]),
                                Integer.parseInt(dados[3]),
                                dados[4]
                        );

                produtos.add(produto);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return produtos;
    }
}
