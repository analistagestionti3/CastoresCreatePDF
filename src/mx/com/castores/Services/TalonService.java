package mx.com.castores.Services;

import com.castores.datautilsapi.log.LoggerUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.onbarcode.barcode.IBarcode;
import com.onbarcode.barcode.QRCode;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TalonService {
    public TalonService(){
        
    }
    
    public Document getDocumentXML(String xml){
        
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document doc;
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            
            doc = builder.parse(new InputSource(new StringReader(xml)));
        } catch (IOException | ParserConfigurationException | SAXException e) {
            return null;
        }
        return doc;
    }
    
    public String getFolio(Document doc){
        return doc.getDocumentElement().getAttribute("Folio").trim();
    }
    
    public String getVersionCFDI(Document doc){
        return doc.getDocumentElement().getAttribute("Version");
    }
    
    public String getNoCertificado(Document doc){
        return doc.getDocumentElement().getAttribute("NoCertificado");
    }
    
    public String getUsoCFDI(Document doc){
        Element receptor = (Element) doc.getDocumentElement().getElementsByTagName("cfdi:Receptor").item(0);
        if (receptor != null){
            return receptor.getAttribute("UsoCFDI").trim();
        }
        return "";
    }
    
    public String getMetodoPago(Document doc){
        return doc.getDocumentElement().getAttribute("MetodoPago").trim();
    }
    
    public String getTotal(Document doc){
        DecimalFormat df = new DecimalFormat("000000000000000000.000000");
        String totalstr = doc.getDocumentElement().getAttribute("Total").trim();
        return df.format(Double.valueOf(totalstr));
    }
    
    public String getCadenaOriginal(String xml, Document doc){
        String respuesta = "";
        try {
            xml = xml.replaceAll("http://www.sat.gob.mx/cfd/33", "http://www.sat.gob.mx/cfd/3");
            crearXMLUTF8(xml, doc);
            // cargar el archivo XSLT
            File xslt = new File(System.getProperty("user.dir")+ CreatePDFService.slash +"cadenaoriginal_3_3.xslt");
            StreamSource sourceXSL = new StreamSource(xslt);

            // cargar el CFDI
            //File cfdi = new File(System.getProperty("user.dir")+ File.separator + "temp"+ File.separator +getFolio(doc)+".xml");
            InputStreamReader is =  leerArchivoUTF8(System.getProperty("user.dir")+ CreatePDFService.slash + 
                                    CreatePDFService.rutaTemp + CreatePDFService.slash + getFolio(doc)+".xml");
            StreamSource sourceXML = new StreamSource(is);

            // crear el procesador XSLT que nos ayudará a generar la cadena original
            // con base en las reglas del archivo XSLT
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(sourceXSL);

            // aplicar las reglas del XSLT con los datos del CFDI y escribir el resultado en output
            //transformer.transform(sourceXML, new StreamResult(System.out));

            StringWriter outWriter = new StringWriter();
            StreamResult result = new StreamResult(outWriter);

            transformer.transform(sourceXML, result);

            StringBuffer sb = outWriter.getBuffer(); 
            respuesta = sb.toString();
        } catch (TransformerException e) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, e, e.toString(), Thread.currentThread().getStackTrace());
        }
        return respuesta;
    }
    
    public String getNoCertificadoSAT(Document doc){
        Element TimbreFiscal = (Element) doc.getDocumentElement().getElementsByTagName("tfd:TimbreFiscalDigital").item(0);
        if ( TimbreFiscal != null ){
            return TimbreFiscal.getAttribute("NoCertificadoSAT").trim();
        }
        return "";
    }
    
    public String getSelloDigital(Document doc){
        Element TimbreFiscal = (Element) doc.getDocumentElement().getElementsByTagName("tfd:TimbreFiscalDigital").item(0);
        if ( TimbreFiscal != null ){
            return TimbreFiscal.getAttribute("SelloCFD").trim();
        }
        return "";
    }
    
    public String getSelloSAT(Document doc){
        Element TimbreFiscal = (Element) doc.getDocumentElement().getElementsByTagName("tfd:TimbreFiscalDigital").item(0);
        if ( TimbreFiscal != null ){
            return TimbreFiscal.getAttribute("SelloSAT").trim();
        }
        return "";
    }
    
    public String getFolioFiscal(Document doc){
        Element TimbreFiscal = (Element) doc.getDocumentElement().getElementsByTagName("tfd:TimbreFiscalDigital").item(0);
        if ( TimbreFiscal != null ){
            return TimbreFiscal.getAttribute("UUID").trim();
        }
        return "";
    }
    
    public String getRfcEmisor(Document doc) {
        Element Emisor = (Element) doc.getDocumentElement().getElementsByTagName("cfdi:Emisor").item(0);
        if (Emisor != null){
            return Emisor.getAttribute("Rfc").trim();
        }
        return "";
    }

    public String getRfcReceptor(Document doc) {
        Element Receptor = (Element) doc.getDocumentElement().getElementsByTagName("cfdi:Receptor").item(0);
        if (Receptor != null){
            return Receptor.getAttribute("Rfc").trim();
        }
        return "";
    }
    
    public String getSelloCFD(Document doc) {
        Element TimbreFiscalDigital = (Element) doc.getDocumentElement().getElementsByTagName("tfd:TimbreFiscalDigital").item(0);
        String SelloCFD = TimbreFiscalDigital.getAttribute("SelloCFD");
        return SelloCFD.substring(SelloCFD.length()-8);
    }
    
    /*Este método crea una imagen que guarda el código QR del talón*/
    public String crearCodigoQR (Document doc){
        String cadenaQR;
        try {
            cadenaQR = MessageFormat.format("https://verificacfdi.facturaelectronica.sat.gob.mx/default.aspx?&id={0}&re={1}&rr={2}&tt={3}&fe={4}",getFolioFiscal(doc),getRfcEmisor(doc),getRfcReceptor(doc),getTotal(doc),getSelloCFD(doc));
            String ruta = "temp"+CreatePDFService.slash+getFolio(doc) + ".jpg";
            
            QRCodeWriter qrCodeWriter = new QRCodeWriter();            
            HashMap hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);            
            BitMatrix bitMatrix = qrCodeWriter.encode(cadenaQR, BarcodeFormat.QR_CODE, 250, 250, hints);
            ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "JPG", jpgOutputStream);
            MatrixToImageWriter.writeToPath(bitMatrix, "JPG", new File(ruta).toPath()); 
            
            return ruta;
        } catch (WriterException | IOException e) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, e, e.toString(), Thread.currentThread().getStackTrace());
        }
        return "";
    }
    
    /*Este método crea una imagen que guarda el código QR del talón*/
    public String crearXML (String xml, Document doc){
        try {
            String ruta = CreatePDFService.rutaTemp + CreatePDFService.slash + getFolio(doc) + ".xml";
            File file = new File(ruta);
            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(xml);
            bw.close();
        } catch (IOException e) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, e, e.toString(), Thread.currentThread().getStackTrace());
        }
        return "";
    }
    
    /*Este método crea una imagen que guarda el código QR del talón*/
    public String crearXMLUTF8 (String xml, Document doc){
        File file = null;
        try {
            String ruta = CreatePDFService.rutaTemp + CreatePDFService.slash + getFolio(doc) + ".xml";
            file = new File(ruta);
            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
            }
            
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            bw.write(xml);
            bw.close();
        } catch (IOException e) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, e, e.toString(), Thread.currentThread().getStackTrace());
        } 
        return "";
    }
    
    public InputStreamReader leerArchivoUTF8(String nombreArchivo){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(nombreArchivo);
            InputStreamReader is = new InputStreamReader(fis, "UTF-8");
            return is;
        } catch (IOException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            return null;
        } finally {
            /*try {
                fis.close();
            } catch (IOException ex) {
                LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
                return null;
            }*/
        }
 
    }
    public String leerArchivoUtf8(String nombreArchivo) {
        FileInputStream fis = null;
        String text = "";
        try {
            fis = new FileInputStream(nombreArchivo);
            InputStreamReader is = new InputStreamReader(fis, "UTF-8");
            BufferedReader brnom = new BufferedReader(is);
            text = "";
            String subcadnom = "";
            while ((subcadnom = brnom.readLine()) != null) {
                if (text.isEmpty()) {
                    text = subcadnom;
                } else {
                    text = text + "\r\n" + subcadnom;
                }
            }

            return text;
        } catch (IOException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            return text;
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            }
        }
    }
}
