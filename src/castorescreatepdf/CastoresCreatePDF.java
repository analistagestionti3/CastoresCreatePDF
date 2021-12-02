package castorescreatepdf;

import com.castores.datautilsapi.log.LoggerUtils;
import com.google.inject.Injector;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.com.castores.Injector.AppInjector;
import mx.com.castores.Models.Servidor;
import mx.com.castores.Services.CreatePDFService;
import mx.com.castores.Utils.Tools;
import org.ini4j.Ini;

/* 
 * Autor: Jasiel Galvan
 * Fecha: 16-11-2021
 * Descripción: Se toma como base el proyecto de CFDINomina.
 * La función de este proyecto es realizar un PDF con todos los talones
 * del viaje junto con su complemento CARTA PORTE. 
 */
public class CastoresCreatePDF implements Runnable {
    static final String VERSION_SISTEMA = "1.0.0.0";
    static int tiempoInicial;// Tiempo inicial en iniciar la tarea y cada cuanto se ejecuta
    private List<Servidor> listaServidor;
    Tools tools;
    
    public CastoresCreatePDF(){
        listaServidor = new ArrayList<Servidor>();
        tools = new Tools();
        if (!cargarConfiguracion("")) { // lectura de archivo ini
            System.out.println("Ocurrio un error al cargar la Configuracion, Favor de revisar el Ini");
        }
    }
    
    public void run() {
        try {
            tools.mensaje("Iniciando Herramienta Creación de PDF");
            Injector inj;
            CreatePDFService objPDFService;
            for (int i = 0; i < listaServidor.size(); i++) {
                inj = AppInjector.getInjector(listaServidor.get(i).getIpservidor(), 
                                              listaServidor.get(i).getNombreservidor(),
                                              listaServidor.get(i).getIpservidor2(), 
                                              listaServidor.get(i).getNombreservidor2());
                objPDFService = new CreatePDFService(inj);
                objPDFService.iniciarProceso();
            }
            tools.mensaje("Terminado Herramienta Creación de PDF");
        }catch(Exception ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            tools.logger(ex.toString());
        }finally{
            AppInjector.setInjector(null);
        }
    }
    
    public static void main(String[] args) {
        System.out.println(System.getProperty("java.version"));
        System.out.println(System.getProperty("java.home"));
        System.out.println("CastoresCreatePDF Versión: " + VERSION_SISTEMA);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        
        Runnable task = new CastoresCreatePDF();
        
        scheduler.scheduleAtFixedRate(task, tiempoInicial, tiempoInicial, TimeUnit.SECONDS);     
    }
    
     public boolean cargarConfiguracion(String rutaArchivo) {
        try {
            String url = "";
            
            if(rutaArchivo.isEmpty())
                url = System.getProperty("user.dir") + File.separator + "CreatePDF.ini";
            else
                url = rutaArchivo + "CreatePDF.ini";
            
            Ini ini = new Ini(new FileReader(url));
            //Recuperar todas las secciones del fichero .ini
            for (Ini.Section section : ini.values()) {
                //Obtener las entradas por cada una de las secciones
                if (section.getName().equalsIgnoreCase("general")) {
                    for (String option : section.keySet()) {
                        if (option.equalsIgnoreCase("tiempo")) {
                            tiempoInicial = Integer.parseInt(section.fetch(option));
                        }
                    }
                } else if (section.getName().equalsIgnoreCase("servidor")) {
                    Servidor objServidor = new Servidor();
                    for (String option : section.keySet()) {
                        if (option.equalsIgnoreCase("servidor")) 
                            objServidor.setIpservidor(section.fetch(option));
                        if (option.equalsIgnoreCase("nombre")) 
                            objServidor.setNombreservidor(section.fetch(option));
                        if (option.equalsIgnoreCase("servidor2")) 
                            objServidor.setIpservidor2(section.fetch(option));
                        if (option.equalsIgnoreCase("nombre2")) 
                            objServidor.setNombreservidor2(section.fetch(option));
                    }
                    if(objServidor.getNombreservidor().isEmpty())
                        objServidor.setNombreservidor("Server13");
                    if(objServidor.getIpservidor2() == null || objServidor.getIpservidor2().isEmpty())
                        objServidor.setIpservidor2(objServidor.getIpservidor());
                    if(objServidor.getNombreservidor2() == null || objServidor.getNombreservidor2().isEmpty())
                        objServidor.setNombreservidor2("Server23");
                    listaServidor.add(objServidor);
                }
            }

            return true;
        } catch (Exception ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            
            new Tools().logger(ex.toString());
            return false;
        }
    }
    public List<Servidor> getListaServidor() {
        return listaServidor;
    }

    public void setListaServidor(List<Servidor> listaServidor) {
        this.listaServidor = listaServidor;
    }
}
