package br.ufrpe.cine_rural.dados.implemento;

import br.ufrpe.cine_rural.dados.interfaces.IRepositorioVendaIngresso;
import br.ufrpe.cine_rural.model.Ingresso;
import br.ufrpe.cine_rural.model.VendaIngresso;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RepositorioVendaIngressoImpl implements IRepositorioVendaIngresso {

    private static RepositorioVendaIngressoImpl instancia;
    private final ArrayList<VendaIngresso> vendas;

    private RepositorioVendaIngressoImpl() {
        vendas = new ArrayList<>();
    }

    public static synchronized RepositorioVendaIngressoImpl getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioVendaIngressoImpl();
        }
        return instancia;
    }

    @Override
    public void cadastrar(VendaIngresso venda) {
        vendas.add(venda);
        salvarVendaCSV(venda);
    }

    @Override
    public ArrayList<VendaIngresso> listar() {
        return vendas;
    }

    private void salvarVendaCSV(VendaIngresso venda) {

        System.out.println("ENTROU NO salvarVendaCSV");
        System.out.println("Ingressos: " + venda.getIngressos().size());

        File arquivo = new File("cinemanagerfx/src/main/java/br/ufrpe/cine_rural/dados/arquivoscsv/vendas_ingresso.csv");
        System.out.println(
                "CSV EM: " +
                        arquivo.getAbsolutePath()
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
                        "DataVenda;FormaPagamento;Filme;Assento;Categoria;Preco;Cliente;Idade"
                );
            }

            for (Ingresso ingresso : venda.getIngressos()) {

                String nomeCliente = "";
                String idadeCliente = "";

                if (ingresso.getCliente() != null) {
                    nomeCliente  = ingresso.getCliente().getNome();
                    idadeCliente = String.valueOf(ingresso.getCliente().getIdade());
                }

                pw.println(
                        venda.getDataVenda() + ";" +
                                venda.getFormaPagamento() + ";" +
                                ingresso.getSessao().getFilme().getTitulo() + ";" +
                                ingresso.getAssento().getCodigo() + ";" +
                                ingresso.getCategoria() + ";" +
                                ingresso.getPreco() + ";" +
                                nomeCliente + ";" +
                                idadeCliente
                );
            }

            pw.flush();

            System.out.println(
                    "Arquivo existe? " +
                            arquivo.exists()
            );

        } catch (Exception e) {
            System.out.println("ERRO AO SALVAR CSV");
            e.printStackTrace();
        }
    }
}