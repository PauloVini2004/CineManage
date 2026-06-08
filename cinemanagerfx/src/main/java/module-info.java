module br.ufrpe.cine_rural {
    requires javafx.controls;
    requires javafx.fxml;

    requires kernel;
    requires layout;
    
    opens br.ufrpe.cine_rural.gui.controllers_telas to javafx.fxml;
    exports br.ufrpe.cine_rural.gui;
    opens br.ufrpe.cine_rural.gui.controllers_telas.emergencia to javafx.fxml;
}