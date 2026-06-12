package br.ufrpe.cine_rural.negocios;

import br.ufrpe.cine_rural.dados.implemento.RepositorioClienteImpl;
import br.ufrpe.cine_rural.model.Cliente;
import br.ufrpe.cine_rural.model.Ingresso;

import java.util.ArrayList;

public class ClienteNegocios {

    private final RepositorioClienteImpl repositorio;

    public ClienteNegocios(RepositorioClienteImpl repositorio) {
        this.repositorio = repositorio;
    }

    public Cliente cadastrarCliente(String nome, String cpf, int idade, String email) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome é obrigatório.");
        if (cpf == null || cpf.isBlank())
            throw new IllegalArgumentException("CPF é obrigatório.");
        if (repositorio.buscar(cpf) != null)
            throw new IllegalArgumentException("Já existe um cliente com CPF " + cpf + ".");
        if (idade < 0)
            throw new IllegalArgumentException("Idade não pode ser negativa.");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email é obrigatório.");

        Cliente cliente = new Cliente(nome, cpf, idade, email);
        repositorio.cadastrar(cliente);
        System.out.println("[ClienteController] Cliente cadastrado: " + nome);
        return cliente;
    }

    public void atualizarCliente(Cliente cliente, String novoNome, int novaIdade, String novoEmail) {
        if (novoNome != null && !novoNome.isBlank()) cliente.setNome(novoNome);
        if (novaIdade >= 0) cliente.setIdade(novaIdade);
        if (novoEmail != null && !novoEmail.isBlank()) cliente.setEmail(novoEmail);
        repositorio.atualizar(cliente);
        System.out.println("[ClienteController] Dados atualizados para: " + cliente.getNome());
    }


    public void enviarConfirmacaoCompra(Cliente cliente, Ingresso ingresso) {


        System.out.println("Confirmação de compra para "
                + cliente.getNome()
                + " | Assento: " + ingresso.getAssento()
                + " | R$ " + String.format("%.2f", ingresso.getPreco()));
    }


    public ArrayList<Cliente> listarClientes() {
        return repositorio.listar();
    }

    public Cliente buscarPorCpf(String cpf) {
        return repositorio.buscar(cpf);
    }

    public void removerCliente(String cpf) {
        repositorio.remover(cpf);
        System.out.println("[ClienteController] Cliente com CPF " + cpf + " removido.");
    }
}