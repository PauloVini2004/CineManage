package br.ufrpe.cine_rural.dados.implemento;

import br.ufrpe.cine_rural.dados.interfaces.IRepositorioProduto;
import br.ufrpe.cine_rural.model.loja.Produto;
import java.util.ArrayList;

public class RepositorioProdutoImpl implements IRepositorioProduto {

    // Instância única global do repositório
    private static RepositorioProdutoImpl instancia;
    private ArrayList<Produto> produtos;


    private RepositorioProdutoImpl() {
        produtos = new ArrayList<>();
    }

    // recuperar instância
    public static RepositorioProdutoImpl getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioProdutoImpl();
        }
        return instancia;
    }

    @Override
    public void cadastrar(Produto produto) {
        produtos.add(produto);
    }

    @Override
    public Produto buscar(int id) {
        for (Produto produto : produtos) {
            if (produto.getId() == id) {
                return produto;
            }
        }
        return null;
    }

    @Override
    public void atualizar(Produto produtoAtualizado) {
        Produto produto = buscar(produtoAtualizado.getId());
        if (produto != null) {
            produto.setNome(produtoAtualizado.getNome());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setQtdEstoque(produtoAtualizado.getQtdEstoque());
        }
    }

    @Override
    public void remover(int id) {
        Produto produto = buscar(id);
        if (produto != null) {
            produtos.remove(produto);
        }
    }

    @Override
    public ArrayList<Produto> listar() {
        return produtos;
    }
}