module br.ufrpe.cine_rural {
    requires javafx.controls;
    requires javafx.fxml;
    
    opens br.ufrpe.cine_rural.gui.controllers_telas to javafx.fxml;
    exports br.ufrpe.cine_rural.gui;
}