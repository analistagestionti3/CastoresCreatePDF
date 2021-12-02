/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.com.castores.Utils;

import com.castores.datautilsapi.log.LoggerUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

/**
 *
 * @author Windows
 */
public class Tools {
    
    public Tools(){}
    /*Autor : Jasiel Galvan
    * Fecha: 16-11-2021
    * Descripción: Método para guardar un archivo TXT registrando un LOG. (Para
    * las excepciones)
    */
    public void logger(String log) {
        FileOutputStream archivo;
        PrintStream p = null;
        try {
            String raiz = System.getProperty("user.dir");
            
            Date date = Calendar.getInstance().getTime(); 
            DateFormat fechaFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat fechaHoraFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
            String strFecha = fechaFormat.format(date);           
            String strFechaHora = fechaHoraFormat.format(date);   
            
            archivo = new FileOutputStream(raiz + File.separator +"LOG/LOG_"+strFecha+".txt", true);
            p = new PrintStream(archivo);
            p.print(strFechaHora+" "+log+"\n");
            p.flush();
            p.close();
        } catch (Exception e) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, e, e.toString(), Thread.currentThread().getStackTrace());
        } finally {
            p.flush();
            p.close();
        }
    }
    /*Autor : Jasiel Galvan
    * Fecha: 16-11-2021
    * Descripción: Método para mandar a consola algún mensaje
    */
    public void mensaje(String msj) {
        System.out.println(msj);
    }
}
