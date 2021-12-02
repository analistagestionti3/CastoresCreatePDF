package mx.com.castores.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class CadenaOriginal {
    public static void main(String[] args) throws TransformerConfigurationException, TransformerException {
        // cargar el archivo XSLT
        File xslt = new File(System.getProperty("user.dir")+ File.separator+"cadenaoriginal_3_3.xslt");
        StreamSource sourceXSL = new StreamSource(xslt);
 
        // cargar el CFDI
        File cfdi = new File(System.getProperty("user.dir")+ File.separator+"factura.xml");
        StreamSource sourceXML = new StreamSource(cfdi);
 
        // crear el procesador XSLT que nos ayudará a generar la cadena original
        // con base en las reglas del archivo XSLT
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(sourceXSL);
 
        // aplicar las reglas del XSLT con los datos del CFDI y escribir el resultado en output
       // transformer.transform(sourceXML, new StreamResult(System.out));
        
        /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.toString(null)
        StreamResult result = new StreamResult(System.out);
        transformer.transform(sourceXML, result);
        System.out.println(result.getOutputStream().toString());*/
        
        StringWriter outWriter = new StringWriter();
        StreamResult result = new StreamResult(outWriter);
        
        transformer.transform(sourceXML, result);
        
        StringBuffer sb = outWriter.getBuffer(); 
        String finalstring = sb.toString();
        
        System.out.println(finalstring);
    }
}
