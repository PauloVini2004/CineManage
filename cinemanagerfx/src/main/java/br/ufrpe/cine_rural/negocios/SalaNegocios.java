package br.ufrpe.cine_rural.negocios;

import br.ufrpe.cine_rural.dados.implemento.RepositorioProdutoImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioSalaImpl;
import br.ufrpe.cine_rural.dados.interfaces.IRepositorioSala;
import br.ufrpe.cine_rural.dados.interfaces.IRepositorioSessao;
import br.ufrpe.cine_rural.model.tiposala.Sala;
import br.ufrpe.cine_rural.model.tiposala.Imax;
import br.ufrpe.cine_rural.model.tiposala.Vip;
import br.ufrpe.cine_rural.model.tiposala.Comum;

import java.util.ArrayList;
import java.util.List;


public class SalaNegocios {

    private final IRepositorioSala repositorioSala;

    private final List<Sala> salas = new ArrayList<>();

    public SalaNegocios(IRepositorioSala repositorioSala) {
        this.repositorioSala = repositorioSala;
    }

    // Cadastra uma sala Comum.
    public Comum cadastrarSalaComum(int id, int capacidade) {
        validarIdUnico(id);
        validarCapacidade(capacidade);
        Comum sala = new Comum(id, capacidade);
        repositorioSala.cadastrar(sala);
        System.out.println("[SalaController] Sala Comum #" + id + " cadastrada (cap.: " + capacidade + ").");
        return sala;
    }

    // Cadastra uma sala VIP.
    public Vip cadastrarSalaVip(int id, int capacidade) {
        validarIdUnico(id);
        validarCapacidade(capacidade);
        Vip sala = new Vip(id, capacidade);
        repositorioSala.cadastrar(sala);
        System.out.println("[SalaController] Sala VIP #" + id + " cadastrada (cap.: " + capacidade + ").");
        return sala;
    }

    // Cadastra uma sala IMAX.
    public Imax cadastrarSalaImax(int id, int capacidade) {
        validarIdUnico(id);
        validarCapacidade(capacidade);
        Imax sala = new Imax(id, capacidade);
        repositorioSala.cadastrar(sala);
        System.out.println("[SalaController] Sala IMAX #" + id + " cadastrada (cap.: " + capacidade + ").");
        return sala;
    }

    /*
     * Permite sobrescrever o preço base de uma sala para promoções específicas.
     * Não deve ser chamado enquanto há sessões abertas na sala
     * (responsabilidade de quem manda em tudo, ex.: SessaoController).
     */
    public void atualizarPreco(Sala sala, double novoPreco) {
        if (novoPreco < 0) throw new IllegalArgumentException("Preço não pode ser negativo.");
        sala.setPreco(novoPreco);
        System.out.println("[SalaController] Preço da sala #" + sala.getId() + " → R$ " + novoPreco);
    }

    public void atualizarCapacidade(Sala sala, int novaCapacidade) {
        validarCapacidade(novaCapacidade);
        sala.setCapacidade(novaCapacidade);
        System.out.println("[SalaController] Capacidade da sala #" + sala.getId() + " → " + novaCapacidade);
    }


    public List<Sala> listarSalas() {
        return repositorioSala.listar();
    }

    public Sala buscarPorId(int id) {
        return repositorioSala.buscar(id);
    }

    // Filtra salas por subtipo (Comum.class, Vip.class ou Imax.class).
    public List<Sala> listarPorTipo(Class<? extends Sala> tipo) {
        return salas.stream().filter(tipo::isInstance).toList();
    }

    public void removerSala(Sala sala) {
        salas.remove(sala);
        System.out.println("[SalaController] Sala #" + sala.getId() + " removida.");
    }

    // Verificadores
    private void validarIdUnico(int id) {
        if (buscarPorId(id) != null)
            throw new IllegalArgumentException("Já existe uma sala com ID " + id + ".");
    }

    private void validarCapacidade(int capacidade) {
        if (capacidade <= 0)
            throw new IllegalArgumentException("Capacidade deve ser maior que zero.");
    }
}