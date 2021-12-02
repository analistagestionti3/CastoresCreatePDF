package mx.com.castores.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import mx.com.castores.Utils.TGlobales;

/**
 *Clase para poder hacer test de alguna función/método.
 */
public class Testing {
    public static final Locale LOCALE_MX = new Locale("es", "MX");
    
    public static String dateFormatter(String inputFormat, String outputFormat, String inputDate) {
        //Define formato default de entrada.   
        String input = inputFormat.isEmpty() ? "yyyy-MM-dd hh:mm:ss" : inputFormat;
        //Define formato default de salida.
        String output = outputFormat.isEmpty() ? "d 'de' MMMM 'del' yyyy" : outputFormat;
        String outputDate = inputDate;
        try {
            outputDate = new SimpleDateFormat(output, LOCALE_MX).format(new SimpleDateFormat(input, LOCALE_MX).parse(inputDate));
        } catch (Exception e) {
            System.out.println("dateFormatter(): " + e.getMessage());
        }
        return outputDate;
    }

    public static void main(String[] args) {
        
        System.out.println(TGlobales.numeros(25.00));
        System.out.println(TGlobales.numeros(25.00,3));
        System.out.println(TGlobales.numeros(0));
        
        
        
        System.out.println("FECHA :" + dateFormatter("MM/dd/yyyy","dd/MM/yyyy", "05/03/2017"));
        System.out.println("FECHA: " + dateFormatter("yyyy-MM-dd hh:mm:ss","dd/MM/yyyy", "2017-05-03 12:24:34"));
        
        System.out.println("FECHA: " + dateFormatter("yyyy-MM-dd","d 'de' MMMM 'del' yyyy", TGlobales.verFecha(new Date(), "yyyy-MM-dd")));
        
        /*Date nuevaFecha = TGlobales.toDate("2021-01-14", "yyyy-MM-dd");
        
        System.out.println(nuevaFecha);
        System.out.println(TGlobales.getAnio(nuevaFecha));
        System.out.println(TGlobales.getMes(nuevaFecha));
        System.out.println(TGlobales.getDiaSemana(nuevaFecha));*/
        
    }
}
