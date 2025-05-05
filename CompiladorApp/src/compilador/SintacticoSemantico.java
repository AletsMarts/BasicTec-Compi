/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: ___________    HORA: ___________ HRS
 *:                                   
 *:               
 *:         Clase con la funcionalidad del Analizador Sintactico
 *                 
 *:                           
 *: Archivo       : SintacticoSemantico.java
 *: Autor         : Fernando Gil  ( Estructura general de la clase  )
 *:                 Grupo de Lenguajes y Automatas II ( Procedures  )
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   : Esta clase implementa un parser descendente del tipo 
 *:                 Predictivo Recursivo. Se forma por un metodo por cada simbolo
 *:                 No-Terminal de la gramatica mas el metodo emparejar ().
 *:                 El analisis empieza invocando al metodo del simbolo inicial.
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 22/Feb/2015 FGil                -Se mejoro errorEmparejar () para mostrar el
 *:                                 numero de linea en el codigo fuente donde 
 *:                                 ocurrio el error.
 *: 08/Sep/2015 FGil                -Se dejo lista para iniciar un nuevo analizador
 *:                                 sintactico.
 *: 20/FEB/2023 F.Gil, Oswi         -Se implementaron los procedures del parser
 *:                                  predictivo recursivo de leng BasicTec.
 *:-----------------------------------------------------------------------------
 */
package compilador;

import general.Linea_BE;
import javax.swing.JOptionPane;
import general.Linea_TS;

public class SintacticoSemantico {

    private Compilador cmp;
    private boolean    analizarSemantica = false;
    private String     preAnalisis;
    public static String VACIO = "vacio";
    public static String ERROR_TIPO = "error_tipo";
    public static String AUX = "aux";
    
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //

    public SintacticoSemantico(Compilador c) {
        cmp = c;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // Metodo que inicia la ejecucion del analisis sintactico predictivo.
    // analizarSemantica : true = realiza el analisis semantico a la par del sintactico
    //                     false= realiza solo el analisis sintactico sin comprobacion semantica

    public void analizar(boolean analizarSemantica) {
        this.analizarSemantica = analizarSemantica;
        preAnalisis = cmp.be.preAnalisis.complex;

        // * * *   INVOCAR AQUI EL PROCEDURE DEL SIMBOLO INICIAL   * * *
        programa(new Atributos ());
    }

    //--------------------------------------------------------------------------

    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            preAnalisis = cmp.be.preAnalisis.complex;            
        } else {
            errorEmparejar( t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
 
    private void errorEmparejar(String _token, String _lexema, int numLinea ) {
        String msjError = "";

        if (_token.equals("id")) {
            msjError += "Se esperaba un identificador";
        } else if (_token.equals("num")) {
            msjError += "Se esperaba una constante entera";
        } else if (_token.equals("num.num")) {
            msjError += "Se esperaba una constante real";
        } else if (_token.equals("literal")) {
            msjError += "Se esperaba una literal";
        } else if (_token.equals("oparit")) {
            msjError += "Se esperaba un operador aritmetico";
        } else if (_token.equals("oprel")) {
            msjError += "Se esperaba un operador relacional";
        } else if (_token.equals("opasig")) {
            msjError += "Se esperaba operador de asignacion";
        } else {
            msjError += "Se esperaba " + _token;
        }
        msjError += " se encontró " + ( _lexema.equals ( "$" )? "fin de archivo" : _lexema ) + 
                    ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico

    private void error(String _descripError) {
        cmp.me.error(cmp.ERR_SINTACTICO, _descripError);
    }

    // Fin de error
    //--------------------------------------------------------------------------
    //  *  *   *   *    PEGAR AQUI EL CODIGO DE LOS PROCEDURES  *  *  *  *
    //--------------------------------------------------------------------------
    //PROCEDURES DE ALEJANDRO
    private void programa(Atributos programa) {
        Linea_BE end = new Linea_BE();
        Atributos declaraciones = new Atributos();
        Atributos declaraciones_subprogramas = new Atributos();
        Atributos proposiciones_optativas = new Atributos();
        
        if (preAnalisis.equals("dim") 
            || preAnalisis.equals("function") 
            || preAnalisis.equals("sub")
            || preAnalisis.equals("id") 
            || preAnalisis.equals("call") 
            || preAnalisis.equals("if") 
            || preAnalisis.equals("do") 
            || preAnalisis.equals("end")){
            
            declaraciones(declaraciones);
            declaraciones_subprogramas(declaraciones_subprogramas);
            proposiciones_optativas(proposiciones_optativas);
            
            //SALVANDO ATRIBUTOS DE 'end'
            end = cmp.be.preAnalisis;
            emparejar("end");
            //SALVANDO ATRIBUTOS DE 'end'
            
            //ACCIÓN SEMÁNTICA 1
            if ((declaraciones.tipo.equals(VACIO))
                    && (declaraciones_subprogramas.tipo.equals(VACIO))
                    && (proposiciones_optativas.tipo.equals(VACIO))) 
            {
                programa.tipo = VACIO;
            } else {
                programa.tipo = ERROR_TIPO;
                cmp.me.error(Compilador.ERR_SEMANTICO,
                        "[Programa] Hay errores de tipos en el programa");
            }
            //FIN ACCIÓN SEMÁNTICA
        } else {
            error("[programa] Se esperaba el inicio de un programa con 'dim', 'function', 'id' o 'end'"
                    + " Línea: " + cmp.be.preAnalisis.numLinea);
        }
    }
    

    //✔
    private void declaraciones(Atributos declaraciones) {
        Linea_BE dim = new Linea_BE();
        Atributos lista_declaraciones = new Atributos();
        Atributos declaraciones1 = new Atributos();
        
        if (preAnalisis.equals("dim")) {
            //declaraciones -> dim lista_declaraciones declaraciones empty
            //SALVANDO ATRIBUTOS DE 'dim'
            dim = cmp.be.preAnalisis;
            emparejar("dim");
            //SALVANDO ATRIBUTOS DE 'dim'
           
            lista_declaraciones(lista_declaraciones);
            declaraciones(declaraciones1);
            
            //ACCIÓN SEMÁNTICA 2
            if ((lista_declaraciones.tipo.equals(VACIO)&&
                    (declaraciones1.tipo.equals(VACIO)))) 
            {
                declaraciones.tipo = VACIO;        
            } else {
                declaraciones.tipo = ERROR_TIPO;
                cmp.me.error(Compilador.ERR_SEMANTICO,
                        "[Declaraciones] La declaración tiene un error de tipo");
            }
            //FIN ACCIÓN SEMÁNTICA
        } else {
            //declaraciones -> empty
            //ACCIÓN SEMÁNTICA 3
            declaraciones.tipo = VACIO;
            //FIN ACCIÓN SEMÁNTICA
        }
    }
    
    //--------------------------------!!!!!!!!!
    //REVISAR ACCION SEMANTICA 4
    //--------------------------------!!!!!!!!!
    
    private void lista_declaraciones(Atributos lista_declaraciones) {
        Linea_BE id = new Linea_BE();
        Linea_BE as = new Linea_BE();
        Atributos tipo = new Atributos();
        Atributos lista_declaraciones_prima = new Atributos();
        
        if (preAnalisis.equals("id")) {
            //lista_declaraciones -> id as tipo lista_declaraciones'
            //SALVANDO ATRIBUTOS DE 'id'
            id = cmp.be.preAnalisis;
            emparejar("id");
            //SALVANDO ATRIBUTOS DE 'id'
            
            //SALVANDO ATRIBUTOS DE 'as'
            as = cmp.be.preAnalisis;
            emparejar("as");
            //SALVANDO ATRIBUTOS DE 'as'
            
            tipo(tipo);
            //ACCIÓN SEMÁNTICA 4
///*--->Falta implementar que el id no esté declarado y agregarlo a la TS. 
//Si está declarado se asigna ERROR_TIPO sino VACIO<---*/
//            lista_declaraciones.aux = ERROR_TIPO;
            //FIN ACCIÓN SEMÁNTICA
            // ACCIÓN SEMÁNTICA 4
            int entrada = cmp.ts.buscar(id.lexema, "global"); // Buscamos por lexema y ámbito (puedes ajustar el ámbito)
            if (entrada > 0) {
                // Ya está declarado
                lista_declaraciones.aux = ERROR_TIPO;
                cmp.me.error(Compilador.ERR_SEMANTICO,
                    "[Lista_declaraciones] Identificador '" + id.lexema + "' ya declarado. Línea: " + id.numLinea);
            } else {
                // No está declarado, insertamos en TS
                Linea_TS nuevaEntrada = new Linea_TS(id.complex, id.lexema, tipo.tipo, "global"); // tipo.tipo contiene el tipo declarado
                cmp.ts.insertar(nuevaEntrada);
                lista_declaraciones.aux = VACIO;
            }
            //FIN ACCIÓN SEMÁNTICA

            lista_declaraciones_prima(lista_declaraciones_prima);
            
            //ACCIÓN SEMÁNTICA 5
            if (lista_declaraciones.aux.equals(VACIO)&&
                    (lista_declaraciones_prima.tipo.equals(VACIO))) 
            {
                lista_declaraciones.tipo = VACIO;
                
            } else {
                lista_declaraciones.tipo = ERROR_TIPO;
                cmp.me.error(Compilador.ERR_SEMANTICO,
                        "[Lista_declaraciones] La lista de declaraciones presenta un error de tipo");
            }
            //FIN ACCIÓN SEMÁNTICA
        } else {
            error("[lista_declaraciones] Se esperaba 'id', 'as', tipo de dato, etc."
                    + " Línea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //✔
    private void lista_declaraciones_prima(Atributos lista_declaraciones_prima) {
        Linea_BE coma = new Linea_BE();
        Atributos lista_declaraciones = new Atributos();
        if (preAnalisis.equals(",")) {
            //lista_declaraciones' -> , lista_declaraciones empty
            //SALVANDO ATRIBUTOS ','
            /*DUDA CON LA DECLARACIÓN Y POSIBLE MISMATCH*/
            coma = cmp.be.preAnalisis;
            emparejar(",");
            //SALVANDO ATRIBUTOS ','
            
            lista_declaraciones(lista_declaraciones);
        } else {
            //lista_declaraciones' -> empty
            lista_declaraciones_prima.tipo = VACIO;
        }
    }

    //✔
    private void tipo(Atributos tipo) {
        Linea_BE integer = new Linea_BE();
        Linea_BE single = new Linea_BE();
        Linea_BE string = new Linea_BE();
        
        if (preAnalisis.equals("integer")) {
            //tipo -> integer
            //SALVANDO ATRIBUTOS DE 'integer'
            integer = cmp.be.preAnalisis;
            emparejar("integer");
            //SALVANDO ATRIBUTOS DE 'integer'
        } else if (preAnalisis.equals("single")) {
            //tipo -> single
            //SALVANDO ATRIBUTOS DE 'single'
            single = cmp.be.preAnalisis;
            emparejar("single");
            //SALVANDO ATRIBUTOS DE 'single'
        } else if (preAnalisis.equals("string")) {
            //tipo -> string
            //SALVANDO ATRIBUTOS DE 'string'
            string = cmp.be.preAnalisis;
            emparejar("string");
            //SALVANDO ATRIBUTOS DE 'string'
        } else {
            error("[tipo] Se esperaba un tipo de dato 'integer', 'single', 'string', etc. "
                    + " Línea: " + cmp.be.preAnalisis.numLinea);
        }
    }

//-------------------------------------------------------------------------------------------------------------------------------
//:::::::::PROCEDURES DAMARIS :::::::::
   private void declaraciones_subprogramas(Atributos declaraciones_subprogramas){
       Atributos declaracion_subprograma = new Atributos();
       Atributos declaraciones_subprogramas1 = new Atributos();
       
       if(preAnalisis.equals("function") || preAnalisis.equals("sub")){
        //declaraciones_subprogramas -> declaracion_subprograma declaraciones_subprogramas
        
        declaracion_subprograma(declaracion_subprograma);
        declaraciones_subprogramas(declaraciones_subprogramas1);

       }else{
           //declaraciones_subprogramas -> empty
           declaraciones_subprogramas.tipo = VACIO;
       }
    }
    
    private void declaracion_subprograma(Atributos declaracion_subprograma){
        Atributos declaracion_funcion = new Atributos();
        Atributos declaracion_subrutina = new Atributos();
      
        if(preAnalisis.equals("function")){ //|| preAnalisis=="sub"){
            //declaracion_subprograma -> declaracion_funcion declaracion_subrutina
            declaracion_funcion(declaracion_funcion);
        }else if(preAnalisis.equals("sub")){
            declaracion_subrutina(declaracion_subrutina);
        }else{
            error("[declaracion_subprograma] Funcion o Subrutina mal declarada o incorrecta " + "Linea: " + cmp.be.preAnalisis.numLinea);
        }

 }

    //✔
    private void declaracion_funcion(Atributos declaracion_funcion){
        Linea_BE function = new Linea_BE();
        Linea_BE id = new Linea_BE();
        Linea_BE as = new Linea_BE();
        Linea_BE end = new Linea_BE();
        Atributos argumentos = new Atributos();
        Atributos tipo = new Atributos();
        Atributos proposiciones_optativas = new Atributos();
        
        if(preAnalisis.equals("function")){
            //declaracion_funcion -> function id argumentos as tipo proposiciones_optativas
            //SALVANDO ATRIBUTOS DE 'function'
            function = cmp.be.preAnalisis;
            emparejar("function");
            //SALVANDO ATRIBUTOS DE 'function'
            
            //SALVANDO ATRIBUTOS DE 'id'
            id = cmp.be.preAnalisis;
            emparejar("id");
            //SALVANDO ATRIBUTOS DE 'id'
            
            argumentos(argumentos);
            //SALVANDO ATRIBUTOS DE 'as'
            as = cmp.be.preAnalisis;
            emparejar("as");
            //SALVANDO ATRIBUTOS DE 'as'
            
            tipo(tipo);
            proposiciones_optativas(proposiciones_optativas);
            
            //SALVANDO ATRIBUTOS DE 'end'
            end = cmp.be.preAnalisis;
            emparejar("end");
            //SALVANDO ATRIBUTOS DE 'end'
            
            //SALVANDO ATRIBUTOS DE 'function'
            /*Duda con repetir emparejar*/
            function = cmp.be.preAnalisis;
            emparejar("function");
            //SALVANDO ATRIBUTOS DE 'function'
        }else{
            //Error de produccion
            error("[declaracion_funcion] Se esperaba 'function'" + "Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }
    
    //✔
    private void declaracion_subrutina(Atributos declaracion_subrutina){
        Linea_BE sub = new Linea_BE();
        Linea_BE id = new Linea_BE();
        Linea_BE end = new Linea_BE();
        Atributos argumentos = new Atributos();
        Atributos proposiciones_optativas = new Atributos();
        
        if(preAnalisis.equals("sub")){
            //declaracion_subrutina -> sub id argumentos proposiciones_optativas end sub
            //SALVANDO ATRIBUTOS DE 'sub'
            sub = cmp.be.preAnalisis;
            emparejar("sub");
            //SALVANDO ATRIBUTOS DE 'sub'
            //SALVANDO ATRIBUTOS DE 'id'
            id = cmp.be.preAnalisis;
            emparejar("id");
            //SALVANDO ATRIBUTOS DE 'id'
            argumentos(argumentos);
            proposiciones_optativas(proposiciones_optativas);
            //SALVANDO ATRIBUTOS DE 'end'
            end = cmp.be.preAnalisis;
            emparejar("end");
            //SALVANDO ATRIBUTOS DE 'end'
            //SALVANDO ATRIBUTOS DE 'sub'
            sub = cmp.be.preAnalisis;
            emparejar("sub");
            //SALVANDO ATRIBUTOS DE 'sub'
        }else{
            //ERROR de produccion
            error("[declaracion_subrutina] Se esperaba 'sub' " + "Linea: " + cmp.be.preAnalisis.numLinea);    
        }
    }
    
    //✔
    private void argumentos(Atributos argumentos){
        Linea_BE pareAbre = new Linea_BE();
        Linea_BE pareCierra = new Linea_BE();
        Atributos lista_declaraciones = new Atributos();
        
        if(preAnalisis.equals("(")){
            //argumentos -> (lista_declaraciones)
            //SALVANDO ATRIBUTOS DE '('
            pareAbre = cmp.be.preAnalisis;
            emparejar("(");
            //SALVANDO ATRIBUTOS DE '('
            
            lista_declaraciones(lista_declaraciones);
            
            //SALVANDO ATRIBUTOS DE ')'
            pareCierra = cmp.be.preAnalisis;
            emparejar(")");
            //SALVANDO ATRIBUTOS DE ')'
        }else{
            //argumentos -> empty 
            argumentos.tipo = VACIO;
        }
        
    }
    //-------------------------------------------------------------------------------------------------------------------------------
    //:::::::::PROCEDURES NARCISO (DAVID) :::::::::
    private void proposiciones_optativas(Atributos proposiciones_optativas){
        Atributos proposicion = new Atributos();
        Atributos proposiciones_optativas1 = new Atributos();
        
        if(preAnalisis.equals("id") || preAnalisis.equals("call") || preAnalisis.equals("if") || preAnalisis.equals("do")){
            proposicion(proposicion);
            proposiciones_optativas(proposiciones_optativas1);
        }else{
            //proposiciones_optativas -> empty
            proposiciones_optativas.tipo = VACIO;
        }

    }
    
    //✔
    private void proposicion(Atributos proposicion){
        Linea_BE id = new Linea_BE();
        Linea_BE opasig = new Linea_BE();
        Linea_BE call = new Linea_BE();
        Linea_BE si = new Linea_BE();
        Linea_BE then = new Linea_BE();
        Linea_BE sino = new Linea_BE();
        Linea_BE haz = new Linea_BE();
        Linea_BE mientras = new Linea_BE();
        Linea_BE loop = new Linea_BE();
        Linea_BE end = new Linea_BE();
        
        Atributos expresion = new Atributos();
        Atributos proposicionB = new Atributos();
        Atributos condicion = new Atributos();
        Atributos proposiciones_optativas = new Atributos();
        
        
        if(preAnalisis.equals("id")){
            //proposicion -> id opasig expresion |
            //SALVANDO ATRIBUTOS DE 'id'
            id = cmp.be.preAnalisis;
            emparejar("id");
            //SALVANDO ATRIBUTOS DE 'id'
            
            //SALVANDO ATRIBUTOS DE 'opasig'
            opasig = cmp.be.preAnalisis;
            emparejar("opasig");
            //SALVANDO ATRIBUTOS DE 'opasig'
            
            expresion(expresion);
        }else if(preAnalisis.equals("call")){
            //proposicion -> call id proposicionB |
            //SALVANDO ATRIBUTOS DE 'call'
            call = cmp.be.preAnalisis;
            emparejar("call");
            //SALVANDO ATRIBUTOS DE 'call'
            
            //SALVANDO ATRIBUTOS DE 'id'
            id = cmp.be.preAnalisis;
            emparejar("id");
            //SALVANDO ATRIBUTOS DE 'id'
            
            proposicionB(proposicionB);
        }else if(preAnalisis.equals("if")){
            //proposicion -> if condicion then proposiciones_optativas else proposiciones_optativas end if |
            //SALVANDO ATRIBUTOS DE 'if'
            si = cmp.be.preAnalisis;
            emparejar("if");
            //SALVANDO ATRIBUTOS DE 'if'
            
            condicion(condicion);
            
            //SALVANDO ATRIBUTOS DE 'then'
            then = cmp.be.preAnalisis;
            emparejar("then");
            //SALVANDO ATRIBUTOS DE 'then'
            
            proposiciones_optativas(proposiciones_optativas);
            
            //SALVANDO ATRIBUTOS DE 'else'
            sino = cmp.be.preAnalisis;
            emparejar("else");
            //SALVANDO ATRIBUTOS DE 'else'
            
            proposiciones_optativas(proposiciones_optativas);
            
            //SALVANDO ATRIBUTOS DE 'end'
            end = cmp.be.preAnalisis;
            emparejar("end");
            //SALVANDO ATRIBUTOS DE 'end'
            
            //SALVANDO ATRIBUTOS DE 'if'
            si = cmp.be.preAnalisis;
            emparejar("if");
            //SALVANDO ATRIBUTOS DE 'if'
        }else if(preAnalisis.equals("do")){
            //proposicion -> do while condicion proposiciones_optativas loop
            //SALVANDO ATRIBUTOS DE 'do'
            haz = cmp.be.preAnalisis;
            emparejar("do");
            //SALVANDO ATRIBUTOS DE 'do'
            
            //SALVANDO ATRIBUTOS DE 'while'
            mientras = cmp.be.preAnalisis;
            emparejar("while");
            //SALVANDO ATRIBUTOS DE 'while'
            
            condicion(condicion);
            proposiciones_optativas(proposiciones_optativas);
            //SALVANDO ATRIBUTOS DE 'loop'
            loop = cmp.be.preAnalisis;
            emparejar("loop");
            //SALVANDO ATRIBUTOS DE 'loop'
        }else{
            //Error de produccion
            error("[proposicion] Se esperaba declaracion de proposicion (id, call, if, do, while)" + "Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }
    
    //✔
    private void proposicionB(Atributos proposicionB){
        Linea_BE pareAbre = new Linea_BE();
        Linea_BE pareCierra = new Linea_BE();
        Atributos lista_expresiones = new Atributos();
        
        if(preAnalisis.equals("(")){
            //SALVANDO ATRIBUTOS DE '('
            pareAbre = cmp.be.preAnalisis;
            emparejar("(");
            //SALVANDO ATRIBUTOS DE '('
            lista_expresiones(lista_expresiones);
            //SALVANDO ATRIBUTOS DE ')'
            pareCierra = cmp.be.preAnalisis;
            emparejar(")");
            //SALVANDO ATRIBUTOS DE ')'
        }else{
            //proposicionB -> empty
            proposicionB.tipo = VACIO;
        }
    }
    
    private void lista_expresiones(Atributos lista_expresiones){
        Atributos expresion = new Atributos();
        Atributos lista_expresionesB = new Atributos();
        if(preAnalisis.equals("literal") || preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num") || preAnalisis.equals("(")){
            //lista_expresiones -> expresion lista_expresionesB | empty
            expresion(expresion);
            lista_expresionesB(lista_expresionesB);
        }else{
            //lista_expresiones -> empty
            lista_expresiones.tipo = VACIO;
        }
    }
    
    //✔
    private void lista_expresionesB(Atributos lista_expresionesB){
        Linea_BE coma = new Linea_BE();
        Atributos expresion = new Atributos();
        Atributos lista_expresionesB1 = new Atributos();
        
        if(preAnalisis.equals(",")){
            //lista_expresionesB -> , expresion lista_expresionesB
            //SALVANDO ATRIBUTOS DE ','
            coma = cmp.be.preAnalisis;
            emparejar(",");
            //SALVANDO ATRIBUTOS DE ','
            
            expresion(expresion);
            lista_expresionesB(lista_expresionesB1);
        }else{
            //lista_expresionesB -> empty
            lista_expresionesB.tipo = VACIO;
        }
    }
            
    //-----------------------------------------------------------------------------------------------------------//    

    // PROCEDURES DE ALONDRA (PROCEDURES DE 16-22)
    
    private void condicion(Atributos condicion){
        Linea_BE oprel = new Linea_BE();
        Atributos expresion = new Atributos();
        
        if(preAnalisis.equals("literal") || preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num") || preAnalisis.equals("(")){
            //condicion -> expresion oprel expresion
            expresion(expresion);
            //SALVANDO ATRIBUTOS DE 'oprel'
            oprel = cmp.be.preAnalisis;
            emparejar("oprel");
            //SALVANDO ATRIBUTOS DE 'oprel'
            expresion(expresion);
            
            if(analizarSemantica){
                //INICIO ACCION SEMANTICA
                //cmp.ts.anadeTipo(oprel.entrada, expresion.tipo);
                if (expresion.tipo.equals(VACIO) && expresion.tipo.equals(VACIO)){
                    condicion.tipo = VACIO;
                }else{
                    condicion.tipo = ERROR_TIPO;
                }
                //FIN SEMANTICA
            }
            
        }else{
            //Error de produccion
            error("[condicion] Condicional incorrecta o mal declarada " + "Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }
    
    private void expresion(Atributos expresion){
        Linea_BE literal = new Linea_BE();
        Atributos termino = new Atributos();
        Atributos expresionB = new Atributos();
        
        if(preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num") || preAnalisis.equals("(")){
            //expresion -> termino expresionB |
            termino(termino);
            expresionB(expresionB);
            
            if(analizarSemantica){
                //INICIO ACCION SEMANTICA
                if(termino.tipo.equals(VACIO) && expresionB.tipo.equals(VACIO)){
                    expresion.tipo = VACIO;
                }else{
                    expresion.tipo = ERROR_TIPO;
                }
                //FIN ACCION SEMANTICA
            }
        }else if(preAnalisis.equals("literal")){
            //expresion -> literal
            //SALVANDO ATRIBUTOS DE 'literal'
            literal = cmp.be.preAnalisis;
            emparejar("literal");
            //SALVANDO ATRIBUTOS DE 'literal'
            
            if(analizarSemantica){
                //INICIO ACCION SEMANTICA
            
                //FIN ACCION SEMANTICA
            }

        }else{
            //Error de produccion
            error("[expresion] Expresion no valida " + "Literal: " + cmp.be.preAnalisis.numLinea);
        }
        
    }
    
    //✔
    private void expresionB(Atributos expresionB){
        Linea_BE opsuma = new Linea_BE();
        Atributos termino = new Atributos();
        Atributos expresionB1 = new Atributos();
        
        if(preAnalisis.equals("opsuma")){
            //expresionB -> opsuma termino expresionB
            //SALVANDO ATRIBUTOS DE 'opsuma'
            opsuma = cmp.be.preAnalisis;
            emparejar("opsuma");
            //SALVANDO ATRIBUTOS DE 'opsuma'
            
            termino(termino);
            expresionB(expresionB1);
            
            if(analizarSemantica){
                //ACCION SEMANTICA
                if(termino.tipo.equals(VACIO) && expresionB1.tipo.equals(VACIO)){
                    expresionB.tipo = VACIO;
                }else{
                    expresionB.tipo = ERROR_TIPO;
                }
            }
        }else{
            //expresionB -> empty
            //ACCION SEMANTICA
            if(analizarSemantica){
                expresionB.tipo = VACIO;
                //FIN ACCION SEMANTICA
            }
        }
    }
    
    private void termino(Atributos termino){
        Atributos factor = new Atributos();
        Atributos terminoB = new Atributos();
        
        if(preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num") || preAnalisis.equals("(")){
            //termino -> factor terminoB
            factor(factor);
            terminoB(terminoB);
            if(analizarSemantica){
                //ACCION SEMANTICA
                if(factor.tipo.equals(VACIO) && terminoB.tipo.equals(VACIO)){
                    termino.tipo = VACIO;
                }else{
                    termino.tipo = ERROR_TIPO;
                }
            }
            
        }else{
            //Error de produccion
            error("[termino] termino incorrecto falta un factor " + "Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }
    
    //✔
    private void terminoB(Atributos terminoB){
        Linea_BE opmult = new Linea_BE();
        Atributos factor = new Atributos();
        Atributos terminoB1 = new Atributos();
        
        if(preAnalisis.equals("opmult")){
            //terminoB -> opmult factor terminoB |
            //SALVANDO ATRIBUTOS DE 'opmult'
            opmult = cmp.be.preAnalisis;
            emparejar("opmult");
            //SALVANDO ATRIBUTOS DE 'opmult'
            factor(factor);
            terminoB(terminoB1);
            
            if(analizarSemantica){
                //ACCION SEMANTICA
                if(factor.tipo.equals(VACIO) && terminoB1.equals(VACIO)){
                    terminoB.tipo = VACIO;
                }else{
                    terminoB.tipo = ERROR_TIPO;
                }
                //FIN ACCION SEMANTICA
            }
      
        }else{
            //terminoB -> empty    
            if(analizarSemantica){
                //ACCION SEMANTICA
                terminoB.tipo = VACIO;
                //FIN ACCION SEMANTICA
            }
        }
    }
    
    //✔
    private void factor(Atributos factor){
        Linea_BE id = new Linea_BE();
        Linea_BE num = new Linea_BE();
        Linea_BE num_num = new Linea_BE();
        Linea_BE pareAbre = new Linea_BE();
        Linea_BE pareCierra = new Linea_BE();
        Atributos expresion = new Atributos();
        Atributos factorB = new Atributos();
        
        if(preAnalisis.equals("id")){
            //factor -> id factorB |
            //SALVANDO ATRIBUTOS DE 'id'
            id = cmp.be.preAnalisis;
            emparejar("id");
            //SALVANDO ATRIBUTOS DE 'id'
            factorB(factorB);
        }else if(preAnalisis.equals("num")){
            //factor -> num |
            //SALVANDO ATRIBUTOS DE 'num'
            num = cmp.be.preAnalisis;
            emparejar("num");
            //SALVANDO ATRIBUTOS DE 'num'
        }else if(preAnalisis.equals("num.num")){
            //factor -> num.num |
            //SALVANDO ATRIBUTOS DE 'num.num'
            num_num = cmp.be.preAnalisis;
            emparejar("num.num");
            //SALVANDO ATRIBUTOS DE 'num.num'
        }else if(preAnalisis.equals("(")){
            //factor -> ( expresion )
            //SALVANDO ATRIBUTOS DE '('
            pareAbre = cmp.be.preAnalisis;
            emparejar("(");
            //SALVANDO ATRIBUTOS DE '('
            expresion(expresion);
            //SALVANDO ATRIBUTOS DE ')'
            pareCierra = cmp.be.preAnalisis;
            emparejar(")");
            //SALVANDO ATRIBUTOS DE ')'
            
            //ACCION SEMANTICA
            if(analizarSemantica){
                
            }
            //FIN ACCION SEMANTICA
            
            
        }else{
            //Error de produccion
            error("[factor] Factor invalido " + "Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //✔
    private void factorB(Atributos factorB){
        Linea_BE pareAbre = new Linea_BE();
        Linea_BE pareCierra = new Linea_BE();
        Atributos lista_expresiones = new Atributos();
        
        if(preAnalisis.equals("(")){
            //factorB -> ( lista_expresiones ) |
            //SALVANDO ATRIBUTOS DE '('
            pareAbre = cmp.be.preAnalisis;
            emparejar("(");
            //SALVANDO ATRIBUTOS DE '('
            
            lista_expresiones(lista_expresiones);
            
            //SALVANDO ATRIBUTOS DE ')'
            pareCierra = cmp.be.preAnalisis;
            emparejar(")");
            //SALVANDO ATRIBUTOS DE ')'
            
            //ACCION SEMANTICA
            if(analizarSemantica){
                
            }
            //FIN ACCION SEMANTICA
            
            
        }else{
            //factorB -> empty
            factorB.tipo = VACIO;
        }
    }
    
    
}
//------------------------------------------------------------------------------
//::
