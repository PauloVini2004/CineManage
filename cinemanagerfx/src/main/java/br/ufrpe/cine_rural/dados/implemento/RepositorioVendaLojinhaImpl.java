package br.ufrpe.cine_rural.dados.implemento;

import br.ufrpe.cine_rural.dados.interfaces.IRepositorioVendaLojinha;
import br.ufrpe.cine_rural.model.loja.ItemVenda;
import br.ufrpe.cine_rural.model.loja.VendaLojinha;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RepositorioVendaLojinhaImpl
        implements IRepositorioVendaLojinha {

    private final ArrayList<VendaLojinha> vendas;

    public RepositorioVendaLojinhaImpl() {
        vendas = new ArrayList<>();
    }

    private void salvarVendaCSV(
            VendaLojinha venda
    ) {

        try (
                FileWriter fw =
                        new FileWriter(
                                "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/dados/arquivoscsv/vendas_lojinha.csv",
                                true
                        );

                PrintWriter pw =
                        new PrintWriter(fw)
        ) {

            for (ItemVenda item : venda.getItens()) {

                pw.println(
                        item.getProduto().getId()
                                + ";"
                                + item.getProduto().getNome()
                                + ";"
                                + item.getQuantidade()
                                + ";"
                                + item.getSubtotal()
                );
            }

            System.out.println(
                    "SALVANDO VENDA NO CSV"
            );

        } catch (Exception e) {

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