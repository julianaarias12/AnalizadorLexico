package co.edu.uniquindio.analizador.controlador

import co.edu.uniquindio.analizador.lexico.AnalizadorLexico
import co.edu.uniquindio.analizador.lexico.ErrorLexico
import co.edu.uniquindio.analizador.lexico.Token

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import java.net.URL
import java.util.*

class CompiladorController: Initializable {

    private lateinit var analizadorLexico: AnalizadorLexico

    @FXML
    lateinit var txtCodigo: TextArea
    @FXML
    lateinit var tablaPrincipal: TableView<Token>
    @FXML
    lateinit var colRow: TableColumn<Token, String>
    @FXML
    lateinit var colColumn: TableColumn<Token, String>
    @FXML
    lateinit var colCategory: TableColumn<Token, String>
    @FXML
    lateinit var coltoken: TableColumn<Token, String>
    @FXML
    lateinit var tableError: TableView<ErrorLexico>
    @FXML
    lateinit var colError: TableColumn<ErrorLexico, String>
    @FXML
    lateinit var colRowError: TableColumn<ErrorLexico, String>
    @FXML
    lateinit var colColumnError: TableColumn<ErrorLexico, String>



    @FXML
    private fun analyze() {
        tablaPrincipal.items.clear()
        tableError.items.clear()

        if (txtCodigo.text.length > 0) {
            analizadorLexico = AnalizadorLexico(txtCodigo.text)
            analizadorLexico.analyze()
            tablaPrincipal.items = FXCollections.observableArrayList(analizadorLexico.tokens)
            tableError.items = FXCollections.observableArrayList(analizadorLexico.errores)

        }else{
            var alerta = Alert(Alert.AlertType.WARNING)
            alerta.headerText = "Info"
            alerta.contentText = "No ha ingresado ningún código fuente para analizar"
            alerta.show()
        }
    }

    @FXML
    private fun borrar() {
        txtCodigo.clear();
        tablaPrincipal.items.clear()
        tableError.items.clear()
    }


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        colRow.cellValueFactory = PropertyValueFactory("row")
        colColumn.cellValueFactory = PropertyValueFactory("column")
        colCategory.cellValueFactory = PropertyValueFactory("categoria")
        coltoken.cellValueFactory = PropertyValueFactory("token")
        colRowError.cellValueFactory = PropertyValueFactory("row")
        colColumnError.cellValueFactory = PropertyValueFactory("column")
        colError.cellValueFactory = PropertyValueFactory("message")



    }
}