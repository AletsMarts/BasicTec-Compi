/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package prueba_sintactico;

import compilador.Compilador;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author FGIL.0
 */
public class SintacticoErrTest {
    Compilador cmp = new Compilador ();
    
    public SintacticoErrTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    //--------------------------------------------------------------------------
    
    @Test
    public void emparejarTest () {
        ArrayList<String> programas = new ArrayList<> ();
        
        // #1 - Programa con error: el programa minimo valido debe llevar el "end"
        programas.add ( """
        """ );

        // #2 - Programa con error: falta la palabra reservada "as"
        programas.add ( """
              dim  a  single
            end
        """ );
        
        // #3 - Programa con error: se esperaba "as" se encontró ","
        programas.add ( """
              dim a, b as single
            end
        """ );        
        
        // #4 - Programa con error: se esperaba un tipo de dato se encontró "texto"
        programas.add ( """
              dim  a as single,  b as single
              dim  c as integer
              dim  s as texto, z as integer
            end
        """ );

        // #5 - Programa con error:  se esperaba un id se encontró "dim"
        programas.add ( """
              function dim  as string
              end function
            end
        """ );
        
        // #6 - Programa con error: se esperaba "function" se encontró "funcion".
        programas.add ( """
              function calcular  as string
              end funcion
             end
        """ );       
        
        // #7 - Programa con error: hay 2 comas en los argumentos de la funcion.
        programas.add ( """
              function leyendaRespuesta (  c1 as integer, , c2 as single ) as string
              end function
            end
        """ );        
        
        // #8 - Programa con error: le falta el tipo de dato de retorno despues de "as".
        programas.add ( """
              function leyendaRespuesta (  c1 as integer, c2 as single ) as 
              end function
            end
        """ );
        
        // #9 - Programa con error: se esperaba ")" se encontró "as" en los args del 3er function.
        programas.add ( """
              function calcular  as string
              end function
              
              function ecuacion ( c0 as single ) as single
              end function
              
              function leyendaRespuesta (  c1 as integer, c2 as single  as string
              end function
            
            end
        """ );

        // #10 - Programa con error:  las sub no retornan valor, error en "as".
        programas.add ( """
              sub retardo as single
              end sub
            end
        """ );
        
        // #11 - Programa con error:  en el argumento "z" se esperaba tipo de dato. 
        programas.add ( """
              sub retardo
              end sub
              
              sub imprimir (  x as integer, y as single, z as  ) 
              end sub
            end
        """ );
  
        // #12 - Programa con error: falta la expresión a asignar
        programas.add ( """
                a =
            end
        """ );
        
        // #13 - Programa con error: hace falta un ")"
        programas.add ( """
                d = ( 2 * c + d * a + 1
            end
        """ );
 
        // #14 - Programa con error: se tienen 3 id's como destino de la asignacion.
        programas.add ( """
                x y z = 882
            end
        """ );
 
        // #15 - Programa con error: en la asignacion a "d" la expresion esta incompleta
        programas.add ( """
                a = 882
                b = 3.12123
                c = 0
                d = ( 2 * c + d ) * a + 1 *
            	s = "Hola mis inges"
            end
        """ );
        
        // #16 - Programa con error: se encontró ")", sobra la coma o faltó un argumento.
        programas.add ( """
                a2 =  promedio ( a,  )
            end
        """ );
        
        // #17 - Programa con error: en la expresión del 3er argumento
        programas.add ( """
                a  =  1 + promedio ( a, b, 3 * ( a  b ) ) * x
            end
        """ );
        
        // #18 - Programa con error: se esperaba expresion se encontró "then"
        programas.add ( """
                if  a <= then  
                else 
                end if
            end
        """ );       

        // #19 - Programa con error: falta el "else"
        programas.add ( """
                if  a <= 10   then
                    a = 1 
            	    b = 1.2
                end if
            end
        """ ); 
        
        // #20 - Programa con error: faltó el "loop" de cierre del primer do while
        programas.add ( """
                do while  a <>  10 
                    do while  a + 1 * y == 2 * ( b + 3 + 1 )  
                    loop
                
            end
        """ ); 
        
        // #21 - Programa con error: se encontró "while" le faltó el "do".
        programas.add ( """
                do while  a <>  10 
                    while  a + 1 * y == 2 * ( b + 3 + 1 )  
                    loop
                loop
            end
        """ );         

        // #22 - Programa con error: falta expresion en la asignacion a "i"
        programas.add ( """
              function calcular   as string
                i = 0
            	do while i < 10
            	  if i == 1 then
            	     i = i + 5
            	  else
            	     i = 
            	  end if
            	loop
            	i = 10
              end function
            end	   
        """ ); 

        // #23 - Programa con error:  falta expresion en la asignacion a "i"
        programas.add ( """
              sub retardo
              end sub
              
              sub imprimir (  x as integer, y as single, z as  ) 
                i = 0
                do while i < 10
                    if i == 1 then
                        i = i + 5
                    else
                        i = 
                    end if
                loop
                i = 10                        
              end sub
            end
        """ );        
        
        int i = 0;
        // Por cada uno de los programas de prueba...
        for ( String programa : programas ) { 
            i++;
            System.out.println ( "********* emparejarTest #" + i + " *********" );
            
            // Ejecutar el Lexico y Sintactico 
            cmp.analizarLexico (programa );
            cmp.analizarSintaxis();

            // Debe contabilizar al menos 1 error y el primer error debe ser de [emparejar]
            assertTrue ( "emparejarTest #" + i, 
                    cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) > 0 );
//            assertTrue ( "emparejarTest #" + i, 
//                cmp.getPrimerMensError ( Compilador.ERR_SINTACTICO )
//                        .contains("[emparejar]" ) );
        }
    }
  
    //--------------------------------------------------------------------------
    
    @Test
    public void simboloInicialTest () {
        ArrayList<String> programas = new ArrayList<> ();
        
        // #1 - Error en la linea 1, no se permite un programa en blanco
        programas.add ( "" );
        
        int i = 0;
        // Por cada uno de los programas de prueba...
        for ( String programa : programas ) { 
            i++;
            System.out.println ( "********* simboloInicialTest #" + i + " *********" );
            
            // Ejecutar el Lexico y Sintactico 
            cmp.analizarLexico (programa );
            cmp.analizarSintaxis();

            // Debe contabilizar al menos 1 error y el primer error debe ser de [emparejar]
            assertTrue ( "simboloInicialTest #" + i, 
                    cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) > 0 );
            assertTrue ( "simboloInicialTest #" + i, 
                cmp.getPrimerMensError ( Compilador.ERR_SINTACTICO )
                        .contains( "[programa]" ) ); // <---- EL SIMBOLO INICIAL es programa
        }
    }  
    
    //--------------------------------------------------------------------------
    
    @Test
    @Ignore     
    public void tipoDatoTest () {
        ArrayList<String> programas = new ArrayList<> ();
        
        // #1 - Error en la linea 1, se esperaba un tipo de dato
        programas.add ( 
        """

        """         
        );
        
        // #2 - Error en la linea 1, se esperaba un tipo de dato
        programas.add ( 
      """

        """         
        );        
        
        int i = 0;
        // Por cada uno de los programas de prueba...
        for ( String programa : programas ) { 
            i++;
            System.out.println ( "********* tipoDatoTest #" + i + " *********" );
            
            // Ejecutar el Lexico y Sintactico 
            cmp.analizarLexico (programa );
            cmp.analizarSintaxis();

            // Debe contabilizar al menos 1 error y el primer error debe ser de [emparejar]
            assertTrue ( "tipoDatoTest #" + i, 
                    cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) > 0 );
            assertTrue ( "tipoDatoTest #" + i, 
                cmp.getPrimerMensError ( Compilador.ERR_SINTACTICO )
                        .contains("[T]" ) );
        }
    }     
    
    //--------------------------------------------------------------------------
    
    @Test
    @Ignore     
    public void expresionTest () {
        ArrayList<String> programas = new ArrayList<> ();
        
        // #1 - Error en la linea 2, se esperaba una expresion
        programas.add ( 
      """

        """         
        );
        
        int i = 0;
        // Por cada uno de los programas de prueba...
        for ( String programa : programas ) { 
            i++;
            System.out.println ( "********* expresionTest #" + i + " *********" );
            
            // Ejecutar el Lexico y Sintactico 
            cmp.analizarLexico (programa );
            cmp.analizarSintaxis();

            // Debe contabilizar al menos 1 error y el primer error debe ser de [emparejar]
            assertTrue ( "expresionTest #" + i, 
                    cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) > 0 );
            assertTrue ( "expresionTest #" + i, 
                cmp.getPrimerMensError ( Compilador.ERR_SINTACTICO )
                        .contains("[E]" ) );
        }
    }     

    //--------------------------------------------------------------------------    
}
