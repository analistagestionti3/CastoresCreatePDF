package mx.com.castores.Data;

import castores.dao.talones.TalonesDao;
import castores.dao.talones.TrMADao;
import castores.model.talones.Impresiones_pdf;
import castores.model.talones.Talones;
import castores.model.talones.TrMA;
import com.castores.criteriaapi.core.CriteriaBuilder;
import com.google.inject.Injector;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import mx.com.castores.CustomDao.GlobalCustomDao;
import mx.com.castores.CustomDao.TalonesCustomDao;
import mx.com.castores.Models.*;
import mx.com.castores.Services.CreatePDFService;
import mx.com.castores.Services.TalonService;
import mx.com.castores.Utils.TGlobales;
import mx.com.castores.Utils.Tools;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TalonDataSource implements JRDataSource{
    private List<Impresiones_pdf> listImpresionesPdf;
    private int contador;
    GlobalCustomDao globalCustomDao;
    CriteriaBuilder cb;
    TalonesCustomDao talonCustomDao;
    TalonesDao talonesDao;
    TalonService objTalService;
    TrMADao trMADao;
    Talones talones;
    TrMA tr;
    List<Talones> listTalones;
    List<TrMA> listTr;
    
    String tabla;
    String cons_ccp;
    
    static final String cadDefault = "" ;
    String logoCastores;
    String codigoQR;
    String xml;
    Document objDocument; 
    String cla_talon;
    String evidencias;//evMA
    String informacion;//evinfo
    String razonSocialTalon;
    String tituloGrandeTalon;
    String tipoServicio;
    String ubicacion;
    String fechaDoc;
    String horaDoc;
    String cartaPorte;
    String folioGrande;
    String remision;
    String retenedorRte;
    String retenedorDest;
    String datosRte;
    String origen;
    String seRecogera;
    String telOficina;
    String datosDest;
    String destino;
    String seEntregara;
    String telOficinaDest;
    String folioFiscal;
    String noCertcsd;
    String regimenFiscal;
    String unidadMedida;
    String precioUnitario;
    String tipoComprobante;
    String valorDeclarado;
    String tipoPago;
    String cantidad;
    String empaque;
    String queContiene;
    String pesoT;
    String mts3;
    String claveConcepto;
    String concepto;
    String importe;
    String pedimento;
    String fechaPedimento;
    String aduana;
    String observaciones;
    String claveEncriptada;
    String usoCfdi;
    String metodoPago;
    String importeLetra;
    String cadenaOriginal;
    String selloDigital;
    String selloSat;
    String lblMetodoPago;
    String noCuenta;
    String fechaHoraCer;
    String noCertSat;
    String cadenaMetodoPago;
    String nomDocumentador;
    String coloniaZonificacion;
    String cpZonificacion;
    String codigoMP;
    String lugarFechaSus;
    String nombreSus;
    String dirSus;
    String ciudadSus;
    String versionCfdi;
    String noImpresion;
    String lugarexp_talon;
    String dia;
    String mes;
    String anio;
    String total;
    String txtOrigenFechaSalida;
    String txtDestinoFechaLlegada;
    String esInternacional;
    String descPedimento;
    String numPedimento;
    JRBeanCollectionDataSource jrBeanConexiones;
    JRBeanCollectionDataSource jrBeanUbicaciones;
    JRBeanCollectionDataSource jrBeanMercancias;
    JRBeanCollectionDataSource jrBeanUnidades;
    JRBeanCollectionDataSource jrBeanAutotransporte;
    
    public TalonDataSource(){
        contador = -1;
    }
    
    public TalonDataSource(List <Impresiones_pdf> listImpre_pdf, Injector inj){
        cb = new CriteriaBuilder();
        talonesDao = inj.getInstance(TalonesDao.class);
        talonCustomDao = inj.getInstance(TalonesCustomDao.class);
        trMADao = inj.getInstance(TrMADao.class);
        globalCustomDao = inj.getInstance(GlobalCustomDao.class);
        listImpresionesPdf = listImpre_pdf;
        objTalService = new TalonService();
        
        logoCastores = System.getProperty("user.dir") + CreatePDFService.slash + "img" + CreatePDFService.slash + "logocastores.jpg";
        //codigoQR = System.getProperty("user.dir") + slash + "temp" + slash + "qr" + slash + "QR.jpeg";
        
        contador = -1;
    }
    
    @Override
    public boolean next() throws JRException {
        contador++;
        
        inicializaVariables();
        if(listImpresionesPdf.size() == contador){
            return false;
        }else{
            cla_talon = listImpresionesPdf.get(contador).getCla_talon();//Clave del talon
            tabla = listImpresionesPdf.get(contador).getTabla();//Mes y Año de la tabla de TR

            Talones talon = talonCustomDao.getTalon(cla_talon);
            cb.clear();
            cb.eq("cla_talon", cla_talon);
            trMADao.setTabla(tabla);
            listTr = trMADao.findBy(cb);
            if(talon != null && listTr != null){
                if (!listTr.isEmpty()){
                    TrMA objTr = listTr.get(0);
                    int longSerie = Integer.parseInt(globalCustomDao.regresaCampo("valor", "castores.parametros", "idparametro","1"));
                    /***            1ra Sección             ******/
                    tituloGrandeTalon = "OFICINAS CORPORATIVAS: LEON, GTO. BLVD. JOSE MA. MORELOS No. 2975 COL. ALFARO  C.P. 37238\n" +
                                "TEL. (477) 7100 700 CON (3O LINEAS ) FAX Ext. 209\n" +
                                "R.F.C.  TCB-740130-3A4                                E-MAIL: castores@castores.com.mx    ";
                    razonSocialTalon = "TRANSPORTES CASTORES DE BAJA CALIFORNIA, S.A. DE C.V.";
                    
                    /*cons_ccp = globalCustomDao.regresaCampo("cons_ccp", "cfdinomina.complementocp_consecutivo", "cla_talon", cla_talon + " ORDER BY cons_ccp DESC LIMIT 1");
                    //Si es un numero significa que si tiene complemento Carta Porte
                    if(TGlobales.isNum(cons_ccp)){
                        Integer.parseInt(cons_ccp);
                    }*/
                    
                    /*INICIO obtener la factura (XML) */
                    xml = talonCustomDao.getXML(cla_talon);
                    if(!xml.equals("")){
                        objDocument = objTalService.getDocumentXML(xml);
                    }
                    if(objDocument != null){
                        /***            4ta Sección             ******/
                        /*inicio usoCfdi*/
                        usoCfdi = objTalService.getUsoCFDI(objDocument);
                        /*fin usoCfdi*/

                        /*inicio metodoPago*/
                        metodoPago = objTalService.getMetodoPago(objDocument);
                        /*fin metodoPago*/

                        /*inicio importeLetra*/
                        importeLetra = "";
                        /*fin importeLetra*/

                        /*inicio cadenaoriginal*/
                        cadenaOriginal = objTalService.getCadenaOriginal(xml, objDocument);
                        /*fin cadenaoriginal*/

                        /*inicio selloDigital*/
                        selloDigital = objTalService.getSelloDigital(objDocument);
                        /*fin selloDigital*/

                        /*inicio selloSat*/
                        selloSat = objTalService.getSelloSAT(objDocument);
                        /*fin selloSat*/

                        /*inicio foliofiscal*/
                        folioFiscal = objTalService.getFolioFiscal(objDocument);
                        /*fin foliofiscal*/

                        /*inicio nocertcsd*/
                        noCertcsd = objTalService.getNoCertificado(objDocument);
                        /*fin nocertcsd*/

                        /*inicio codigoQR tiene la ruta de la imagen jpeg*/
                        codigoQR = objTalService.crearCodigoQR(objDocument);
                        /*fin codigoQR*/

                        /*inicio versionCfdi*/
                        versionCfdi = objTalService.getVersionCFDI(objDocument);
                        /*fin versionCfdi*/

                        /*NoCertificadoSAT*/
                        noCertSat = objTalService.getNoCertificadoSAT(objDocument); 
                        /*NoCertificadoSAT*/
                    }
                    /*FIN obtener la factura (XML) */

                    /*inicio tipo_servicio*/
                    if(objTr.getOcurre() == 0){
                        if(objTr.getCompleto() == 0){
                           tipoServicio = "Paquetería"; 
                        }else{
                           tipoServicio = "Completo"; 
                        }
                    }else{
                        if(objTr.getIdclasificaciondoc() == 8){
                            tipoServicio = "Mensajería Ocurre";
                        }else{
                            tipoServicio = "Ocurre";
                        }
                    }
                    /*fin tipo_servicio*/

                    /*inicio ubicacion*/
                    ubicacion = objTr.getUbicacion();
                    /*fin ubicacion*/

                    /*inicio cartaPorte*/
                    //if(objTr.getIddocumentosat() == 1){
                        cartaPorte = "CARTAPORTE-FACTURA";
                    //}
                    /*fin cartaPorte*/

                    /*inicio foliogrande*/
                    if((objTr.getIdclasificaciondoc() == 8) && (objTr.getOcurre() == 1)){
                        folioGrande = "<"+listTr.get(0).getSerie() + "-" + listTr.get(0).getCla_talon().substring(longSerie+2, listTr.get(0).getCla_talon().length())+">";
                    }else{
                        folioGrande = "<"+listTr.get(0).getSerie() + "-" + listTr.get(0).getCla_talon().substring(longSerie+1, listTr.get(0).getCla_talon().length())+">";
                    }
                    /*fin foliogrande*/

                    /*inicio remision*/
                    remision = objTr.getRemision();
                    /*fin remision*/

                    /*inicio fecha*/
                    fechaDoc = talonCustomDao.getAbreviacionCiudad(objTr.getCdorigen()) + " " 
                            + TGlobales.dateFormatter("yyyy-MM-dd","d 'de' MMMM 'del' yyyy", TGlobales.verFecha(objTr.getFecha(), "yyyy-MM-dd"))+" "+
                             TGlobales.verFecha(objTr.getHora(), "hh:mm:ss");
                    //horaDoc = TGlobales.verFecha(objTr.getHora(), "hh:mm:ss");
                    dia = String.valueOf(TGlobales.getDia(objTr.getFecha()));
                    mes = String.valueOf(TGlobales.getMes(objTr.getFecha()));
                    anio = String.valueOf(TGlobales.getAnio(objTr.getFecha()));
                    /*fin fecha*/

                    /*inicio retenedorRte*/
                    if((objTr.getIva_ret() > 0.00) && (objTr.getTipopago() <= 2)){
                        retenedorRte = "RETENEDOR";
                    }else{
                        retenedorRte = "";
                    }
                    /*fin retenedorRte*/

                    /*inicio retenedorDest*/
                    if((objTr.getIva_ret() > 0.00) && (objTr.getTipopago() == 3)){
                        retenedorDest = "DATOS DEL RETENEDOR";
                    }else{
                        retenedorDest = "";
                    }
                    /*fin retenedorDest*/

                    /***            2da Sección             ******/
                    /*inicio datosRte*/
                    datosRte = "";
                    if(!objTr.getRfcorigen().equals(""))
                        datosRte = datosRte + "RFC: "+ objTr.getRfcorigen() + "   ";
                    if(!objTr.getNomorigen().equals(""))    
                        datosRte = datosRte + "NOMBRE: "+ objTr.getNomorigen() + "\n";
                    if(!objTr.getCalleorigen().equals(""))
                        datosRte = datosRte + "CALLE: "+ objTr.getCalleorigen() +"  ";
                    if(!objTr.getNoextrte().equals(""))
                        datosRte = datosRte + "No. EXT.: " + objTr.getNoextrte() + " ";
                    if(!objTr.getNointrte().equals(""))
                        datosRte = datosRte + "No. INT.: " + objTr.getNointrte() + " ";
                    if(!objTr.getColoniaorigen().equals(""))
                        datosRte = datosRte + "COLONIA: " + objTr.getColoniaorigen() + " ";
                              //DELEGACION
                    if(objTr.getCdorigen() != 0)
                        datosRte = datosRte + "CIUDAD: " + objTr.getCdorigen() + " ";
                    if(!objTr.getCporigen().equals(""))
                        datosRte = datosRte + "CP: " + objTr.getCporigen() + "\n";
                    if(!objTr.getTelorigen().equals(""))
                        datosRte = datosRte + "TELEFONO: " + objTr.getTelorigen() + "";
                    /*fin datosRte*/

                    /*inicio origen*/
                    origen = talonCustomDao.getAbreviacionCiudad(objTr.getCdorigen());
                    /*fin origen*/

                    /*inicio seRecogera*/
                    seRecogera = "";
                    if(objTr.getColrec().equals("")){
                        if(objTr.getCdorigen() > 0)
                            seRecogera = seRecogera + talonCustomDao.getAbreviacionCiudad(objTr.getCdorigen()) + "\n";
                        if(!objTr.getCalleorigen().equals(""))
                            seRecogera = seRecogera + "CALLE: "+ objTr.getCalleorigen() +"  ";
                        if(!objTr.getNoextrte().equals(""))
                            seRecogera = seRecogera + "No. EXT.: " + objTr.getNoextrte() + " ";
                        if(!objTr.getNointrte().equals(""))
                            seRecogera = seRecogera + "No. INT.: " + objTr.getNointrte() + " ";
                        if(!objTr.getColoniaorigen().equals(""))
                            seRecogera = seRecogera + "COLONIA: " + objTr.getColoniaorigen() + " ";
                        if(!objTr.getCporigen().equals(""))
                            seRecogera = seRecogera + "CP: " + objTr.getCporigen() + "\n";
                        if(!objTr.getTelorigen().equals(""))
                            seRecogera = seRecogera + "TELEFONO: " + objTr.getTelorigen() + "";
                    }else{
                        if(objTr.getIdcdrec() > 0)
                            seRecogera = seRecogera + talonCustomDao.getAbreviacionCiudad(objTr.getIdcdrec()) + "\n";
                        if(!objTr.getCallerec().equals(""))
                            seRecogera = seRecogera + "CALLE: "+ objTr.getCallerec()+"  ";
                        if(!objTr.getNoextrec().equals(""))
                            seRecogera = seRecogera + "No. EXT.: " + objTr.getNoextrec() + " ";
                        if(!objTr.getNointrec().equals(""))
                            seRecogera = seRecogera + "No. INT.: " + objTr.getNointrec() + " ";
                        if(!objTr.getColrec().equals(""))
                            seRecogera = seRecogera + "COLONIA: " + objTr.getColrec() + " ";
                        if(!objTr.getCprec().equals(""))
                            seRecogera = seRecogera + "CP: " + objTr.getCprec() + "\n";
                        if(!objTr.getTelrec().equals(""))
                            seRecogera = seRecogera + "TELEFONO: " + objTr.getTelrec() + "";
                    }
                    /*fin seRecogera*/

                    /*inicio teloficina*/
                    telOficina = objTr.getSerie() + "-"+ globalCustomDao.regresaCampo("telefono", "telefonia.telsoficina", "idoficina", objTr.getIdoficina());
                    /*fin teloficina*/

                    /*inicio datosDest*/
                    datosDest = "";
                    if(!objTr.getRfcdestino().equals(""))
                        datosDest = datosDest + "RFC: "+ objTr.getRfcdestino() + "   ";
                    if(!objTr.getNomdestino().equals(""))    
                        datosDest = datosDest + "NOMBRE: "+ objTr.getNomdestino() + "\n";
                    if(!objTr.getCalledestino().equals(""))
                        datosDest = datosDest + "CALLE: "+ objTr.getCalledestino() +"  ";
                    if(!objTr.getNoextdest().equals(""))
                        datosDest = datosDest + "No. EXT.: " + objTr.getNoextdest() + " ";
                    if(!objTr.getNointdest().equals(""))
                        datosDest = datosDest + "No. INT.: " + objTr.getNointdest() + " ";
                    if(!objTr.getColoniadestino().equals(""))
                        datosDest = datosDest + "COLONIA: " + objTr.getColoniadestino() + " ";
                              //DELEGACION
                    if(objTr.getCddestino() != 0)
                        datosDest = datosDest + "CIUDAD: " + objTr.getCddestino() + " ";
                    if(!objTr.getCpdestino().equals(""))
                        datosDest = datosDest + "CP: " + objTr.getCpdestino() + "\n";
                    if(!objTr.getTeldestino().equals(""))
                        datosDest = datosDest + "TELEFONO: " + objTr.getTeldestino() + "";
                    /*fin datosDest*/

                    /*inicio destino*/
                    destino = talonCustomDao.getAbreviacionCiudad(objTr.getCddestino());
                    /*fin destino*/

                    /*inicio seEntregara*/
                    seEntregara = "";
                    if(objTr.getColdes().equals("")){
                        if(objTr.getCdorigen() > 0)
                            seEntregara = seEntregara + talonCustomDao.getAbreviacionCiudad(objTr.getCddestino()) + "\n";
                        if(!objTr.getCalleorigen().equals(""))
                            seEntregara = seEntregara + "CALLE: "+ objTr.getCalledestino() +"  ";
                        if(!objTr.getNoextrte().equals(""))
                            seEntregara = seEntregara + "No. EXT.: " + objTr.getNoextdest() + " ";
                        if(!objTr.getNointrte().equals(""))
                            seEntregara = seEntregara + "No. INT.: " + objTr.getNointdest() + " ";
                        if(!objTr.getColoniaorigen().equals(""))
                            seEntregara = seEntregara + "COLONIA: " + objTr.getColoniadestino() + " ";
                        if(!objTr.getCporigen().equals(""))
                            seEntregara = seEntregara + "CP: " + objTr.getCpdestino() + " ";
                        if(!objTr.getTelorigen().equals(""))
                            seEntregara = seEntregara + "TELEFONO: " + objTr.getTeldestino() + "";
                    }else{
                        if(objTr.getIdcddes()> 0)
                            seEntregara = seEntregara + talonCustomDao.getAbreviacionCiudad(objTr.getIdcddes()) + "\n";
                        if(!objTr.getCallerec().equals(""))
                            seEntregara = seEntregara + "CALLE: "+ objTr.getCalledes()+"  ";
                        if(!objTr.getNoextrec().equals(""))
                            seEntregara = seEntregara + "No. EXT.: " + objTr.getNoextdes() + " ";
                        if(!objTr.getNointrec().equals(""))
                            seEntregara = seEntregara + "No. INT.: " + objTr.getNointdes() + " ";
                        if(!objTr.getColrec().equals(""))
                            seEntregara = seEntregara + "COLONIA: " + objTr.getColdes() + " ";
                        if(!objTr.getCprec().equals(""))
                            seEntregara = seEntregara + "CP: " + objTr.getCpdes() + " ";
                        if(!objTr.getTelrec().equals(""))
                            seEntregara = seEntregara + "TELEFONO: " + objTr.getTeldes() + "";
                    }
                    /*fin seEntregara*/

                    /*inicio teloficinadestino*/
                    telOficinaDest = talonCustomDao.getTelOficina(objTr.getIdcddes());
                    /*fin teloficinadestino*/

                    /*inicio regimenFiscal*/
                    regimenFiscal = "";
                    /*fin regimenFiscal*/

                    /*inicio unidadMedida*/
                    unidadMedida = "";
                    /*fin unidadMedida*/

                    /*inicio precioUnitario*/
                    precioUnitario = TGlobales.moneda(objTr.getImportesubtotal());
                    /*fin precioUnitario*/

                    /*inicio tipoComprobante*/
                    tipoComprobante = "";
                    /*fin tipoComprobante*/

                    /***            3ra Sección             ******/
                    /*inicio val_decl*/
                    valorDeclarado = String.valueOf(objTr.getVal_decl());
                    /*fin val_decl*/

                    /*inicio tipodePago*/
                    switch(objTr.getTipopago()){
                        case 1:
                            tipoPago = "PAGADO";
                            break;
                        case 2:
                            tipoPago = "C. ORIGEN";
                            break;
                        case 3:
                            tipoPago = "C. DESTINO";
                            break;
                        default:
                           tipoPago = "***PAGADO***";
                            break;
                    }
                    /*fin tipodePago*/

                    /*inicio cantidad*/
                    ArrayList<String> tablaContiene = talonCustomDao.getTablaContiene(cla_talon, tabla);
                    cantidad = tablaContiene.get(0);
                    /*fin cantidad*/

                    /*inicio empaque*/
                    empaque = tablaContiene.get(1);
                    /*fin empaque*/

                    /*inicio que_contiene*/
                    queContiene = tablaContiene.get(2);
                    /*fin que_contiene*/

                    /*inicio pesoT*/
                    pesoT = tablaContiene.get(3);
                    /*fin pesoT*/

                    /*inicio mts3*/
                    mts3 = tablaContiene.get(4);
                    /*fin mts3*/

                    /*inicio claveConcepto*/
                    claveConcepto = "78101800 (E48)\n78101800 (E48)\n\n78101800 (E48)\n78101800 (E48)\n78101800 (E48)\n78101800 (E48)\n78101800 (E48)\n"
                            + "78101800 (E48)\n\n78101800 (E48)\n\n78101800 (E48)\n\n78101800 (E48)\n\n78101800 (E48)";
                    /*fin claveConcepto*/

                    /*inicio concepto*/
                    concepto = "FLETE\nSEGURO\n\nCAPUFE\nRECOLECCION\nENTREGA\nMANIOBRAS\nCDP\nFERRY\nCITA\nOTROS\nADMON. OCURRE\nADMON. FIN.\n"
                        + "EyE\nTRASLADO CEDIS\n\nSUB-TOTAL\nIVA\n4% RET. I.V.A.\nO. LINEAS\nTOTAL";
                    /*fin concepto*/
                    /*inicio importe*/
                    ArrayList<String> tablaImporte = talonCustomDao.getImportes(cla_talon, tabla); 
                    importe = TGlobales.numeros(objTr.getSuma_flete()) +"\n"+
                              TGlobales.numeros(objTr.getImporteseguro()) +"\n\n"+
                              TGlobales.numeros(objTr.getCasetas()) +"\n"+
                              TGlobales.numeros(objTr.getRecoleccion()) +"\n"+
                              TGlobales.numeros(objTr.getEntrega()) +"\n"+
                              TGlobales.numeros(objTr.getManiobras()) +"\n"+
                              TGlobales.numeros(objTr.getCpac()) +"\n"+
                              TGlobales.numeros(objTr.getFerry()) +"\n"+
                              TGlobales.numeros(objTr.getGps()) +"\n"+
                              TGlobales.numeros(objTr.getOtroscargos()) +"\n"+
                              tablaImporte.get(0) +"\n"+ //Admon Ocurre
                              tablaImporte.get(1) +"\n"+ //Admon Fin
                              tablaImporte.get(2) +"\n"+ //Admon EyE
                              tablaImporte.get(3) +"\n\n"+ //Admon Traslado Cedis
                              TGlobales.numeros(objTr.getImportesubtotal()) +"\n"+
                              TGlobales.numeros(objTr.getImporteiva()) +"\n"+
                              TGlobales.numeros(objTr.getImporteiva_ret()) +"\n"+
                              TGlobales.numeros(objTr.getOtras_lineas()) +"\n"+
                              TGlobales.numeros(objTr.getImportetotal());


                            //tablaConceptoImporte.get(1);
                    /*fin importe*/


                    /*inicio pedimento*/
                    pedimento = talonCustomDao.getPedimento(cla_talon, tabla);
                    /*fin pedimento*/

                    /*inicio fechaPedimento*/
                    fechaPedimento = "";
                    /*fin fechaPedimento*/

                    /*inicio aduana*/
                    aduana = "";
                    /*fin aduana*/

                    /*inicio observaciones*/
                    observaciones = objTr.getObservaciones();
                    /*fin observaciones*/

                    /*inicio evidencias*/
                    evidencias = "";
                    /*fin evidencias*/

                    /*inicio evinfo*/
                    informacion = "";
                    /*fin evinfo*/

                    /*inicio noCuenta CONSULTA TARDA MUCHO*/
                    //noCuenta = globalCustomDao.regresaCampo("nocuentapago", "talones.detatr", "cla_talon", objTr.getCla_talon());
                    /*fin noCuenta*/

                    /*inicio nom_documentador*/
                    nomDocumentador = talonCustomDao.getNombrePersonal(objTr.getIdpersonal());
                    /*fin nom_documentador*

                    /*inicio clave_encriptada*/
                    claveEncriptada = talonCustomDao.getClaveEncriptada(cla_talon);
                    /*fin clave_encriptada*/

                    /*inicio lugarFechaSus*/
                    if(objTr.getTipopago() == 1 || objTr.getTipopago() == 2 || objTr.getTipopago() == 4){
                        lugarFechaSus = talonCustomDao.getAbreviacionCiudad(objTr.getCdorigen());
                    }else{
                        lugarFechaSus = talonCustomDao.getAbreviacionCiudad(objTr.getCddestino());
                    }
                    /*fin lugarFechaSus*/

                    /*inicio nombre_Sus*/
                    if(objTr.getTipopago() == 1 || objTr.getTipopago() == 2){
                        nombreSus = objTr.getNomorigen();
                    }else{
                        nombreSus = objTr.getNomdestino();
                    }
                    /*fin nombre_Sus*/

                    /*inicio dir_Sus*/
                    if(objTr.getTipopago() == 1 || objTr.getTipopago() == 2){
                        dirSus = objTr.getCalleorigen() + " COL. " + objTr.getColoniaorigen();
                    }else{
                        dirSus = objTr.getCalledestino() + " COL. " + objTr.getColoniadestino();
                    }
                    /*fin dir_Sus*/

                    /*inicio ciudad_Sus*/
                    if(objTr.getTipopago() == 1 || objTr.getTipopago() == 2){
                        ciudadSus = talonCustomDao.getAbreviacionCiudad(objTr.getCdorigen());
                    }else{
                        ciudadSus = talonCustomDao.getAbreviacionCiudad(objTr.getCddestino());
                    }
                    /*fin ciudad_Sus*/

                    /*total al final*/
                    total = TGlobales.numeros(objTr.getImportetotal());
                    /*total al final*/

                    /*inicio numero de impresion*/
                    noImpresion = String.valueOf(contador+1);
                    /*fin numero de impresion*/

                    /*Inicio Identificar cuando las mercancias son internacionales*/
                    ArrayList<String> pedimentoCartaPorte = talonCustomDao.getPedimentoCartaPorte(cla_talon); 
                    esInternacional = pedimentoCartaPorte.get(0);
                    descPedimento = pedimentoCartaPorte.get(1);
                    numPedimento = pedimentoCartaPorte.get(2);
                    /*FIN Identificar cuando las mercancias son internacionales*/
                    
                    /*inicio conexiones*/
                    List <TablaConexiones> listConexiones = talonCustomDao.getConexiones(cla_talon);
                    jrBeanConexiones = new JRBeanCollectionDataSource(listConexiones);
                    /*fin conexiones*/

                    /*inicio ubicaciones*/
                    List <TablaUbicaciones> listUbicaciones = talonCustomDao.getUbicaciones(cla_talon);
                    jrBeanUbicaciones = new JRBeanCollectionDataSource(listUbicaciones);
                    /*fin ubicaciones*/

                    /*inicio mercancias*/
                    List <TablaMercancias> listMercancias = talonCustomDao.getMercancias(cla_talon);
                    jrBeanMercancias = new JRBeanCollectionDataSource(listMercancias);
                    /*fin mercancias*/

                    /*inicio unidades*/
                    List <TablaUnidades> listUnidades = talonCustomDao.getUnidades(cla_talon);
                    jrBeanUnidades = new JRBeanCollectionDataSource(listUnidades);
                    /*fin unidades*/

                    /*inicio autotransporte*/
                    List <TablaAutoTransporte> listAutotransporte = talonCustomDao.getAutotransporte(cla_talon);
                    jrBeanAutotransporte = new JRBeanCollectionDataSource(listAutotransporte);
                    /*inicio autotransporte*/
                }
            }
            //new Tools().mensaje((contador+1) + " de " + listImpresionesPdf.size());
        }
        return contador < listImpresionesPdf.size();
    }
    
    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        //JRField representa un campo (field) en EL REPORTE
        switch (jrf.getName()) {
            case "rutaLogoCastores":
                return logoCastores;
            case "codigoQR":
                return codigoQR;
            case "evidencias":
                return evidencias;
            case "informacion":
                return informacion;
            case "razonSocialTalon":
                return razonSocialTalon;
            case "tituloGrandeTalon":
                return tituloGrandeTalon;
            case "tipoServicio":
                return tipoServicio;
            case "ubicacion":
                return ubicacion;
            case "fechaDoc":
                return fechaDoc;
            /*case "horaDoc":
                return horaDoc;*/
            case "cartaPorte":
                return cartaPorte;
            case "folioGrande":
                return folioGrande;
            case "remision":
                return remision;
            case "retenedorRte":
                return retenedorRte;
            case "retenedorDest":
                return retenedorDest;
            case "datosRte":
                return datosRte;
            case "origen":
                return origen;
            case "seRecogera":
                return seRecogera;
            case "telOficina":
                return telOficina;
            case "datosDest":
                return datosDest;
            case "destino":
                return destino;
            case "seEntregara":
                return seEntregara;
            case "telOficinaDest":
                return telOficinaDest;
            case "folioFiscal":
                return folioFiscal;
            case "noCertcsd":
                return noCertcsd;
            case "regimenFiscal":
                return regimenFiscal;
            case "unidadMedida":
                return unidadMedida;
            case "precioUnitario":
                return precioUnitario;
            case "tipoComprobante":
                return tipoComprobante;
            case "valorDeclarado":
                return valorDeclarado;
            case "tipoPago":
                return tipoPago;
            case "cantidad":
                return cantidad;
            case "empaque":
                return empaque;
            case "queContiene":
                return queContiene;
            case "pesoT":
                return pesoT;
            case "mts3":
                return mts3;
            case "claveConcepto":
                return claveConcepto;
            case "concepto":
                return concepto;
            case "importe":
                return importe;
            case "pedimento":
                return pedimento;
            case "fechaPedimento":
                return fechaPedimento;
            case "aduana":
                return aduana;
            case "observaciones":
                return observaciones;
            case "cla_talon":
                return cla_talon;
            case "claveEncriptada":
                return claveEncriptada;
            case "usoCfdi":
                return usoCfdi;
            case "metodoPago":
                return metodoPago;
            case "importeLetra":
                return importeLetra;
            case "cadenaOriginal":
                return cadenaOriginal;
            case "selloDigital":
                return selloDigital;
            case "selloSat":
                return selloSat;
            case "lblMetodoPago":
                return lblMetodoPago;
            case "noCuenta":
                return noCuenta;
            case "fechaHoraCer":
                return fechaHoraCer;
            case "noCertSat":
                return noCertSat;
            case "cadenaMetodoPago":
                return cadenaMetodoPago;
            case "nomDocumentador":
                return nomDocumentador;
            case "coloniaZonificacion":
                return coloniaZonificacion;
            case "cpZonificacion":
                return cpZonificacion;
            case "codigoMP":
                return codigoMP;
            case "lugarFechaSus":
                return lugarFechaSus;
            case "nombreSus":
                return nombreSus;
            case "dirSus":
                return dirSus;
            case "ciudadSus":
                return ciudadSus;
            case "versionCfdi":
                return versionCfdi;
            case "noImpresion":
                return noImpresion;
            case "lugarexp_talon":
                return lugarexp_talon;
            case "dia":
                return dia;
            case "mes":
                return mes;
            case "anio":
                return anio;
            case "total":
                return total;
            case "txtOrigenFechaSalida":
                return txtOrigenFechaSalida;
            case "txtDestinoFechaLlegada":
                return txtDestinoFechaLlegada;
            case "esInternacional":
                return esInternacional;
            case "descPedimento":
                return descPedimento;
            case "numPedimento":
                return numPedimento;
            case "conexionesData":
                return jrBeanConexiones;
            case "ubicacionesData":
                return jrBeanUbicaciones;
            case "mercanciasData":
                return jrBeanMercancias;
            case "unidadesData":
                return jrBeanUnidades;
            case "autotransporteData":
                return jrBeanAutotransporte;
        }
        
        return null;
    }
    
    public void inicializaVariables(){
        objDocument = null;
        cons_ccp = "";
        codigoQR = System.getProperty("user.dir") + CreatePDFService.slash + 
                   "img" + CreatePDFService.slash + "QR.jpeg";
        lugarexp_talon = cadDefault;
        tituloGrandeTalon = cadDefault;
        razonSocialTalon = cadDefault;
        evidencias = cadDefault;
        informacion = cadDefault;
        tipoServicio = cadDefault;
        ubicacion = cadDefault;
        fechaDoc = cadDefault;
        horaDoc = cadDefault;
        cartaPorte = cadDefault;
        folioGrande = cadDefault;
        remision = cadDefault;
        retenedorRte = cadDefault;
        retenedorDest = cadDefault;
        datosRte = cadDefault;
        origen = cadDefault;
        seRecogera = cadDefault;
        telOficina = cadDefault;
        datosDest = cadDefault;
        destino = cadDefault;
        seEntregara = cadDefault;
        telOficinaDest = cadDefault;
        folioFiscal = cadDefault;
        noCertcsd = cadDefault;
        regimenFiscal = cadDefault;
        unidadMedida = cadDefault;
        precioUnitario = cadDefault;
        tipoComprobante = cadDefault;
        valorDeclarado = cadDefault;
        tipoPago = cadDefault;
        cantidad = cadDefault;
        empaque = cadDefault;
        queContiene = cadDefault;
        pesoT = cadDefault;
        mts3 = cadDefault;
        claveConcepto = cadDefault;
        concepto = cadDefault;
        importe = cadDefault;
        pedimento = cadDefault;
        fechaPedimento = cadDefault;
        aduana = cadDefault;
        observaciones = cadDefault;
        cla_talon = cadDefault;
        claveEncriptada = cadDefault;
        usoCfdi = cadDefault;
        metodoPago = cadDefault;
        importeLetra = cadDefault;
        cadenaOriginal = cadDefault;
        selloDigital = cadDefault;
        selloSat = cadDefault;
        lblMetodoPago = "FORMA DE PAGO";
        noCuenta = cadDefault;
        fechaHoraCer = cadDefault;
        noCertSat = cadDefault;
        cadenaMetodoPago = cadDefault;
        nomDocumentador = cadDefault;
        coloniaZonificacion = cadDefault;
        cpZonificacion = cadDefault;
        codigoMP = cadDefault;
        lugarFechaSus = cadDefault;
        nombreSus = cadDefault;
        dirSus = cadDefault;
        ciudadSus = cadDefault;
        versionCfdi = cadDefault;
        noImpresion = cadDefault;
        esInternacional = cadDefault;
        descPedimento = cadDefault;
        numPedimento = cadDefault;
        
        dia = cadDefault;
        mes = cadDefault;
        anio = cadDefault;
        total = cadDefault;
        txtOrigenFechaSalida = cadDefault;
        txtDestinoFechaLlegada = cadDefault;
    }
}
