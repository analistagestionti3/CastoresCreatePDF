package mx.com.castores.Test;

import com.onbarcode.barcode.IBarcode;
import com.onbarcode.barcode.QRCode;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CodigoQR {
    public static void main(String[] args) throws Exception {
        
        leerXML();
        
        if(false){
        DecimalFormat df = new DecimalFormat("000000000000000000.000000");
        
        System.out.println(df.format(Double.valueOf(0)));
        QRCode barcode = new QRCode();
        
        String UUID = "54374f10-ccee-4d28-9d2f-7f3d5a506fe4";
        String rfcEmisor = "TCB7401303A4";
        String rfcReceptor = "XAXX010101000";
        String total = "000000000000000000.000000";
        String ultimosCaracteresSello = "UkFmnw==";
	String cadenaQR = "https://verificacfdi.facturaelectronica.sat.gob.mx/default.aspx?&id=54374f10-ccee-4d28-9d2f-7f3d5a506fe4&re=TCB7401303A4&rr=XAXX010101000&tt=000000000000000000.000000&fe=UkFmnw==";
        /*"https://verificacfdi.facturaelectronica.sat.gob.mx/default.aspx?&id="+UUID+"&re="+rfcEmisor+"&rr="+rfcReceptor+
         "&tt="+total+"&fe="+ultimosCaracteresSello;
	/*
	   QR Code Valid data char set:
	        numeric data (digits 0 - 9);
	        alphanumeric data (digits 0 - 9; upper case letters A -Z; nine other characters: space, $ % * + - . / : );
	        byte data (default: ISO/IEC 8859-1);
	        Kanji characters
	*/
	barcode.setData(cadenaQR);
	barcode.setDataMode(QRCode.M_AUTO);
	barcode.setVersion(10);
	barcode.setEcl(QRCode.ECL_M);
	
	//  Set the processTilde property to true, if you want use the tilde character "~" to specify special characters in the input data. Default is false.
	//  1-byte character: ~ddd (character value from 0 ~ 255)
	//  ASCII (with EXT): from ~000 to ~255
	//  2-byte character: ~6ddddd (character value from 0 ~ 65535)
	//  Unicode: from ~600000 to ~665535
	//  ECI: from ~7000000 to ~7999999
	//  SJIS: from ~9ddddd (Shift JIS 0x8140 ~ 0x9FFC and 0xE040 ~ 0xEBBF)
	barcode.setProcessTilde(false);
	
	// QR Code unit of measure for X, Y, LeftMargin, RightMargin, TopMargin, BottomMargin
	barcode.setUom(IBarcode.UOM_PIXEL);
	// QR Code barcode module width in pixel
	barcode.setX(3f);
	
	barcode.setLeftMargin(10f);
	barcode.setRightMargin(10f);
	barcode.setTopMargin(10f);
	barcode.setBottomMargin(10f);
	// barcode image resolution in dpi
	barcode.setResolution(72);
	
	barcode.drawBarcode("qrcode.jpg");
        }
    }
    
    public static void leerXML() throws IOException, ParserConfigurationException, SAXException{
        String xml = new String(Files.readAllBytes((Paths.get(System.getProperty("user.dir") + File.separator+ "factura_ini.xml"))));        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        Document doc = builder.parse( new InputSource( new StringReader(xml)));
        String url=""; //= MessageFormat.format("https://verificacfdi.facturaelectronica.sat.gob.mx/default.aspx?&id={0}&re={1}&rr={2}&tt={3}&fe={4}",getUUID(doc),getRfcEmisor(doc),getRfcReceptor(doc),getTotal(doc),getSelloCFD(doc));
        String totalstr = doc.getDocumentElement().getAttribute("Total").trim();
        
        System.out.println(totalstr);
        
        Element Emisor = (Element) doc.getDocumentElement().getElementsByTagName("cfdi:Emisor").item(0);
        String rfcEmisor = Emisor.getAttribute("Rfc").trim();
        
        System.out.println(rfcEmisor);
        
        Element TimbreFiscal = (Element) doc.getDocumentElement().getElementsByTagName("tfd:TimbreFiscalDigital").item(0);
        
        if ( TimbreFiscal != null ){
            System.out.println(TimbreFiscal.getAttribute("NoCertificadoSAT"));
            System.out.println(TimbreFiscal.getAttribute("SelloCFD"));
            System.out.println(TimbreFiscal.getAttribute("UUID"));
            System.out.println(TimbreFiscal.getAttribute("SelloSAT"));
        }else{
            System.out.println("NO tiene timbre");
        }
    }
}
