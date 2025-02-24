/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package pruebas_sintactico;

import compilador.Compilador;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author FGIL.0
 */
public class SintacticoOKTest {
    Compilador cmp = new Compilador ();
    
    public SintacticoOKTest() {
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
    public void programasOKTest () {
        ArrayList<String> programas = new ArrayList<> ();
        
        // #1 - Programa sin errores
        programas.add ( """
            end
        """ );
        
        // #2 - Programa sin errores
        programas.add ( """
              dim  a as single,  b as single
              dim  c as integer
              dim  s as string, z as integer
            end
        """ );

        // #3 - Programa sin errores
        programas.add ( """
              function calcular  as string
              end function
              
              function ecuacion ( c0 as single ) as single
              end function
              
              function leyendaRespuesta (  c1 as integer, c2 as single ) as string
              end function
            
              sub retardo
              end sub
              
              sub imprimir (  x as integer, y as single, z as string ) 
              end sub
            end
        """ );
        
        // #4 - Programa sin errores
        programas.add ( """
              dim  a as single,  b as single
              dim  c as integer
              dim  s as string, z as integer
            
              function calcular  as string
              end function
              
              function ecuacion ( c0 as single ) as single
              end function
              
              function leyendaRespuesta (  c1 as integer, c2 as single ) as string
              end function
            
              sub retardo
              end sub
              
              sub imprimir (  x as integer, y as single, z as string ) 
              end sub
            end  
        """ );

        // #5 - Programa sin errores
        programas.add ( """
                a = 882
                b = 3.12123
                c = 0
                d = ( 2 * c + d ) * a + 1
            	s = "Hola mis inges"
            end
        """ );

        // #6 - Programa sin errores
        programas.add ( """
                a2 =  promedio ( a, b, 3 )
                a  =  1 + promedio ( a, b, 3 * ( a + b ) ) * x
            	b  =  b * suma ()
            end
        """ );
        
        // #7 - Programa sin errores
        programas.add ( """
                if  a <= 10   then
                    a = 1 
            	    b = 1.2
                else 
                    a = 1 
            	    b = 1.2
                end if
            end
        """ );       
        
        // #8 - Programa sin errores
        programas.add ( """
                do while  a <>  10 
                    do while  a + 1 * y == 2 * ( b + 3 + 1 )  
                    loop
                loop
            end
        """ ); 

        // #9 - Programa sin errores
        programas.add ( """
              dim  a as single,  b as single
              dim  c as integer
              dim  s as string, z as integer
              
              function calcular   as string
                i = 0
            	do while i < 10
            	  if i == 1 then
            	     i = i + 5
            	  else
            	     i = i + 1
            	  end if
            	loop
            	i = 10
              end function
              
              function ecuacion ( c0 as single ) as single
              end function
              
              function leyendaRespuesta (  c1 as integer, c2 as single ) as string
              end function
            
              sub retardo
                i = 0
            	do while i < 10
            	  if i == 1 then
            	     i = i + 5
            	  else
            	     i = i + 1
            	  end if
            	loop
            	i = 10  
              end sub
              
              sub imprimir (  x as integer, y as single, z as string ) 
              end sub
            
              a  = 882
              b  = 3.12123
              c  = 0
              d  = ( 2 * c + d ) * a + 1
              s  = "Hola mis inges"  
              
              if  a <= 10   then
                 if a > 0   then
                    a = 1 
            	    b = 1.2
            	 else
                 end if	 
              else 
                 a = 1   
                 do while b <= a
                    a = 1 
            	    b = 1.2
                 loop
                 b = 1.2	 
              end if  
              
              do while  a <>  10 
                    a2 =  promedio ( a, b, 3 )
                    do while  a + 1 * y  == 2 * ( b + 3 + 1 )  
                       a  =  1 + promedio ( a, b, 3 * ( a + b ) ) * x
                    loop
                    b  =  b * suma ()  		
              loop  
            
            end	   
        """ ); 

        
        int i = 0;
        // Por cada uno de los programas de prueba...
        for ( String programa : programas ) { 
            i++;
            System.out.println ( "********* programasOKTest #" + i + " *********" );
            
            // Ejecutar el Lexico y Sintactico 
            cmp.analizarLexico (programa );
            cmp.analizarSintaxis();

            // Debe contabilizar 0 errores y el primer error registrado debe ser "" 
            assertEquals ( "programasOKTest #" + i, 
                    0, cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) );
            assertEquals ( "programasOKTest #" + i, 
                    "", cmp.getPrimerMensError ( Compilador.ERR_SINTACTICO ) );
        }
    }  
    
    //--------------------------------------------------------------------------
  
}