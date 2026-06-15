package br.ufrpe.cine_rural.dados.implemento;

import br.ufrpe.cine_rural.dados.interfaces.IRepositorioProduto;
import br.ufrpe.cine_rural.model.loja.Produto;
import br.ufrpe.cine_rural.util.ProdutoArquivo;
import java.util.ArrayList;

public class RepositorioProdutoImpl implements IRepositorioProduto {

    private static RepositorioProdutoImpl instancia;
    private ArrayList<Produto> produtos;


    private RepositorioProdutoImpl() {

        System.out.println(
                "CONSTRUTOR REPOSITORIO -> "
                        + System.identityHashCode(this)
        );

        produtos = new ArrayList<>();
        produtos = ProdutoArquivo.carregar();
    }

    public static RepositorioProdutoImpl getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioProdutoImpl();
        }

        System.out.println(
                "INSTANCIA CRIADA -> "
                        + System.identityHashCode(instancia)
        );

        return instancia;
    }

    @Override
    public void cadastrar(Produto produto) {
        produtos.add(produto);
        ProdutoArquivo.salvar(produtos);
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
            produto.setCaminhoImagem(produtoAtualizado.getCaminhoImagem());
        }
        ProdutoArquivo.salvar(produtos);
    }

    @Override
    public void remover(int id) {
        Produto produto = buscar(id);
        if (produto != null) {
            produtos.remove(produto);
        }
        ProdutoArquivo.salvar(produtos);
    }

    @Override
    public ArrayList<Produto> listar() {
        return produtos;
    }
}