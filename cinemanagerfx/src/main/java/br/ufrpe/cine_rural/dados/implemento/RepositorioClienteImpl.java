package br.ufrpe.cine_rural.dados.implemento;

import br.ufrpe.cine_rural.dados.interfaces.IRepositorioCliente;
import br.ufrpe.cine_rural.model.Cliente;
import java.util.ArrayList;


public class RepositorioClienteImpl implements IRepositorioCliente {

    private ArrayList<Cliente> clientes;

    public RepositorioClienteImpl() {
        clientes = new ArrayList<>();
    }

    @Override
    public void cadastrar(Cliente cliente) {
        clientes.add(cliente);
    }

    @Override
    public Cliente buscar(String cpf) {

        for (Cliente cliente : clientes) {

            if(cliente.getCpf().equals(cpf)) {
                return cliente;
            }
        }

        return null;
    }

    @Override
    public void atualizar(Cliente cliente) {

    }

    @Override
    public void remover(String cpf) {

        Cliente cliente = buscar(cpf);

        if(cliente != null) {
            clientes.remove(cliente);
        }
    }

    @Override
    public ArrayList<Cliente> listar() {
        return clientes;
    }


}