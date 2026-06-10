package br.ufrpe.cine_rural.dados.implemento;

import br.ufrpe.cine_rural.dados.interfaces.IRepositorioSala;
import br.ufrpe.cine_rural.model.tiposala.Sala;

import java.util.ArrayList;

public class RepositorioSalaImpl implements IRepositorioSala {

    private static RepositorioSalaImpl instancia;

    private final ArrayList<Sala> salas;

    private RepositorioSalaImpl() {
        this.salas = new ArrayList<>();
    }

    public static RepositorioSalaImpl getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioSalaImpl();
        }
        return instancia;
    }

    @Override
    public void cadastrar(Sala sala) {
        salas.add(sala);
    }

    @Override
    public Sala buscar(int id) {
        for (Sala sala : salas) {
            if (sala.getId() == id) {
                return sala;
            }
        }
        return null;
    }

    @Override
    public void remover(int id) {
        Sala sala = buscar(id);

        if (sala != null) {
            salas.remove(sala);
        }
    }

    @Override
    public ArrayList<Sala> listar() {
        return salas;
    }
}