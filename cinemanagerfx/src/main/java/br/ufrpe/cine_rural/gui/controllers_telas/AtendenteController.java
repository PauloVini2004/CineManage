package br.ufrpe.cine_rural.gui.controllers_telas;

import br.ufrpe.cine_rural.dados.implemento.RepositorioSessaoImpl;
import br.ufrpe.cine_rural.negocios.SessaoNegocios;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AtendenteController {
    @FXML
    private Button btnCprProd;

    @FXML
    private Button btnCprIngr;

    @FXML
    private Button btnEncCxa;

    @FXML
    private Button btnCancelIngr;

    @FXML
    private Button btnText;

    private final SessaoNegocios sessaoNegocios =
            new SessaoNegocios(RepositorioSessaoImpl.getInstancia());

    @FXML
    public void initialize() {
        // Atualiza status das sessões ao abrir a tela do atendente
        sessaoNegocios.atualizarStatusPorHorarioAtual();
    }

    @FXML
    public void btnCprIngrAction() {
        ScreenManager.getInstance().showFilmesScreen();
    }

    @FXML
    public void btnCancelIngrAction() {
        ScreenManager.getInstance().showCancelarIngressoScreen();
    }

    @FXML
    public void btnCprProdAction() {
        /*
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/ufrpe/cine_rural/gui/TelasProduto/Produto.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnCprIngr.getScene().getWindow();
            stage.setScene(new Scene(root));

            scene.getStylesheets().add(getClass().getResource(
                    "/br/ufrpe/cine_rural/gui/EstiloFilmes.css").toExternalForm());

            stage.show();
        } catch (Exception e) {
            System.out.println("Erro ao tentar voltar para a tela de Atendente. Verifique o caminho do FXML.");
            e.printStackTrace();
        }
        */

        ScreenManager.getInstance().showProdutosScreen();

    }

    @FXML
    public void btnEncCxaAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/ufrpe/cine_rural/gui/home-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnEncCxa.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.out.println("Erro ao tentar voltar para a tela de Home. Verifique o caminho do FXML.");
            e.printStackTrace();
        }
    }

    /*
    @FXML
    public void btnTextAction() {
        ScreenManager.getInstance().showAssentosScreen();
    }
    */

}
