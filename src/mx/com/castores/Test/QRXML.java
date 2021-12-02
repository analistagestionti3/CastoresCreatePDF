package mx.com.castores.Test;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class QRXML {
   public static void main(String a[]) throws Exception {
        
        try {    
            String text = getURL();
            System.out.println(text);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();            
            HashMap hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);            
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250, hints);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "JPG", pngOutputStream);
            MatrixToImageWriter.writeToPath(bitMatrix, "JPG", new File(System.getProperty("user.dir") + File.separator+"qrcode2.jpg").toPath());        
        } catch (IOException | WriterException e) {
            e.printStackTrace();
        }
    }

    private static String getURL() throws Exception {
        String xml = new String(Files.readAllBytes((Paths.get(System.getProperty("user.dir") + File.separator+"factura.xml"))));        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        Document doc = builder.parse( new InputSource( new StringReader(xml)));
        String url= MessageFormat.format("https://verificacfdi.facturaelectronica.sat.gob.mx/default.aspx?&id={0}&re={1}&rr={2}&tt={3}&fe={4}",getUUID(doc),getRfcEmisor(doc),getRfcReceptor(doc),getTotal(doc),getSelloCFD(doc));
        return url;
    }

    private static String getTotal(Document doc)  {    
        DecimalFormat df = new DecimalFormat("000000000000000000.000000");
        String totalstr = doc.getDocumentElement().getAttribute("Total").trim();
        return df.format(Double.valueOf(totalstr));
    }
    
    public static int convertirStringANumero(String numero)  {
        return Integer.parseInt(numero);
    }

    private static String getRfcEmisor(Document doc) {
        Element Emisor = (Element) doc.getDocumentElement().getElementsByTagName("cfdi:Emisor").item(0);
        return Emisor.getAttribute("Rfc").trim();
    }

    private static String getRfcReceptor(Document doc) {
        Element Receptor = (Element) doc.getDocumentElement().getElementsByTagName("cfdi:Receptor").item(0);
       return Receptor.getAttribute("Rfc").trim();
    }
    
    private static String getUUID(Document doc) {
        Element TimbreFiscalDigital = (Element) doc.getElementsByTagName("tfd:TimbreFiscalDigital").item(0);
        return TimbreFiscalDigital.getAttribute("UUID");
    }
    
    private static String getSelloCFD(Document doc) {
        Element TimbreFiscalDigital = (Element) doc.getDocumentElement().getElementsByTagName("tfd:TimbreFiscalDigital").item(0);
        String SelloCFD = TimbreFiscalDigital.getAttribute("SelloCFD");
        return SelloCFD.substring(SelloCFD.length()-8);
    }
} 
