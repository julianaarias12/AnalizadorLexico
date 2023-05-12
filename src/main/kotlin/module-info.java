module co.edu.uniquindio.analizador {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    opens co.edu.uniquindio.analizador.app to javafx.fxml;
    opens co.edu.uniquindio.analizador.controlador to javafx.fxml;
    opens co.edu.uniquindio.analizador.lexico to javafx.fxml;
    exports co.edu.uniquindio.analizador.app;
    exports co.edu.uniquindio.analizador.controlador;
    exports co.edu.uniquindio.analizador.lexico;

}


