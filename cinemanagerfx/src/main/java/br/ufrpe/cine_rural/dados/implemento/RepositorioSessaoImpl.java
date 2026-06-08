package br.ufrpe.cine_rural.dados.implemento;

import br.ufrpe.cine_rural.dados.interfaces.IRepositorioSessao;
import br.ufrpe.cine_rural.model.Filme;
import br.ufrpe.cine_rural.model.tiposala.Sala;
import br.ufrpe.cine_rural.enums.Idioma;
import br.ufrpe.cine_rural.enums.StatusSessao;
import br.ufrpe.cine_rural.model.Sessao;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class RepositorioSessaoImpl implements IRepositorioSessao {

    private ArrayList<Sessao> sessoes;

    private RepositorioFilmeImpl repositorioFilme;
    private RepositorioSalaImpl repositorioSala;

    public RepositorioSessaoImpl(RepositorioFilmeImpl repositorioFilme,
                                 RepositorioSalaImpl repositorioSala) {
        this.sessoes = new ArrayList<>();
        this.repositorioFilme = repositorioFilme;
        this.repositorioSala = repositorioSala;

        try {
            carregarCSV();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cadastrar(Sessao sessao) {
        sessoes.add(sessao);

        try {
            salvarCSV();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Sessao buscar(LocalDateTime horario) {
        for (Sessao sessao : sessoes) {
            if (sessao.getHorario().equals(horario)) {
                return sessao;
            }
        }
        return null;
    }

    @Override
    public void atualizar(Sessao sessaoAtualizada) {
        for (int i = 0; i < sessoes.size(); i++) {
            if (sessoes.get(i).getHorario().equals(sessaoAtualizada.getHorario())) {

                sessoes.set(i, sessaoAtualizada);

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
    public void remover(LocalDateTime horario) {
        Sessao sessao = buscar(horario);

        if (sessao != null) {
            sessoes.remove(sessao);

            try {
                salvarCSV();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ArrayList<Sessao> listar() {
        return sessoes;
    }

    private void carregarCSV() throws IOException {

        File arquivo = new File(
                "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/csv/sessoes.csv"
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

            Filme filme = repositorioFilme.buscar(dados[0]);
            Sala sala = repositorioSala.buscar(Integer.parseInt(dados[1]));

            if (filme == null || sala == null) {
                continue;
            }

            Sessao sessao = new Sessao(
                    filme,
                    sala,
                    LocalDateTime.parse(dados[2]),
                    Idioma.valueOf(dados[3]),
                    StatusSessao.valueOf(dados[4])
            );

            sessoes.add(sessao);
        }

        reader.close();
    }

    public void salvarCSV() throws IOException {

        System.out.println("Entrou em salvarCSV");

        BufferedWriter writer = new BufferedWriter(
                new FileWriter(
                        "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/csv/sessoes.csv"
                )
        );

        for (Sessao sessao : sessoes) {
            writer.write(
                    sessao.getFilme().getTitulo() + ";" +
                            sessao.getSala().getId() + ";" +
                            sessao.getHorario() + ";" +
                            sessao.getIdioma() + ";" +
                            sessao.getStatus()
            );

            writer.newLine();
        }

        writer.close();
    }
}