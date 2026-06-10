package br.ufrpe.cine_rural.negocios;

import br.ufrpe.cine_rural.enums.CategoriaMeiaEntrada;
import br.ufrpe.cine_rural.model.Assento;
import br.ufrpe.cine_rural.model.Cliente;
import br.ufrpe.cine_rural.model.Ingresso;
import br.ufrpe.cine_rural.model.Sessao;

public class IngressoNegocios {

    private final SessaoNegocios sessaoNegocios;
    private final ClienteNegocios clienteNegocios;

    public IngressoNegocios(SessaoNegocios sessaoNegocios,
                            ClienteNegocios clienteNegocios) {
        this.sessaoNegocios  = sessaoNegocios;
        this.clienteNegocios = clienteNegocios;
    }

    public Ingresso venderIngresso(Sessao sessao, Cliente cliente, Assento assento,
                                   double precoBase, CategoriaMeiaEntrada categoria,
                                   boolean acompanhante) {

        if (!cliente.podeAssistir(sessao.getFilme(), acompanhante))
            throw new IllegalStateException(
                    "Venda negada: cliente com " + cliente.getIdade()
                            + " anos nao pode assistir a este filme sem acompanhante. (REQ19)");

        Ingresso ingresso = new Ingresso(sessao, assento, precoBase, categoria);
        ingresso.setCliente(cliente);

        sessaoNegocios.adicionarIngresso(sessao.getHorario(), ingresso);

        System.out.println("[IngressoNegocios] Ingresso vendido | Assento: "
                + assento.getCodigo()
                + " | R$ " + String.format("%.2f", ingresso.getPreco())
                + " | Categoria: " + categoria);

        clienteNegocios.enviarConfirmacaoCompra(cliente, ingresso);

        return ingresso;
    }

    public void cancelarIngresso(Sessao sessao, Ingresso ingresso) {
        if (sessaoNegocios.sessaoJaIniciou(sessao.getHorario()))
            throw new IllegalStateException(
                    "Cancelamento nao permitido apos o inicio da sessao. (REQ23)");

        sessao.getIngressos().remove(ingresso);
        ingresso.getAssento().liberar();

        System.out.println("[IngressoNegocios] Ingresso do assento '"
                + ingresso.getAssento().getCodigo() + "' cancelado.");
    }

    public double calcularTaxaOcupacao(Sessao sessao) {
        int capacidade = sessao.getSala().getCapacidade();
        if (capacidade == 0) return 0.0;
        int vendidos = sessao.getIngressos() != null ? sessao.getIngressos().size() : 0;
        return (double) vendidos / capacidade * 100.0;
    }
}