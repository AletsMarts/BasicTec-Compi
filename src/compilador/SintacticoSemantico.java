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
 *: 17/Feb/2025 AletsMarts          -Se implementaron los procedimientos del PPR del lenguaje
 *:                                 Simple.
 *:-----------------------------------------------------------------------------
 */
package compilador;

import javax.swing.JOptionPane;

public class SintacticoSemantico {

    private Compilador cmp;
    private boolean    analizarSemantica = false;
    private String     preAnalisis;
    
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
        programa();
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

    //-----------------------------------------------------------------------------------------------------
    //PROCEDURES DE ALEJANDRO
    private void programa() {
        if (preAnalisis.equals("dim") || preAnalisis.equals("function")
                || preAnalisis.equals("id") || preAnalisis.equals("end")) {
            declaraciones();
            declaraciones_subprogramas()
            
        } else {
            error("[programa] Se esperaba el inicio de un programa con 'dim', 'function', 'id' o 'end'"
                    + " Línea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    private void declaraciones() {
        if (preAnalisis.equals("dim")) {
            //declaraciones -> dim lista_declaraciones declaraciones empty
            emparejar("dim");
            lista_declaraciones();
            declaraciones();
        } else {
            //declaraciones -> empty
        }
    }

    private void lista_declaraciones() {
        if (preAnalisis.equals("id")) {
            //lista_declaraciones -> id as tipo lista_declaraciones'
            emparejar("id");
            emparejar("as");
            tipo();
            lista_declaraciones_prima();
        } else {
            error("[lista_declaraciones] Se esperaba 'id', 'as', tipo de dato, etc."
                    + " Línea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    private void lista_declaraciones_prima() {
        if (preAnalisis.equals(",")) {
            //lista_declaraciones' -> , lista_declaraciones empty
            emparejar(",");
            lista_declaraciones();
        } else {
            //lista_declaraciones' -> empty
        }
    }

    private void tipo() {
        if (preAnalisis.equals("integer")) {
            //tipo -> integer
            emparejar("integer");
        } else if (preAnalisis.equals("single")) {
            //tipo -> single
            emparejar("single");
        } else if (preAnalisis.equals("string")) {
            //tipo -> string
            emparejar("string");
        } else {
            error("[tipo] Se esperaba un tipo de dato 'integer', 'single', 'string', etc. "
                    + " Línea: " + cmp.be.preAnalisis.numLinea);
        }
    }

//-----------------------------------------------------------------------------------------------------------//    

    // PROCEDURES DE ALONDRA (PROCEDURES DE 16-22)
    
    private void condicion(){
        if(preAnalisis.equals("literal")){
            emparejar("literal");
        }
        else if(preAnalisis.equals("id")){
            emparejar("id");
            factorB();
        }else if(preAnalisis.equals("num")){
            emparejar("id");
        }else if(preAnalisis.equals("num.num")){
            emparejar("num.num");
        }else if(preAnalisis.equals("(")){
            emparejar("(");
            expresion();
            emparejar(")");
        }
    }
    
    private void expresion(){
        if(preAnalisis.equals("literal")){
            emparejar("literal");
        }
        else if(preAnalisis.equals("id")){
            emparejar("id");
            factorB();
        }else if(preAnalisis.equals("num")){
            emparejar("num");
        }else if(preAnalisis.equals("num.num")){
            emparejar("num.num");
        }else if(preAnalisis.equals("(")){
            emparejar("(");
            expresion();
            emparejar(")");
        }
    }
    
    private void expresionB(){
        if(preAnalisis.equals("opsuma")){
            emparejar("opsuma");
            termino();
            expresionB();
        }else{
            //empty
        }
    }
    
    private void termino(){
        if(preAnalisis.equals("id")){
            emparejar("id");
            factorB();
        }else if(preAnalisis.equals("num")){
            emparejar("num");
        }else if(preAnalisis.equals("num.num")){
            emparejar("num.num");
        }else if(preAnalisis.equals("(")){
            emparejar("(");
            expresion();
            emparejar(")");
        }
    }
    
    private void terminoB(){
        if(preAnalisis.equals("opmult")){
            emparejar("opmult");
            factor();
            terminoB();
        }else{
            //empty
        }
    }
    
    private void factor(){
        if(preAnalisis.equals("id")){
            emparejar("id");
            factorB();
        }else if(preAnalisis.equals("num")){
            emparejar("num");
        }else if(preAnalisis.equals("num.num")){
            emparejar("num.num");
        }else if(preAnalisis.equals("(")){
            emparejar("(");
            expresion();
            emparejar(")");
        }
    }

    private void factorB(){
        if(preAnalisis.equals("(")){
            emparejar("(");
            lista_expresiones();
            emparejar(")");
        }else{
            //empty
        }
    }
    
    //------------------------------------------------------
}
//------------------------------------------------------------------------------
//::
