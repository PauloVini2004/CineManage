package br.ufrpe.cine_rural.dados.interfaces;

import br.ufrpe.cine_rural.model.Cliente;
import java.util.ArrayList;

public interface IRepositorioCliente {

    void cadastrar(Cliente cliente);

    Cliente buscar(String cpf);

    void atualizar(Cliente cliente);

    void remover(String cpf);

    ArrayList<Cliente> listar();
}