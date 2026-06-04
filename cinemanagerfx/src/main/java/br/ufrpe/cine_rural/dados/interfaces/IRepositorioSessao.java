package br.ufrpe.cine_rural.dados.interfaces;

import br.ufrpe.cine_rural.model.Sessao;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface IRepositorioSessao {

    void cadastrar(Sessao sessao);

    Sessao buscar(LocalDateTime horario);

    void atualizar(Sessao sessao);

    void remover(LocalDateTime horario);

    ArrayList<Sessao> listar();
}