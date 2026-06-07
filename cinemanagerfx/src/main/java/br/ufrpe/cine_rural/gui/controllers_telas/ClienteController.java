package br.ufrpe.cine_rural.gui.controllers_telas;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ClienteController {

    @FXML
    private TabPane meuTabPane;

    @FXML
    private Tab tabCadastro;

    @FXML
    private Tab tabServicos;

    @FXML
    private Tab tabNota;

    @FXML
    private Tab tabIngresso;

    @FXML
    private TextField txtFieldNome;

    @FXML
    private TextField txtFieldIdade;

    @FXML
    private TextField txtFieldEmail;

    @FXML
    private TextField txtFieldCpf;

    @FXML
    private RadioButton btnSim;

    @FXML
    private RadioButton btnNao;


    //botões para seguir
    @FXML
    private Button btnSeguir;

    @FXML
    private Button btnNotaFiscal;

    @FXML
    private Button btnIngresso;

    //butões para voltar
    @FXML
    private Button btnVoltar;
    @FXML
    private Button btnVoltar3;
    @FXML
    private Button btnVoltar1;
    @FXML
    private Button btnVoltar2;

    @FXML
    public void irparaServico(){
        if (meuTabPane != null && tabServicos != null) {

            meuTabPane.getSelectionModel().select(tabServicos);
        }
    }

    //volta para cadastro
    @FXML
    public void voltar3(){
        if (meuTabPane!= null && tabCadastro != null) {

            meuTabPane.getSelectionModel().select(tabCadastro);
        }
    }

    @FXML
    public void IrparaIngresso(){
        if (meuTabPane != null && tabIngresso != null) {

            meuTabPane.getSelectionModel().select(tabIngresso);
        }
    }

    //volta para serviços
    public void voltar2(){
        if (meuTabPane!= null && tabIngresso != null) {

            meuTabPane.getSelectionModel().select(tabServicos);
        }

    }

    @FXML
    public void IrparaNota(){
        if (meuTabPane != null && tabNota != null) {

            meuTabPane.getSelectionModel().select(tabNota);
        }
    }

    // volta para serviços
    @FXML
    public void volta1(){
        if (meuTabPane!= null && tabServicos != null) {

            meuTabPane.getSelectionModel().select(tabServicos);
        }
    }

}
