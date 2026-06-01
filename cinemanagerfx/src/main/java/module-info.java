module br.ufrpe.cine_rural {
    requires javafx.controls;
    requires javafx.fxml;


    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens br.ufrpe.cine_rural.gui.controllers_telas to javafx.fxml;
    exports br.ufrpe.cine_rural.gui;
}