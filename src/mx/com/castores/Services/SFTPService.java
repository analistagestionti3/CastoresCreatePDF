package mx.com.castores.Services;

import com.castores.datautilsapi.log.LoggerUtils;
import com.google.inject.Injector;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.com.castores.CustomDao.GlobalCustomDao;
import mx.com.castores.Utils.Tools;

/*Fecha: 24-11-2021
 *Autor: Jasiel Galvan
 *Descripción: Clase que funciona para manejar el servicio con un servidor SFTP.
 **/

public class SFTPService {
    private String server = "";
    private int puerto;
    private String username = "";
    private String password = "";
    private Injector inj = null;
    JSch jsch;
    Session session;
    ChannelSftp channelSftp;
    Tools tools;
    
    SFTPService (Injector inj){
        this.inj = inj;
        tools = new Tools();
    }
    
    public boolean conectar(){
        try{
            jsch = new JSch();
            
            //Parametros de Conexion
            this.server=getParametro(16);
            this.puerto=Integer.parseInt(getParametro(19));
            this.username=getParametro(17);
            this.password=getParametro(18);
            
            //Establecimiento de Conexion
            jsch.setKnownHosts("known_hosts");
            //session.setConfig("StrictHostKeyChecking", "no");
            session = jsch.getSession(username,server,puerto);
            session.setPassword(password);
            
            session.connect();
            System.out.println(session.isConnected());
            
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            
            return channelSftp.isConnected();
        }
        
        catch(NumberFormatException | JSchException ex){
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            tools.logger(ex.toString());
            
            //System.out.println("Error al conectar con el con el servidor FTP: "+e.getMessage());
            return false;
        }
    }
    
    public boolean desconectar() {
        try {
            if(channelSftp.isConnected()){
                channelSftp.exit();
                channelSftp.disconnect();
            }
            if(session.isConnected()){
                session.disconnect();
            }
            return true;
        } catch (Exception ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            tools.logger(ex.toString());
            //System.out.println("Error al cerrar la conexion con el servidor FTP");
            return false;
        }
    }
    
    public boolean crearDirectorio(String nombreDirectorio) {
        try {
            /*Intenta hacer un ls al directorio, en caso de que ocurra una excepción
            es posible que el directorio no exista por tanto se crea uno nuevo*/
            try {
                channelSftp.ls(nombreDirectorio);
            } catch (SftpException e) {
                channelSftp.mkdir(nombreDirectorio);
            }
            return true;
        } catch (SftpException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            tools.logger(ex.toString());
            //System.out.println("Error al crear el directorio "+ nombreDirectorio +" en el servidor FTP");
            return false;
        }
    }
    
    public boolean cambiarDirectorio(String nombreDirectorio) {
        try {
            channelSftp.cd(nombreDirectorio);
            
            return true;
        } catch (SftpException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            tools.logger(ex.toString());
            //System.out.println("Error al crear el directorio "+ nombreDirectorio +" en el servidor FTP");
            return false;
        }
    }
    
    public String obtenerRuta() {
        try {
            return channelSftp.pwd();
        } catch (SftpException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            tools.logger(ex.toString());
            return "";
        }
    }
    
    public boolean cambiarDirectorioRaiz() {
        try {
            channelSftp.cd(getParametro(20));
            
            return true;
        } catch (SftpException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            tools.logger(ex.toString());
            //System.out.println("Error al crear el directorio "+ nombreDirectorio +" en el servidor FTP");
            return false;
        }
    }
    
    public boolean listarDirectorioActual(){
        try {
            Vector filelist = channelSftp.ls(channelSftp.pwd());
            //String[] listDatos = filelist.toArray(new String[filelist.size()]);
            for(int i=0; i<filelist.size();i++){
                System.out.println(filelist.get(i).toString());
            }
            return true;
        } catch (SftpException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            tools.logger(ex.toString());
            System.out.println("Error al cambiar al directirio Raiz: "+ex.getMessage());
            return false;
        }
    }
    
    /*Descripción: Método para subir un archivo al servidor.
    */
    public boolean guardarArchivo(String rutaArchivoLocal, String nombreArchivoFtp) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(rutaArchivoLocal);
            //channelSftp.mkdir(nombreDirectorio);
            channelSftp.put(fis, nombreArchivoFtp);
            
            fis.close();
            
            return true;
        } catch (FileNotFoundException | SftpException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            tools.logger(ex.toString());
            //System.out.println("Error al crear el directorio "+ nombreDirectorio +" en el servidor FTP");
            return false;
        } catch (IOException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            tools.logger(ex.toString());
            return false;
        }
    }
    
    public String getParametro(int noParametro){
        String valor="";
        try{
            GlobalCustomDao dao = inj.getInstance(GlobalCustomDao.class);
            valor=dao.regresaCampo("valor", "cfdinomina.complementocp_parametros", "idparametro", String.valueOf(noParametro));
            
        }catch(Exception ex){
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, ex.toString(), Thread.currentThread().getStackTrace());
            tools.logger(ex.toString());
            /*System.out.println("Error al obtener el valor del "
                    + "parametro "+noParametro+": "+e.getMessage());*/
        }
        return valor;
    }
}
