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
            declaraciones_subprogramas();
            
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

//-------------------------------------------------------------------------------------------------------------------------------
//:::::::::PROCEDURES DAMARIS :::::::::
   private void declaraciones_subprogramas(){
       if(preAnalisis.equals("function")){
        //declaraciones_subprogramas -> declaracion_subprograma declaraciones_subprogramas
        
        declaracion_subprograma();
        declaraciones_subprogramas();
        
       }else{
           //declaraciones_subprogramas -> empty
       }
    }
    
    private void declaracion_subprograma(){
      
        if(preAnalisis.equals("function")){ //|| preAnalisis=="sub"){
            //declaracion_subprograma -> declaracion_funcion declaracion_subrutina
            declaracion_funcion();
        }else if(preAnalisis.equals("sub")){
            declaracion_subrutina();
        }else{
            error("[declaracion_subprograma] Funcion o Subrutina mal declarada o incorrecta " + "Linea: " + cmp.be.preAnalisis.numLinea);
        }
 }

    private void declaracion_funcion(){
        if(preAnalisis.equals("function")){
            //declaracion_funcion -> function id argumentos as tipo proposiciones_optativas
            emparejar("function");
            emparejar("id");
            argumentos();
            emparejar("as");
            tipo();
            proposiciones_optativas();
        }else{
            //Error de produccion
            error("[declaracion_funcion] Se esperaba 'function'" + "Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }
    
    private void declaracion_subrutina(){
    
        if(preAnalisis.equals("sub")){
            //declaracion_subrutina -> sub id argumentos proposiciones_optativas end sub
            emparejar("sub");
            emparejar("id");
            argumentos();
            proposiciones_optativas();
            emparejar("end");
            emparejar("sub");
        }else{
            //ERROR de produccion
            error("[declaracion_subrutina] Se esperaba 'sub' " + "Linea: " + cmp.be.preAnalisis.numLinea);
            
        }
    }
    
    private void argumentos(){
        if(preAnalisis =="("){
            //argumentos -> (lista_declaraciones)
            emparejar("(");
            lista_declaraciones();
            emparejar(")");
        }else{
            //argumentos -> empty       
        }
        
    }
    //-------------------------------------------------------------------------------------------------------------------------------
    //:::::::::PROCEDURES NARCISO (DAVID) :::::::::
    private void proposiciones_optativas(){
        if(preAnalisis.equals("id")){
            proposicion();
            proposiciones_optativas();
        }else{
            //proposiciones_optativas -> empty
        }
    }
    
    private void proposicion(){
        if(preAnalisis.equals("id")){
            //proposicion -> id opasig expresion |
            emparejar("id");
            emparejar("opasig");
            expresion();
        }else if(preAnalisis.equals("call")){
            //proposicion -> call id proposicionB |
            emparejar("call");
            emparejar("id");
            proposicionB();
        }else if(preAnalisis.equals("if")){
            //proposicion -> if condicion then proposiciones_optativas else proposiciones_optativas end if |
            emparejar("if");
            condicion();
            emparejar("then");
            proposiciones_optativas();
            emparejar("else");
            proposiciones_optativas();
            emparejar("end");
            emparejar("if");
        }else if(preAnalisis.equals("do")){
            //proposicion -> do while condicion proposiciones_optativas loop
            emparejar("do");
            emparejar("while");
            condicion();
            proposiciones_optativas();
            emparejar("loop");
        }else{
            //Error de produccion
            error("[proposicion] Se esperaba declaracion de proposicion (id, call, if, do, while)" + "Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }
    
    private void proposicionB(){
        if(preAnalisis.equals("(")){
            emparejar("(");
            lista_expresiones();
            emparejar(")");
        }else{
            //proposicionB -> empty
        }
    }
    
    private void lista_expresiones(){
        if(preAnalisis.equals("id")){
            //lista_expresiones -> expresion lista_expresionesB | empty
            expresion();
            lista_expresionesB();
        }else{
            //lista_expresiones -> empty
        }
    }
    
    private void lista_expresionesB(){
        if(preAnalisis.equals(",")){
            //lista_expresionesB -> , expresion lista_expresionesB
            emparejar(",");
            expresion();
            lista_expresionesB();
        }else{
            //lista_expresionesB -> empty
        }
    }
            
    //-----------------------------------------------------------------------------------------------------------//    

    // PROCEDURES DE ALONDRA (PROCEDURES DE 16-22)
    
    private void condicion(){
        if(preAnalisis.equals("id")){
            //condicion -> expresion oprel expresion
            expresion();
            emparejar("oprel");
            expresion();
        }else{
            //Error de produccion
            error("[condicion] Condicional incorrecta o mal declarada " + "Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }
    
    private void expresion(){
        if(preAnalisis.equals("id")){
            //expresion -> termino expresionB |
            termino();
            expresionB();
        }else if(preAnalisis.equals("literal")){
            //expresion -> literal
            emparejar("literal");
        }else{
            //Error de produccion
            error("[expresion] Expresion no valida " + "Literal: " + cmp.be.preAnalisis.numLinea);
        }
        
        
    }
    
    private void expresionB(){
        if(preAnalisis.equals("opsuma")){
            //expresionB -> opsuma termino expresionB
            emparejar("opsuma");
            termino();
            expresionB();
        }else{
            //expresionB -> empty
        }
    }
    
    private void termino(){
        if(preAnalisis.equals("id")){
            //termino -> factor terminoB
            factor();
            terminoB();
        }else{
            //Error de produccion
            error("[termino] termino incorrecto falta un factor " + "Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }
    
    private void terminoB(){
        if(preAnalisis.equals("opmult")){
            //terminoB -> opmult factor terminoB |
            emparejar("opmult");
            factor();
            terminoB();
        }else{
            //terminoB -> empty
        }
    }
    
    private void factor(){
        if(preAnalisis.equals("id")){
            //factor -> id factorB |
            emparejar("id");
            factorB();
        }else if(preAnalisis.equals("num")){
            //factor -> num |
            emparejar("num");
        }else if(preAnalisis.equals("num.num")){
            //factor -> num.num |
            emparejar("num.num");
        }else if(preAnalisis.equals("(")){
            //factor -> ( expresion )
            emparejar("(");
            expresion();
            emparejar(")");
        }else{
            //Error de produccion
            error("[factor] Factor invalido " + "Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    private void factorB(){
        if(preAnalisis.equals("(")){
            //factorB -> ( lista_expresiones ) |
            emparejar("(");
            lista_expresiones();
            emparejar(")");
        }else{
            //factorB -> empty
        }
    }
//-------------------------------------------------------------------------------------------------------------------------------
    
    //------------------------------------------------------
}
//------------------------------------------------------------------------------
//::
