module br.ufrpe.cine_rural {
    requires javafx.controls;
    requires javafx.fxml;

    requires kernel;
    requires layout;

    exports br.ufrpe.cine_rural.gui;

    opens br.ufrpe.cine_rural.gui.controllers_telas to javafx.fxml;
    opens br.ufrpe.cine_rural.gui.controllers_telas.emergencia to javafx.fxml;

    opens br.ufrpe.cine_rural.model to javafx.base;
    opens br.ufrpe.cine_rural.dados.interfaces to javafx.fxml;
}