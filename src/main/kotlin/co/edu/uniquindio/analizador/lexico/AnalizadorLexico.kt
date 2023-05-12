package co.edu.uniquindio.analizador.lexico

import co.edu.uniquindio.analizador.lexico.Categoria
import co.edu.uniquindio.analizador.lexico.ErrorLexico
import co.edu.uniquindio.analizador.lexico.Token

class AnalizadorLexico (var sourceCode : String ){

    /**
     * Atributo que representa las posiciones para hacer backtracking
     *
     * 0 - row
     * 1 - column
     * 2 - position
     */
    var posicionBacktracking = mutableListOf(0,0,0)

    /**
     * Atributo que representa algunos carácteres especiales
     */
    var caracteresEspeciales = listOf(' ','\n', '\t')

    /**
     * Atributo que representa HEXADECIMALES
     */
    var hexadecimales = listOf('A','B','C','D','E','F')

    /**
     * Atributo que representa la posición actual dentro del código fuente
     */
    var posicionActual = 0

    /**
     * Atributo que representa la posición actual dentro del código fuente
     */
    var caracterActual = sourceCode[0]

    /**
     * Atributo que representa la lista de tokens
     */
    var tokens = ArrayList<Token>()

    /**
     * Atributo que representa los errores lexicos
     */
    var errores = ArrayList<ErrorLexico>()

    /**
     * Atributo que representa la fila actual
     */
    var filaActual = 0

    /**
     * Atributo que representa la columna actual
     */
    var columnaActual = 0

    /**
     * Atributo que representa el fin del código fuente
     */
    var finCodigoFuente = 0.toChar()

    /**
     * Función encargada de agregar un token a la lista de tokens, CUANDO SE ENCUENTRA LA PALABRA QUE SE VERIFICA QUE EXISTE EN EL AUTOMATA
     * @param token
     * @param categoria categoría del token
     * @return true
     */
    fun agregarToken(token : String, categoria : Categoria) : Boolean{
        tokens.add(Token(token, categoria, posicionBacktracking[0], posicionBacktracking[1]))
        return true
    }

    /**
     * Función encargada de agregar un token a la lista de tokens y de obtener el siguiente token
     * @param token
     * @param categoria categoría del token
     * @return true
     */
    fun agregarSiguiente(token : String, categoria : Categoria) : Boolean{
        tokens.add(Token(token, categoria, filaActual, columnaActual))
        siguienteCaracter()
        return true
    }

    /**
     * Función encargada de aumentar la posición actual de la fila y la columna actual
     * para obtener el siguiente carácter
     */
    fun siguienteCaracter(){
        if(posicionActual == sourceCode.length -1){
            caracterActual = finCodigoFuente
        }else{
            if(caracterActual == caracteresEspeciales[1]){
                filaActual++
                columnaActual = 0
            }else{
                columnaActual++
            }
            posicionActual++
            caracterActual = sourceCode[posicionActual]
        }
    }

    fun borrar(){

    }
    /**
     * Función encargada de analizar el código fuente
     */
    fun analyze(){
        while(caracterActual != finCodigoFuente) {
            if (caracteresEspeciales.contains(caracterActual)) {
                siguienteCaracter()
                continue
            }
            if(isEntero()) continue
//            if(isReservedWordsOrIdentifier()) continue
            if(isDecimal()) continue
//            if(isReales('$', Categoria.REAL)) continue
//            if(isOperadoresAritmeticos()) continue
//            if(isAsignacion()) continue
//            if(isIncrementoODecremento('+')) continue
//            if(isIncrementoODecremento('-')) continue
//            if(isOperadoresRelacionales()) continue
//            if(isOperadoresLogicos()) continue
//            if(isString()) continue
//            if(isHexadecimal('¡')) continue
//            if(isComentarioLinea('\\')) continue
//            if(isComentarioBloque()) continue
//            if(isOtroCaracter(',', Categoria.SEPARADOR)) continue
//            if(isOtroCaracter('{', Categoria.LLAVE_IZQUIERDA)) continue
//            if(isOtroCaracter('}', Categoria.LLAVE_DERECHA)) continue
//            if(isOtroCaracter('(', Categoria.PARENTESIS_IZQUIERDO)) continue
//            if(isOtroCaracter(')', Categoria.PARENTESIS_DERECHO)) continue
//            if(isOtroCaracter(';', Categoria.FIN_SENTENCIA)) continue
            errores.add(ErrorLexico("No es valido",filaActual,columnaActual))
            siguienteCaracter()
        }
    }
    /**
     * Función encargada de verificar si un token es entero
     * @return true si el token es entero; de lo contrario, false
     */
    fun isEntero() : Boolean{
        if(caracterActual.isDigit()){
            var token = ""
            token = concatcaracterActual(token)
            setposicionBacktracking(filaActual,columnaActual,posicionActual)
            siguienteCaracter()
            while (caracterActual.isDigit() || isPoint(caracterActual)){
                token = concatcaracterActual(token)
                if(isPoint(caracterActual)){
                    return backtracking()
                }
                siguienteCaracter()
            }
            return agregarToken(token, Categoria.ENTERO)
        }
        return false
    }

    /**
     * Función encargada de almacenar las posiciones para hacer backtracking
     * @param row fila actual
     * @param column columna actual
     * @param position posición actual
     */
    fun setposicionBacktracking(row: Int, column: Int, position : Int){
        posicionBacktracking[0] = row
        posicionBacktracking[1] = column
        posicionBacktracking[2] = position
    }

    /**
     * Función encargada de hacer backtracking
     * @return false
     */
    fun backtracking () : Boolean{
        filaActual = posicionBacktracking[0]
        columnaActual = posicionBacktracking[1]
        posicionActual = posicionBacktracking[2]
        caracterActual = sourceCode[posicionBacktracking[2]]
        return false
    }
    /**
     * Función encargada de validar si un carácter es un guión bajo
     * @param character carácter a validar
     * @return true si el carácter es un guión bajo; de lo contrario, false
     */
    fun isUnderscore(character: Char) : Boolean{
        return character == '_'
    }

    /**
     * Función encargada de concatenar el carácter actual
     * @param token a concatenar
     * @return tokeb concatenado con el carácter actual
     */
    fun concatcaracterActual(token: String) : String {
        return token + caracterActual
    }


    /**
     * Función encargada de validar si un carácter es una comilla simple
     * @param character carácter a validar
     * @return true si el carácter es una comilla simple; de lo contrario, false
     */
    fun isSingleQuote(character: Char) : Boolean{
        return character == '\''
    }

    /**
     * Función encargada de validar si un carácter es una suma
     * @param character carácter a validar
     * @return true si el carácter es una suma; de lo contrario, false
     */
    fun isPlus(character: Char) : Boolean{
        return character == '+'
    }

    /**
     * Función encargada de validar si un carácter es un igual
     * @param character carácter a validar
     * @return true si el carácter es un igual; de lo contrario, false
     */
    fun isEquals(character: Char) : Boolean{
        return character == '='
    }

    /**
     * Función encargada de validar si un carácter es un punto
     * @param character carácter a validar
     * @return true si el carácter es un punto; de lo contrario, false
     */
    fun isPoint(character: Char) : Boolean{
        return character == '.'
    }

    /**
     * Función encargada de verificar si un token es decimal
     * @return true si el token es entero; de lo contrario, false
     */
    fun isDecimal() : Boolean{
        if(caracterActual.isDigit() || isPoint(caracterActual)){
            var token = ""
            setposicionBacktracking(filaActual,columnaActual,posicionActual)
            if(isPoint(caracterActual)){
                token = concatcaracterActual(token)
                siguienteCaracter()
                if(caracterActual.isDigit()){
                    token = concatcaracterActual(token)
                    siguienteCaracter()
                }else{
                    return backtracking()
                }
            }else{
                token = concatcaracterActual(token)
                siguienteCaracter()
                while(caracterActual.isDigit()){
                    token = concatcaracterActual(token)
                    siguienteCaracter()
                }
                if(isPoint(caracterActual)){
                    token = concatcaracterActual(token)
                    setposicionBacktracking(filaActual,columnaActual,posicionActual)
                    siguienteCaracter()
                    if(caracterActual.isDigit()){
                        token = concatcaracterActual(token)
                        siguienteCaracter()
                    }else{
                        return agregarToken(token.substring(0,token.length-2),Categoria.ENTERO)
                    }
                }
            }
            while(caracterActual.isDigit()){
                token = concatcaracterActual(token)
                siguienteCaracter()
            }
            return agregarToken(token, Categoria.DECIMAL)
        }
        return false
    }
}