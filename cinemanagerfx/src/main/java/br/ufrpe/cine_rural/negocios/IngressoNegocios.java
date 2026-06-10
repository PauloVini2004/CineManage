package br.ufrpe.cine_rural.negocios;

import br.ufrpe.cine_rural.enums.CategoriaMeiaEntrada;
import br.ufrpe.cine_rural.enums.StatusSessao;
import br.ufrpe.cine_rural.model.Assento;
import br.ufrpe.cine_rural.model.Cliente;
import br.ufrpe.cine_rural.model.Ingresso;
import br.ufrpe.cine_rural.model.Sessao;

/**
 * Negócio responsável pela bilheteria.
 *
 * REQ08 – Registrar venda de ingressos com escolha de assentos.
 * REQ09 – Aplicar regras de meia-entrada conforme categorias legais (CategoriaMeiaEntrada).
 * REQ18 – Enviar confirmação de compra para o e-mail do cliente
 *          (delegado ao ClienteNegocios).
 * REQ19 – Não permitir venda para menores sem acompanhante em sessões restritas
 *          (verificado via Cliente.podeAssistir).
 * REQ21 – Impedir venda de assento já ocupado (verificado no construtor de Ingresso).
 * REQ23 – Não permitir cancelamento após início da sessão.
 */
public class IngressoNegocios {

    private final SessaoNegocios sessaoNegocios;
    private final ClienteNegocios clienteNegocios;

    public IngressoNegocios(SessaoNegocios sessaoNegocios,
                            ClienteNegocios clienteNegocios) {
        this.sessaoNegocios  = sessaoNegocios;
        this.clienteNegocios = clienteNegocios;
    }

    // -------------------------------------------------------------------------
    // REQ08 – Venda de ingresso
    // -------------------------------------------------------------------------

    /**
     * Registra a compra de um ingresso.
     *
     * @param sessao       Sessão escolhida
     * @param cliente      Cliente comprador
     * @param assento      Objeto Assento desejado
     * @param precoBase    Preço base da sala (sem aplicar meia-entrada)
     * @param categoria    Categoria do ingresso (INTEIRA ou meia-entrada)
     * @param acompanhante Se menor está acompanhado (REQ19)
     * @return Ingresso criado e registrado na sessão
     */
    public Ingresso venderIngresso(Sessao sessao, Cliente cliente, Assento assento,
                                   double precoBase, CategoriaMeiaEntrada categoria,
                                   boolean acompanhante) {

        // REQ19 – Verifica classificação indicativa via Cliente.podeAssistir()
        if (!cliente.podeAssistir(sessao.getFilme(), acompanhante))
            throw new IllegalStateException(
                    "Venda negada: cliente com " + cliente.getIdade()
                            + " anos nao pode assistir a este filme sem acompanhante. (REQ19)");

        // REQ21 – O construtor de Ingresso já lança IllegalArgumentException
        //         se o assento estiver OCUPADO, e chama assento.ocupar() ao final.
        // REQ09 – CategoriaMeiaEntrada.INTEIRA = preço cheio; qualquer outro = metade.
        //         A lógica está em Ingresso.calcularValor().
        Ingresso ingresso = new Ingresso(sessao, assento, precoBase, categoria);
        ingresso.setCliente(cliente);

        // Registra o ingresso na sessão (chave = horário da sessão)
        sessaoNegocios.adicionarIngresso(sessao.getHorario(), ingresso);

        System.out.println("[IngressoNegocios] Ingresso vendido | Assento: "
                + assento.getCodigo()
                + " | R$ " + String.format("%.2f", ingresso.getPreco())
                + " | Categoria: " + categoria);

        // REQ18 – Confirmação por e-mail
        clienteNegocios.enviarConfirmacaoCompra(cliente, ingresso);

        return ingresso;
    }

    // -------------------------------------------------------------------------
    // REQ23 – Cancelamento (proibido após início da sessão)
    // -------------------------------------------------------------------------

    /**
     * Cancela um ingresso liberando o assento.
     * Delegado ao SessaoNegocios.sessaoJaIniciou() para verificar o status.
     *
     * @throws IllegalStateException se a sessão já estiver EM_EXIBICAO ou ENCERRADA
     */
    public void cancelarIngresso(Sessao sessao, Ingresso ingresso) {
        if (sessaoNegocios.sessaoJaIniciou(sessao.getHorario()))
            throw new IllegalStateException(
                    "Cancelamento nao permitido apos o inicio da sessao. (REQ23)");

        sessao.getIngressos().remove(ingresso);
        ingresso.getAssento().liberar();

        System.out.println("[IngressoNegocios] Ingresso do assento '"
                + ingresso.getAssento().getCodigo() + "' cancelado.");
    }

    // -------------------------------------------------------------------------
    // REQ12 / REQ15 – Taxa de ocupação
    // -------------------------------------------------------------------------

    /**
     * Calcula a taxa de ocupação da sessão em percentual.
     */
    public double calcularTaxaOcupacao(Sessao sessao) {
        int capacidade = sessao.getSala().getCapacidade();
        if (capacidade == 0) return 0.0;
        int vendidos = sessao.getIngressos() != null ? sessao.getIngressos().size() : 0;
        return (double) vendidos / capacidade * 100.0;
    }
}