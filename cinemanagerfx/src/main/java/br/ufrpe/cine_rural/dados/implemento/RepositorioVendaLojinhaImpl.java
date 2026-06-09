package br.ufrpe.cine_rural.dados.implemento;

import br.ufrpe.cine_rural.dados.interfaces.IRepositorioVendaLojinha;
import br.ufrpe.cine_rural.model.loja.ItemVenda;
import br.ufrpe.cine_rural.model.loja.VendaLojinha;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RepositorioVendaLojinhaImpl
        implements IRepositorioVendaLojinha {

    private final ArrayList<VendaLojinha> vendas;

    public RepositorioVendaLojinhaImpl() {
        vendas = new ArrayList<>();
    }

    private void salvarVendaCSV(VendaLojinha venda) {

        File arquivo = new File(
                "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/dados/arquivoscsv/vendas_lojinha.csv"
        );

        File pasta = arquivo.getParentFile();

        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs();
        }

        boolean arquivoNovo = !arquivo.exists();

        try (FileWriter fw = new FileWriter(arquivo, true);
             PrintWriter pw = new PrintWriter(fw)) {

            if (arquivoNovo) {
                pw.println(
                        "DataVenda;FormaPagamento;Cliente;Produto;Quantidade;Subtotal"
                );
            }

            String nomeCliente = "";

            if (venda.getCliente() != null) {
                nomeCliente = venda.getCliente().getNome();
            }

            for (ItemVenda item : venda.getItens()) {

                pw.println(
                        venda.getDataVenda() + ";" +
                                venda.getFormaPagamento() + ";" +
                                nomeCliente + ";" +
                                item.getProduto().getNome() + ";" +
                                item.getQuantidade() + ";" +
                                item.getSubtotal()
                );
            }

            pw.flush();

            System.out.println("SALVANDO VENDA NO CSV");

        } catch (Exception e) {

            System.out.println("ERRO AO SALVAR CSV");
            e.printStackTrace();
        }
    }

    @Override
    public void cadastrar(
            VendaLojinha venda
    ) {

        vendas.add(venda);

        salvarVendaCSV(venda);
    }

    @Override
    public VendaLojinha buscar(
            int indice
    ) {

        if (indice >= 0 &&
                indice < vendas.size()) {

            return vendas.get(indice);
        }

        return null;
    }

    @Override
    public void remover(
            int indice
    ) {

        if (indice >= 0 &&
                indice < vendas.size()) {

            vendas.remove(indice);
        }
    }

    @Override
    public ArrayList<VendaLojinha> listar() {
        return vendas;
    }
}