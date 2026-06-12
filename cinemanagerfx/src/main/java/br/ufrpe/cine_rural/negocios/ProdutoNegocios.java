package br.ufrpe.cine_rural.negocios;

import br.ufrpe.cine_rural.dados.implemento.RepositorioProdutoImpl;
import br.ufrpe.cine_rural.model.loja.Produto;

import java.util.ArrayList;
import java.util.List;




public class ProdutoNegocios{

    private final RepositorioProdutoImpl repositorioProduto;


    private static final int LIMIAR_ESTOQUE_BAIXO = 5;


    public ProdutoNegocios(RepositorioProdutoImpl repositorioProduto) {
        this.repositorioProduto = repositorioProduto;
    }


     // Cadastra produto da bomboniere com controle de estoque.

    public void cadastrarProduto(int id, String nome, double preco, int qtdEstoque, String caminhoImagem) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio.");
        }
        if (preco < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo.");
        }
        if (qtdEstoque < 0) {
            throw new IllegalArgumentException("Quantidade em estoque não pode ser negativa.");
        }
        if (repositorioProduto.buscar(id) != null) {
            throw new IllegalStateException("Já existe um produto com o ID: " + id);
        }


        repositorioProduto.cadastrar(new Produto(id, nome, preco, qtdEstoque, caminhoImagem));
    }


     //Busca produto pelo ID.

    public Produto buscarProduto(int id) {
        Produto produto = repositorioProduto.buscar(id);
        if (produto == null) {
            throw new IllegalArgumentException("Produto não encontrado com ID: " + id);
        }
        return produto;
    }


     // Atualiza dados de um produto existente.

    public void atualizarProduto(int id, String novoNome, double novoPreco, int novaQtd, String novoCaminhoImagem) {
        Produto produto = buscarProduto(id);
        produto.setNome(novoNome);
        produto.setPreco(novoPreco);
        produto.setQtdEstoque(novaQtd);
        produto.setCaminhoImagem(novoCaminhoImagem);
        repositorioProduto.atualizar(produto);
    }


    public void removerProduto(int id) {
        buscarProduto(id); // garante existência
        repositorioProduto.remover(id);
    }

    public ArrayList<Produto> listarProdutos() {
        return repositorioProduto.listar();
    }



     // Lança exceção se estoque for insuficiente para a quantidade pedida.

    public void validarEstoque(Produto produto, int quantidadeDesejada) {
        if (produto.getQtdEstoque() < quantidadeDesejada) {
            throw new IllegalStateException(
                "Estoque insuficiente para '" + produto.getNome()
                + "'. Disponível: " + produto.getQtdEstoque()
                + ", solicitado: " + quantidadeDesejada
            );
        }
    }


    public boolean isEstoqueVazio() {
        return this.listarProdutos().isEmpty();
    }


     // Retorna a lista dos produtos abaixo do limiar.

    public List<Produto> verificarEstoqueBaixo() {
        List<Produto> alertas = new ArrayList<>();

        for (Produto p : repositorioProduto.listar()) {
            if (p.getQtdEstoque() <= LIMIAR_ESTOQUE_BAIXO) {
                alertas.add(p);
            }
        }


        return alertas;
    }
}