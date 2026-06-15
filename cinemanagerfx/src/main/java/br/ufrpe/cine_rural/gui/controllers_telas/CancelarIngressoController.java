package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.dados.implemento.RepositorioSessaoImpl;
import br.ufrpe.cine_rural.dados.implemento.RepositorioVendaIngressoImpl;
import br.ufrpe.cine_rural.enums.StatusSessao;
import br.ufrpe.cine_rural.model.Ingresso;
import br.ufrpe.cine_rural.model.Sessao;
import br.ufrpe.cine_rural.negocios.SessaoNegocios;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class CancelarIngressoController {

    @FXML private TextField txtNomeCliente;
    @FXML private TextField txtCodigoAssento;
    @FXML private TextField txtHorarioSessao;
    @FXML private Label lblMensagem;
    @FXML private Button btnCancelarIngresso;
    @FXML private Button btnVoltar;

    private final RepositorioSessaoImpl repositorioSessao =
            RepositorioSessaoImpl.getInstancia();
    private final RepositorioVendaIngressoImpl repositorioVenda =
            RepositorioVendaIngressoImpl.getInstancia();
    private final SessaoNegocios sessaoNegocios =
            new SessaoNegocios(repositorioSessao);

    @FXML
    public void initialize() {
        sessaoNegocios.atualizarStatusPorHorarioAtual();
    }

    @FXML
    public void btnCancelarIngressoAction() {
        lblMensagem.setStyle("-fx-text-fill: #b90000;");

        String nome    = txtNomeCliente.getText().trim();
        String assento = txtCodigoAssento.getText().trim().toUpperCase();
        String horStr  = txtHorarioSessao.getText().trim();

        // Validações básicas
        if (nome.isEmpty() || assento.isEmpty() || horStr.isEmpty()) {
            lblMensagem.setText("Por favor, preencha todos os campos.");
            return;
        }

        LocalDateTime horario;
        try {
            horario = LocalDateTime.parse(horStr);
        } catch (DateTimeParseException e) {
            lblMensagem.setText("Formato de horário inválido. Use: AAAA-MM-DDTHH:MM"); //ficou horrivel esse layout de data que agoniakkkk
            return;
        }

        // Busca a sessão
        Sessao sessao = repositorioSessao.buscar(horario);
        if (sessao == null) {
            lblMensagem.setText("Nenhuma sessão encontrada para o horário informado.");
            return;
        }

        // Verifica se a sessão já está em exibição ou encerrada
        if (sessao.getStatus() == StatusSessao.EM_EXIBICAO
                || sessao.getStatus() == StatusSessao.ENCERRADA) {
            lblMensagem.setText("Cancelamento não permitido: a sessão já iniciou ou foi encerrada.");
            return;
        }

        // Busca o ingresso com assento e nome do cliente correspondentes
        Ingresso ingressoParaCancelar = null;
        for (Ingresso ing : sessao.getIngressos()) {
            boolean assentoOk = ing.getAssento().getCodigo().equalsIgnoreCase(assento);
            boolean clienteOk = ing.getCliente() != null
                    && ing.getCliente().getNome().equalsIgnoreCase(nome);
            if (assentoOk && clienteOk) {
                ingressoParaCancelar = ing;
                break;
            }
        }

        if (ingressoParaCancelar == null) {
            lblMensagem.setText("Ingresso não encontrado. Verifique os dados informados.");
            return;
        }

        // Remove o ingresso da sessão em memória e libera o assento
        sessao.getIngressos().remove(ingressoParaCancelar);
        ingressoParaCancelar.getAssento().liberar();
        repositorioSessao.atualizar(sessao);

        AssentoController.liberarAssentoNoCache(horario, assento);

        // Remove do CSV de vendas
        boolean removidoDoCSV = repositorioVenda.cancelarVenda(assento, horario.toString());

        if (removidoDoCSV) {
            lblMensagem.setStyle("-fx-text-fill: #00cc66;");
            lblMensagem.setText("Ingresso cancelado com sucesso! Assento " + assento + " foi liberado.");
        } else {
            // Mesmo sem entrada no CSV, o cancelamento em memória já foi feito
            lblMensagem.setStyle("-fx-text-fill: #ffaa00;");
            lblMensagem.setText("Ingresso cancelado em memória. Registro CSV não encontrado.");
        }

        // Limpa os campos
        txtNomeCliente.clear();
        txtCodigoAssento.clear();
        txtHorarioSessao.clear();
    }

    @FXML
    public void btnVoltarAction() {
        ScreenManager.getInstance().showAtendenteScreen();
    }
}
