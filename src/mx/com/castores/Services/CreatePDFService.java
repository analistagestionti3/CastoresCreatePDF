package mx.com.castores.Services;

import castores.dao.talones.Impresiones_pdfDao;
import castores.dao.talones.Impresiones_pdf_totalDao;
import castores.dao.talones.TalonesDao;
import castores.dao.talones.TrMADao;
import castores.dao.talones.ViajesDao;
import castores.model.talones.Impresiones_pdf;
import castores.model.talones.Impresiones_pdf_total;
import castores.model.talones.Talones;
import castores.model.talones.TrMA;
import com.castores.criteriaapi.core.CriteriaBuilder;
import com.castores.datautilsapi.log.LoggerUtils;
import com.google.inject.Injector;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.com.castores.CustomDao.GlobalCustomDao;
import mx.com.castores.CustomDao.TalonesCustomDao;
import mx.com.castores.Data.TalonDataSource;
import mx.com.castores.Utils.TGlobales;
import mx.com.castores.Utils.Tools;
import org.apache.commons.io.FileUtils;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
/*
 * Autor: Jasiel Galvan
 * Fecha: 16-11-2021
 * Descripción: 
 */
public class CreatePDFService {
    public final static String rutaTemp = "temp";
    public final static String slash = File.separator;
    Injector inj;
    Tools tools;
    
    public CreatePDFService(){ }
    
    public CreatePDFService(Injector inj){
        this.inj = inj;
        tools = new Tools();
    }
    
    public void iniciarProceso (){
        CriteriaBuilder cb;
        GlobalCustomDao globalCustomDao;
        //talones.impresiones_pdf_total es la tabla cabecera donde insertan el viaje para generar el PDF
        Impresiones_pdf_totalDao imp_pdfTotalDao;
        List <Impresiones_pdf_total> listImpre_pdfTotal;
        Impresiones_pdf_total objImpre_pdfTotal;
        //talones.impresiones_pdf es la tabla que contiene todos los talones del viaje
        Impresiones_pdfDao imp_pdfDao;
        List <Impresiones_pdf> listImpre_pdf;
        //Donde se almacena la información para el reporte
        TalonDataSource talonFuenteDatos;
        
        String idViaje, idOficinaViaje, folioViaje, nombrePDF;
        String fechaViajeString, idunidad, anio, mes;
        Date fechaViajeDate;
        
        try {
            crearCarpetaTemp();
            globalCustomDao = inj.getInstance(GlobalCustomDao.class);
            imp_pdfTotalDao = inj.getInstance(Impresiones_pdf_totalDao.class);
            imp_pdfDao = inj.getInstance(Impresiones_pdfDao.class);
            cb = new CriteriaBuilder();

            /*Comienza haciedo la consulta select * from talones.impresiones_pdf_total where estatus_imp = 1*/
            cb.eq("estatus_imp", 1);
            listImpre_pdfTotal = imp_pdfTotalDao.findBy(cb);
            
            if(listImpre_pdfTotal != null && listImpre_pdfTotal.size() > 0){
                tools.mensaje("Viajes a generar PDF: " + listImpre_pdfTotal.size());
                for (int i = 0; i < listImpre_pdfTotal.size(); i++) {
                    /*Recuperamos el renglon en el objeto impresion_pdf_total para actualizar
                    el estatus a 2, significa que esta procesando ese viaje*/
                    objImpre_pdfTotal = listImpre_pdfTotal.get(i);
                    objImpre_pdfTotal.setEstatus_imp("2");
                    imp_pdfTotalDao.edit(objImpre_pdfTotal);/////////////////////////////////////////////////

                    cb.clear();
                    cb.eq("idviaje", objImpre_pdfTotal.getIdviaje());
                    cb.eq("idoficinaviaje", objImpre_pdfTotal.getIdoficinaviaje());

                    listImpre_pdf = imp_pdfDao.findBy(cb);
                    if(!listImpre_pdf.isEmpty()){
                        /*Generación de PDF*/
                        try {
                            tools.mensaje("Iniciando pdf...");
                            idViaje = objImpre_pdfTotal.getIdviaje();
                            idOficinaViaje = objImpre_pdfTotal.getIdoficinaviaje();
                            
                            folioViaje = "";
                            fechaViajeString = "";
                            idunidad = "";
                            
                            List<Map> viaje = globalCustomDao.getListaPersonalizada
                            ("SELECT folio, fechaviaje, idunidad FROM talones.viajes WHERE idviaje = '"+idViaje+"' AND idoficina = "+idOficinaViaje+" LIMIT 1;");
                            if(viaje.size() > 0){
                               folioViaje = (String) viaje.get(0).get("folio");
                               fechaViajeString = (String) viaje.get(0).get("fechaviaje");
                               idunidad = (String) viaje.get(0).get("idunidad");
                            }
                            
                            /*folioViaje = globalCustomDao.regresaCampo("folio", "talones.viajes", "idviaje", 
                                                    idViaje + " AND idoficina = " + idOficinaViaje);*/
                            nombrePDF = folioViaje + ".pdf";
                            
                            
                            //Obtenemos la info que contendra el reporte
                            JRDataSource dataSource = new TalonDataSource(listImpre_pdf, inj);
                            tools.mensaje("Procesando pdf...");
                            //Se llena la info en el reporte 
                            JasperPrint reporteLleno = JasperFillManager.fillReport("Reportes/Talon_mod.jasper", new HashMap(), dataSource);
                            
                            // Se exporta el reporte en formato PDF
                            JasperExportManager.exportReportToPdfFile(reporteLleno, rutaTemp+slash+nombrePDF);
                            
                            tools.mensaje("Pdf Generado...");
                            
                            SFTPService sftpService = new SFTPService(inj);
                            if(sftpService.conectar()){
                                tools.mensaje("Conectado al servidor FTP...");
                               // fechaViajeString = globalCustomDao.regresaCampo("fechaviaje", "talones.viajes", "idviaje", idViaje + " AND idoficina = " + idOficinaViaje);
                                fechaViajeDate = TGlobales.toDate(fechaViajeString, "yyyy-MM-dd");
                                anio = String.valueOf(TGlobales.getAnio(fechaViajeDate));
                                mes = String.valueOf(TGlobales.getMes(fechaViajeDate));
                                
                                sftpService.cambiarDirectorioRaiz();
                                tools.mensaje(sftpService.channelSftp.pwd());
                                
                                sftpService.crearDirectorio(anio);
                                sftpService.cambiarDirectorio(anio);
                                sftpService.crearDirectorio(mes);
                                sftpService.cambiarDirectorio(mes);
                                sftpService.crearDirectorio(idOficinaViaje);
                                sftpService.cambiarDirectorio(idOficinaViaje);
                                
                                if(sftpService.guardarArchivo(rutaTemp+slash+nombrePDF, nombrePDF)){
                                    if(!globalCustomDao.ejecutaInstruccion(preparaInsertarPDFEnviar(folioViaje, 
                                                                                                   idOficinaViaje, 
                                                                                                   idunidad, 
                                                                                                   sftpService.obtenerRuta().replaceAll("/","|"), 
                                                                                                   0))){
                                       globalCustomDao.ejecutaInstruccion(preparaUpdatePDFEnviar(folioViaje, 
                                                                                                 idOficinaViaje, 
                                                                                                 idunidad, 
                                                                                                 sftpService.obtenerRuta().replaceAll("/","|"), 
                                                                                                 0));
                                    }
                                    tools.mensaje("Guardado en el servidor...");
                                    objImpre_pdfTotal.setEstatus_imp("3");
                                    imp_pdfTotalDao.edit(objImpre_pdfTotal);
                                }
                                //ftp.borrarArchivo(nombrePDF);
                                //ftp.guardarArchivo("PDF/"+nombrePDF, nombrePDF);
                                if(sftpService.desconectar()){
                                    tools.mensaje("Conexión cerrada al servidor FTP..."); 
                                }else{
                                    tools.mensaje("Error al cerrar conexion al servidor FTP..."); 
                                }
                            }
                            
                            for (int j = 0; j < listImpre_pdf.size(); j++) {
                                //imp_pdfDao.remove(listImpre_pdf.get(j));//////////////////////////
                            }
                        } catch (JRException ex) {
                            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                            tools.logger(ex.toString());
                            tools.mensaje("Error al generar PDF: " + ex.toString());
                        }
                    }else{
                        tools.mensaje("No se encontraron talones del viaje en la tabla impresiones_pdf");
                    }
                    //imp_pdfTotalDao.remove(objImpre_pdfTotal);////////////////////////////////
                }
            }
            //return "";
        } catch (SftpException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            tools.logger(ex.toString());
            //return "-1";
        }
        eliminaCarpetaTemp();
    }
    
    /*public void subirArchivoFTP(){
        FTPService ftp = new FTPService(inj);
        if(ftp.conectar()){
            tools.mensaje("Conectado al servidor FTP...");
            fechaViajeString = globalCustomDao.regresaCampo("fechaviaje", "talones.viajes", "idviaje", idViaje + " AND idoficina = " + idOficinaViaje);
            fechaViajeDate = TGlobales.toDate(fechaViajeString, "yyyy-MM-dd");
            anio = String.valueOf(TGlobales.getAnio(fechaViajeDate));
            mes = String.valueOf(TGlobales.getMes(fechaViajeDate));
                               
            ftp.cambiarDirectorio("PDFTalones");
            ftp.crearDirectorio(anio);
            ftp.cambiarDirectorio(anio);
            ftp.crearDirectorio(mes);
            ftp.cambiarDirectorio(mes);
            ftp.crearDirectorio(idOficinaViaje);
            ftp.cambiarDirectorio(idOficinaViaje);
                               
            ftp.borrarArchivo(nombrePDF);
            ftp.guardarArchivo("PDF/"+nombrePDF, nombrePDF);
            if(ftp.desconectar()){
                tools.mensaje("Conexión cerrada al servidor FTP..."); 
            }else{
                tools.mensaje("Error al cerrar conexion al servidor FTP..."); 
            }
            tools.mensaje("Guardado en el servidor...");
        }
    }*/
    public void crearCarpetaTemp(){
        try {
            new File(System.getProperty("user.dir") + slash + rutaTemp).mkdirs();
        } catch (Exception ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            tools.logger(ex.toString());
        }
    }
    
    public void eliminaCarpetaTemp(){
        try {
            FileUtils.deleteDirectory(new File(rutaTemp));
        } catch (IOException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            tools.logger(ex.toString());
        }
    }
    
    public String preparaInsertarPDFEnviar(String folio, String idoficinaviaje, String unidad, String url, int estatus){
        String instruccion = "INSERT INTO talones.impresiones_pdf_envio values "
                           + "('" + folio         + "' ,"
                           + "'" + idoficinaviaje + "' ,"
                           + "'" + unidad         + "' ,"
                           + "'" + url            + "' ,"
                           + " " + estatus        + " ,"
                           + " CURRENT_DATE, CURRENT_TIME);";
        
        return instruccion;
    }
    
    public String preparaUpdatePDFEnviar(String folio, String idoficinaviaje, String unidad, String url, int estatus){
        String instruccion = "UPDATE talones.impresiones_pdf_envio SET "
                           + "url ='"+ url +"', "
                           + "estatus ="+ estatus +", "
                           + "fechamod = CURRENT_DATE, horamod = CURRENT_TIME "
                           + "WHERE folio = '" + folio + "' AND "
                                + " idoficinaviaje ='"+ idoficinaviaje +"' AND "
                                + " unidad =" + unidad + ";";
        
        return instruccion;
    }
}
