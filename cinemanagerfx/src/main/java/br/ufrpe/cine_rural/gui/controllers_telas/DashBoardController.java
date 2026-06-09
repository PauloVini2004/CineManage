package main.java.br.ufrpe.cine_rural.gui.controllers_telas;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;

public class DashBoardController {

        @FXML
        private BarChart<String, Number> graficoBilheteria;

        @FXML
        private PieChart graficoBomboniere;

        @FXML
        private ListView<String> listaAlertas;

        @FXML
        private ProgressBar barraOcupacao;

        @FXML
        private Label lblTaxa;

        @FXML
        public void initialize() {

            atualizarDashboard();

        }

        public void atualizarDashboard() {

            carregarBilheteria();
            carregarBomboniere();
            carregarAlertas();

        }

        private void carregarBilheteria() {

        }

        private void carregarBomboniere() {

        }

        private void carregarAlertas() {

        }

}

