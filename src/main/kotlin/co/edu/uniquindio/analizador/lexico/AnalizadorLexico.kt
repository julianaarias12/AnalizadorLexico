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
            if(isPalabraReservadaOIdentificador()) continue
            if(isDecimal()) continue
            if(isReales('$', Categoria.REAL)) continue
            if(isOperadoresAritmeticos()) continue
            if(isAsignacion()) continue
            if(isIncrementoODecremento('+')) continue
            if(isIncrementoODecremento('-')) continue
            if(isOperadoresRelacionales()) continue
            if(isOperadoresLogicos()) continue
            if(isString()) continue
            if(isHexadecimal('¡')) continue
            if(isComentarioLinea('\\')) continue
            if(isComentarioBloque()) continue
            if(isOtroCaracter(',', Categoria.SEPARADOR)) continue
            if(isOtroCaracter('{', Categoria.LLAVE_IZQUIERDA)) continue
            if(isOtroCaracter('}', Categoria.LLAVE_DERECHA)) continue
            if(isOtroCaracter('(', Categoria.PARENTESIS_IZQUIERDO)) continue
            if(isOtroCaracter(')', Categoria.PARENTESIS_DERECHO)) continue
            if(isOtroCaracter(';', Categoria.FIN_SENTENCIA)) continue
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
     * Función encargada de verificar si un token es una palabra reservada o un identificador
     * @return true si el token es es una palabra reservada o un identificador; de lo contrario, false
     */
    fun isPalabraReservadaOIdentificador() : Boolean{
        if(caracterActual.isLetter() ||  isUnderscore(caracterActual)){
            var token = ""
            token = concatcaracterActual(token)
            setposicionBacktracking(filaActual,columnaActual,posicionActual)
            siguienteCaracter()
            var contador = 0;
            while((caracterActual.isLetter() || caracterActual.isDigit() || isUnderscore(caracterActual)) && contador <9){
                token = concatcaracterActual(token)
                siguienteCaracter()
                contador++
            }
            if(PalabrasReservadas.values().map { it.name }.contains(token)){
                agregarToken(token, Categoria.PALABRA_RESERVADA)
                return true
            }else{
                if ((caracterActual.isLetter() || caracterActual.isDigit() || isUnderscore(caracterActual))){
                    errores.add(ErrorLexico("la longitud maxima son 10 caracteres",filaActual,columnaActual))
                    caracterActual = finCodigoFuente
                    posicionActual = sourceCode.length -1
                    return  false
                }
                return agregarToken(token,Categoria.IDENTIFICADOR)
            }
        }
        return false
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
    fun isReales(operador:Char, categoria:Categoria) : Boolean{
        // $-9, $9
        if(caracterActual==operador) {
            var token = ""

            siguienteCaracter()
            setposicionBacktracking(filaActual, columnaActual, posicionActual)


            if (caracterActual.isDigit() || caracterActual.equals('-')) {
                token = concatcaracterActual(token)
                setposicionBacktracking(filaActual, columnaActual, posicionActual)
                siguienteCaracter()
                while (caracterActual.isDigit() || isPoint(caracterActual)) {
                    token = concatcaracterActual(token)
                    if (isPoint(caracterActual)) {
                        return backtracking()
                    }
                    siguienteCaracter()
                }
                return agregarToken(token, Categoria.REAL)
            }
        }
        return false
    }

    /**
     * Función encargada de verificar si un token es un hexadecimal
     * @return true si el token es es una cadena; de lo contrario, false
     */
    fun isHexadecimal(operador:Char ) : Boolean{
        // $-9, $9
        if(caracterActual==operador) {
            var token = ""
            siguienteCaracter()
            setposicionBacktracking(filaActual, columnaActual, posicionActual)


            if (caracterActual.isDigit() || hexadecimales.contains(caracterActual)) {
                token = concatcaracterActual(token)
                setposicionBacktracking(filaActual, columnaActual, posicionActual)
                siguienteCaracter()
                while (caracterActual.isDigit() || hexadecimales.contains(caracterActual)) {
                    token = concatcaracterActual(token)

                    siguienteCaracter()
                }
                return agregarToken(token, Categoria.HEXADECIMAL)
            }
        }
        return false
    }

    /**
     * Función encargada de verificar si un token es asignación
     * @return true si el token es asignación; de lo contrario, false
     */
    fun isAsignacion() : Boolean{
        if(mutableListOf('=','+','-','*','/').contains(caracterActual)){
            var token = ""
            val previousCharacter = caracterActual
            setposicionBacktracking(filaActual,columnaActual,posicionActual)
            token = concatcaracterActual(token)
            siguienteCaracter()
            return if(isEquals(previousCharacter) && isEquals(caracterActual)){
                // Flujo =
                token = concatcaracterActual(token)
                siguienteCaracter()
                if(isEquals(caracterActual)){
                    backtracking()
                }else{
                    agregarToken(token, Categoria.ASIGNACION)
                }
            }else if(isEquals(caracterActual)){
                // Flujo +, -, *, /
                token = concatcaracterActual(token)
                siguienteCaracter()
                agregarToken(token, Categoria.ASIGNACION)
            }else{
                backtracking()
            }
        }
        return false
    }

    /**
     * Función encargada de verificar si un token es incremento o decremento
     * @param operator operador a validar
     * @return true si el token es incremento o decremento; de lo contrario, false
     */
    fun isIncrementoODecremento(operator : Char): Boolean {
        if(caracterActual==operator){
            var token = ""
            setposicionBacktracking(filaActual, columnaActual, posicionActual)
            token = concatcaracterActual(token)
            siguienteCaracter()
            if(caracterActual==operator){
                token = concatcaracterActual(token)
                val categoria = if (isPlus(caracterActual)) Categoria.INCREMENTO else Categoria.DECREMENTO
                siguienteCaracter()
                return agregarToken(token, categoria)
            }
        }
        return false
    }

    /**
     * Función encargada de verificar si un token es un operador relacional
     * @return true si el token es es un operador relacional; de lo contrario, false
     */
    fun isOperadoresRelacionales(): Boolean{
        if(mutableListOf('!','=','<','>').contains(caracterActual)) {
            var token = ""
            setposicionBacktracking(filaActual, columnaActual, posicionActual)
            token = concatcaracterActual(token)
            if(caracterActual == '!'){
                siguienteCaracter()
                if (isEquals(caracterActual)) {
                    token = concatcaracterActual(token)
                    return agregarSiguiente(token, Categoria.OPERADOR_RELACIONAL)
                }
                return backtracking()
            }
            else if (caracterActual == '<' || caracterActual == '>') {
                siguienteCaracter()
                if (isEquals(caracterActual)) {
                    token = concatcaracterActual(token)
                    return agregarSiguiente(token, Categoria.OPERADOR_RELACIONAL)
                }
                return agregarToken(token, Categoria.OPERADOR_RELACIONAL)
            } else {
                siguienteCaracter()
                if (isEquals(caracterActual)) {
                    token = concatcaracterActual(token)
                    siguienteCaracter()
                    if (isEquals(caracterActual)) {
                        token = concatcaracterActual(token)
                        return agregarSiguiente(token, Categoria.OPERADOR_RELACIONAL)
                    } else {
                        return agregarToken(token, Categoria.OPERADOR_RELACIONAL)
                    }
                } else {
                    return backtracking()
                }
            }
        }
        return false
    }

    /**
     * Función encargada de verificar si un token es un comentario de linea
     * @return true si el token es es una comentario de liena; de lo contrario, false
     */
    fun isComentarioLinea(operator : Char): Boolean {
        if(caracterActual==operator){
            var token = ""
            setposicionBacktracking(filaActual, columnaActual, posicionActual)
            token = concatcaracterActual(token)
            siguienteCaracter()
            if(caracterActual==operator){
                token = concatcaracterActual(token)
                siguienteCaracter()
                while(!caracteresEspeciales.contains(caracterActual) && caracterActual != finCodigoFuente ){
                    token = concatcaracterActual(token)
                    siguienteCaracter()
                }
                return agregarToken(token, Categoria.COMENTARIO_LINEA)
            }
        }
        return false
    }


    /**
     * Función encargada de verificar si un token es un comentario en bloque
     * @return true si el token es es una cadena; de lo contrario, false
     */
    fun isComentarioBloque() : Boolean{
        if(caracterActual == '#'){
            var token = ""
            setposicionBacktracking(filaActual,columnaActual,posicionActual)
            token = concatcaracterActual(token)
            siguienteCaracter()
            while(caracterActual != '#'){
                token = concatcaracterActual(token)
                siguienteCaracter()
                if(caracterActual == finCodigoFuente){
                    errores.add(ErrorLexico("No se cerro el comentario en bloque",filaActual,columnaActual))
                    backtracking()
                    siguienteCaracter()
                    return true
                }
            }
            token = concatcaracterActual(token)
            return agregarSiguiente(token,Categoria.COMENTARIO_BLOQUE)
        }
        return false
    }

    /**
     *
     * Funcion que permite saber si una expresion es un operador logico
     */
    fun isOperadoresLogicos(): Boolean {
        if(mutableListOf('!','&','|').contains(caracterActual)) {
            var token = ""
            setposicionBacktracking(filaActual, columnaActual, posicionActual)
            token = concatcaracterActual(token)
            if(caracterActual == '!'){
                siguienteCaracter()
                if(caracterActual=='='){
                    return  backtracking()
                }
                return agregarToken(token, Categoria.OPERADOR_LOGICO)
            }else if(caracterActual == '&'){
                siguienteCaracter()
                if(caracterActual == '&'){
                    token = concatcaracterActual(token)
                    return agregarSiguiente(token, Categoria.OPERADOR_LOGICO)
                }
                return backtracking()
            }else if(caracterActual == '|'){
                siguienteCaracter()
                if(caracterActual == '|'){
                    token = concatcaracterActual(token)
                    return agregarSiguiente(token, Categoria.OPERADOR_LOGICO)
                }
                return backtracking()
            }

        }
        return false
    }

    /**
     * Función encargada de verificar si un token es una cadena de caracteres
     * @return true si el token es es una cadena; de lo contrario, false
     */
    fun isString() : Boolean{
        if(caracterActual == '\"'){
            var token = ""
            setposicionBacktracking(filaActual,columnaActual,posicionActual)
            token = concatcaracterActual(token)
            siguienteCaracter()
            while(caracterActual != '\"'){
                token = concatcaracterActual(token)
                siguienteCaracter()
                if(caracterActual == finCodigoFuente){
                    errores.add(ErrorLexico("No se cerro la cadena",filaActual,columnaActual))
                    backtracking()
                    siguienteCaracter()
                    return true
                }
            }
            token = concatcaracterActual(token)
            return agregarSiguiente(token,Categoria.CADENA_CARACTERES)
        }
        return false
    }

    /**
     * Función encargada de verificar si un token es un operador aritmético
     * @return true si el token es es un operador aritmético; de lo contrario, false
     */
    fun isOperadoresAritmeticos(): Boolean{
        when(caracterActual){
            '+' -> return agregarOperadorAritmetico(mutableListOf('+','='))
            '-' -> return agregarOperadorAritmetico(mutableListOf('-','='))
            '/' -> return agregarOperadorAritmetico(mutableListOf('-','=','*'))
            '*','%' -> return agregarOperadorAritmetico(mutableListOf('='))
            else -> return false
        }
    }

    /**
     * Función encargada de agregar un token de un operador aritmético
     * @return true si se agrega el operador aritmético; de lo contrario, false
     */
    fun agregarOperadorAritmetico(characters: MutableList<Char>): Boolean{
        var token =  ""
        token = concatcaracterActual(token)
        setposicionBacktracking(filaActual,columnaActual,posicionActual)
        siguienteCaracter()
        return if(characters.contains(caracterActual)){
            backtracking()
        }else{
            agregarToken(token,Categoria.OPERADOR_ARITMETICO)
        }
    }

    /**
     * Función encargada de verificar si un token es de un solo carácter
     * @param character caracter a validar
     * @param categoria categoría del token
     * @return true si el token es de un solo carácter; de lo contrario, false
     */
    fun isOtroCaracter (character:Char, categoria:Categoria): Boolean{
        if(caracterActual==character){
            var token = ""
            setposicionBacktracking(filaActual,columnaActual,posicionActual)
            token = concatcaracterActual(token)
            return agregarSiguiente(token,categoria)
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


}