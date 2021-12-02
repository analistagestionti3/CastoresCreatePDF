package mx.com.castores.Services;

import com.google.inject.Injector;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import mx.com.castores.CustomDao.GlobalCustomDao;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 *
 * @author Windows
 */
public class FTPService {
    private String server = "";
    private int puerto;
    private String username = "";
    private String password = "";
    private Injector inj = null;
    private FTPClient ftp;
    
    public FTPService(Injector inj){
        this.inj = inj;
    }
    
    public boolean conectar(){
        try{
            ftp = new FTPClient();
            
            //Parametros de Conexion
            this.server=getParametro(16);
            this.puerto=Integer.parseInt(getParametro(19));
            this.username=getParametro(17);
            this.password=getParametro(18);
            
            //Establecimiento de Conexion
            ftp.connect(server, puerto);
            getMensajeServerFTP(ftp);
            // Obteniendo respuesta del servidos
            int respuesta = ftp.getReplyCode();
            getMensajeServerFTP(ftp);
            
            
            // Si la respuesta del servidor indica podemos pasar procedemos
            if(!FTPReply.isPositiveCompletion(respuesta)) 
                return false;  //Retorna true si la conexion ha sido exi
           
            // Entrando a modo pasivo
            //ftp.enterLocalPassiveMode();
            
            boolean loginSatisfactorio = ftp.login(username, password);
            getMensajeServerFTP(ftp);
            
            return loginSatisfactorio;
        }
        
        catch(Exception e){
            System.out.println("Error al conectar con el con el servidor FTP: "+e.getMessage());
            return false;
        }
    }
    
    public boolean desconectar() {
        try {
            ftp.logout();
            ftp.disconnect();
            return true;
        } catch (Exception e) {
            System.out.println("Error al cerrar la conexion con el servidor FTP");
            return false;
        }
    }
    /*Ya que la respuesta de los métodos utilizados es muy ambigua, se utiliza 
    este método para devolver más detalles de la respuesta.*/
    public void getMensajeServerFTP(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }
    
    public boolean guardarArchivo(String rutaArchivoLocal, String nombreArchivoFtp) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(rutaArchivoLocal);
            
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.storeFile(nombreArchivoFtp, fis);
            getMensajeServerFTP(ftp);
            fis.close();
            
            return true;
        } catch (IOException e) {
            System.out.println("Error al cargar el archivo "+nombreArchivoFtp+" al servidor FTP");
            return false;
        }
    }
    
    public boolean borrarArchivo(String nombreArchivo) {
        try {
            /*if(ftp.cwd(nombreArchivo)==550){ 
                getMensajeServerFTP(ftp);
            }else if(ftp.cwd(nombreArchivo)==250){ 
                getMensajeServerFTP(ftp); 
                ftp.deleteFile(nombreArchivo);
            }else{ 
                getMensajeServerFTP(ftp);
            } */
            /*FTPFile[] archivoRemoto = ftp.listFiles(nombreArchivo);
            if(archivoRemoto.length > 0){
                getMensajeServerFTP(ftp); */
                ftp.deleteFile(nombreArchivo);
            //}
            
            getMensajeServerFTP(ftp);
            return true;
        } catch (Exception e) {
            System.out.println("Error al eliminar el archivo  "+nombreArchivo+ " del FTP: "+ e.getMessage());
            return false;
        }
    }
    
    public boolean crearDirectorio(String nombreDirectorio) {
        try {
            ftp.makeDirectory(nombreDirectorio);
            getMensajeServerFTP(ftp);
            return true;
        } catch (IOException e) {
            System.out.println("Error al crear el directorio "+ nombreDirectorio +" en el servidor FTP");
            return false;
        }
    }
    
    public String getParametro(int noParametro){
        String valor="";
        try{
            GlobalCustomDao dao = inj.getInstance(GlobalCustomDao.class);
            valor=dao.regresaCampo("valor", "cfdinomina.complementocp_parametros", "idparametro", String.valueOf(noParametro));
            System.out.println(dao.getLastQuery());
            
        }
        catch(Exception e){
            System.out.println("Error al obtener el valor del "
                    + "parametro "+noParametro+": "+e.getMessage());
        }
        return valor;
    }
    
    public boolean cambiarDirectorio(String dir){
        try {
            ftp.changeWorkingDirectory(dir);
            getMensajeServerFTP(ftp);
            return true;
        } catch (Exception e) {
            System.out.println("Error al cambiar de directorio: "+e.getMessage());
            return false;
        }
    }
    
    public boolean cambiarADirectorioRaiz(){
        try {
            ftp.changeToParentDirectory();
            return true;
        } catch (Exception e) {
            System.out.println("Error al cambiar al directirio Raiz: "+e.getMessage());
            return false;
        }
    }
    
    public String getContenido(String archivo) {
        String contenido = "";
        try {
            InputStream iStream = ftp.retrieveFileStream(archivo);
            
            BufferedInputStream bInf = new BufferedInputStream(iStream);
            int bytesRead;
            byte[] buffer = new byte[1024];

           while ((bytesRead = bInf.read(buffer)) != -1) {
                contenido += new String(buffer, 0, bytesRead);
            }
            iStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al obtener el contendio del archivo " + archivo + " Error: " + e.getMessage());
        }
        
        return contenido;
    }
    
    public File crearArchivoTemporal(String nombre, String contenido) {
        try {
            File file = new File(nombre);

            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(contenido);
            bw.close();

            return file;
        } catch (Exception e) {
            System.out.println("Error al crear el archivo temporal " + e.getMessage());
            return null;
        }
    }
    
    public boolean borrarArchivoTemporal(String nombre){
        try{
            File fichero = new File(nombre);
            if (fichero.exists()) {
                fichero.delete();
            }
            else{
                System.out.println("El fichero no existe");
                return false;
            }
            return true;
        }
        catch (Exception e) {
            System.out.println("Error al crear el archivo temporal " + e.getMessage());
            return false;
        }
    }
    
    public boolean moverArchivoContabilizado(File fichero){
        try{
            int replay = ftp.getReplyCode();
            
            if (FTPReply.isPositiveCompletion(replay)) {
                FileInputStream input = new FileInputStream(fichero);
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                ftp.enterLocalPassiveMode();
                
                if (!ftp.storeFile(fichero.getName(), input)) {
                    input.close();
                    return false;
                }
                input.close();
            }
           
            return true;
        }
        catch (Exception e) {
            System.out.println("Error al mover el archivo temporal " + e.getMessage());
            return false;
        }
    }
    
    public boolean borrarArchivoContabilizado(String nombreRutaArchivo) {
        try {
            ftp.deleteFile(nombreRutaArchivo);
            return true;
        } catch (Exception e) {
            System.out.println("Error al eliminar el archivo  "+nombreRutaArchivo+ " del FTP: "+ e.getMessage());
            return false;
        }
    }
}
