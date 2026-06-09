package br.ufrpe.cine_rural.negocios;

import br.ufrpe.cine_rural.model.tiposala.Sala;
import br.ufrpe.cine_rural.model.tiposala.Comum;
import br.ufrpe.cine_rural.model.tiposala.Vip;
import br.ufrpe.cine_rural.model.tiposala.Imax;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller responsável pelo cadastro e gestão de salas.
 *
 * REQ03 – Cadastrar salas com identificação e capacidade total de assentos.
 * REQ04 – Herança: Comum (preco=1.0), VIP (preco=3.0), IMAX (preco=2.0).
 *
 * Os construtores reais do seu código:
 *   Comum(int id, int capacidade)  → super(id, capacidade, 1.0)
 *   Imax (int id, int capacidade)  → super(id, capacidade, 2.0)
 *   Vip  (int id, int capacidade)  → super(id, capacidade, 3.0)
 *
 * O preço base é fixado pelo tipo de sala, mas pode ser sobrescrito via
 * atualizarPreco() para promoções específicas.
 */
public class SalaNegocios {

    private final List<Sala> salas = new ArrayList<>();

    // -------------------------------------------------------------------------
    // REQ03 / REQ04 – Cadastro por tipo
    // -------------------------------------------------------------------------

    /** Cadastra uma sala Comum (multiplicador de preço 1.0×). */
    public Comum cadastrarSalaComum(int id, int capacidade) {
        validarIdUnico(id);
        validarCapacidade(capacidade);
        Comum sala = new Comum(id, capacidade);   // construtor real do seu projeto
        salas.add(sala);
        System.out.println("[SalaNegocios] Sala Comum #" + id + " cadastrada (cap.: " + capacidade + ").");
        return sala;
    }

    /** Cadastra uma sala VIP (multiplicador de preço 3.0×). */
    public Vip cadastrarSalaVip(int id, int capacidade) {
        validarIdUnico(id);
        validarCapacidade(capacidade);
        Vip sala = new Vip(id, capacidade);        // construtor real do seu projeto
        salas.add(sala);
        System.out.println("[SalaNegocios] Sala VIP #" + id + " cadastrada (cap.: " + capacidade + ").");
        return sala;
    }

    /** Cadastra uma sala IMAX (multiplicador de preço 2.0×). */
    public Imax cadastrarSalaImax(int id, int capacidade) {
        validarIdUnico(id);
        validarCapacidade(capacidade);
        Imax sala = new Imax(id, capacidade);      // construtor real do seu projeto
        salas.add(sala);
        System.out.println("[SalaNegocios] Sala IMAX #" + id + " cadastrada (cap.: " + capacidade + ").");
        return sala;
    }

    // -------------------------------------------------------------------------
    // Atualização de dados
    // -------------------------------------------------------------------------

    /**
     * Permite sobrescrever o preço base de uma sala para promoções específicas.
     * Não deve ser chamado enquanto há sessões abertas na sala
     * (responsabilidade de quem orquestra, ex.: SessaoController).
     */
    public void atualizarPreco(Sala sala, double novoPreco) {
        if (novoPreco < 0) throw new IllegalArgumentException("Preço não pode ser negativo.");
        sala.setPreco(novoPreco);
        System.out.println("[SalaNegocios] Preço da sala #" + sala.getId() + " → R$ " + novoPreco);
    }

    public void atualizarCapacidade(Sala sala, int novaCapacidade) {
        validarCapacidade(novaCapacidade);
        sala.setCapacidade(novaCapacidade);
        System.out.println("[SalaNegocios] Capacidade da sala #" + sala.getId() + " → " + novaCapacidade);
    }

    // -------------------------------------------------------------------------
    // Consultas
    // -------------------------------------------------------------------------

    public List<Sala> listarSalas() {
        return new ArrayList<>(salas);
    }

    public Sala buscarPorId(int id) {
        return salas.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    /** Filtra salas por subtipo (Comum.class, Vip.class ou Imax.class). */
    public List<Sala> listarPorTipo(Class<? extends Sala> tipo) {
        return salas.stream().filter(tipo::isInstance).toList();
    }

    public void removerSala(Sala sala) {
        salas.remove(sala);
        System.out.println("[SalaNegocios] Sala #" + sala.getId() + " removida.");
    }

    // -------------------------------------------------------------------------
    // Validações internas
    // -------------------------------------------------------------------------

    private void validarIdUnico(int id) {
        if (buscarPorId(id) != null)
            throw new IllegalArgumentException("Já existe uma sala com ID " + id + ".");
    }

    private void validarCapacidade(int capacidade) {
        if (capacidade <= 0)
            throw new IllegalArgumentException("Capacidade deve ser maior que zero.");
    }
}
