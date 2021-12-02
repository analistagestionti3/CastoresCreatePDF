package mx.com.castores.Test;

/**
 *
 * @author Jasiel Galvan
 */

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class TestFTP {

    public static void main(String[] args) {
        
        final String SERVIDOR = "10.3.1.235";
        final int PUERTO = 22;
        final String USUARIO = "cassftp";
        final String PASSWORD = "CozCASKtor.1974";
        
        FTPClient clienteFtp = new FTPClient();
        
        try {
            clienteFtp.connect(SERVIDOR, PUERTO);
            int respuesta = clienteFtp.getReplyCode();
            
            if (!FTPReply.isPositiveCompletion(respuesta)){
                System.out.println("Algo ha salido mal. El servidor respondió con el código: " + respuesta);
            }
            
            boolean loginSatisfactorio = clienteFtp.login(USUARIO, PASSWORD);
            getMensajeServerFTP(clienteFtp);
            if (loginSatisfactorio){
                System.out.println("Se ha iniciado sesión en el servidor FTP.");
            } else {
                System.out.println("Las credenciales son inválidas.");
            }
            
            /*clienteFtp.changeWorkingDirectory("/PDFTalones");
            System.out.println("Current working directory is: " + clienteFtp.printWorkingDirectory());
            clienteFtp.changeWorkingDirectory(clienteFtp.printWorkingDirectory()+"/2021");
            System.out.println("Current working directory is: " + clienteFtp.printWorkingDirectory());
            
            clienteFtp.makeDirectory("PDFTalones");
            clienteFtp.changeWorkingDirectory(clienteFtp.printWorkingDirectory()+"/11");
            System.out.println("Current working directory is: " + clienteFtp.printWorkingDirectory());*/
            clienteFtp.makeDirectory("PDFTalones");
            clienteFtp.changeWorkingDirectory("PDFTalones");
            getMensajeServerFTP(clienteFtp);
            clienteFtp.setFileType(FTP.BINARY_FILE_TYPE);
            clienteFtp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            //clienteFtp.removeDirectory("test");
            //showServerReply(clienteFtp);
            getMensajeServerFTP(clienteFtp);
            FileInputStream fis = new FileInputStream("Reporte.pdf");
            clienteFtp.storeFile("Test.pdf", fis);
            getMensajeServerFTP(clienteFtp);
            fis.close();
            
            clienteFtp.disconnect();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    private static void getMensajeServerFTP(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }
}