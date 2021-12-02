package mx.com.castores.Test;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Vector;

public class sftp {
    public static void main(String[] args) throws SftpException, FileNotFoundException {
        try {
            JSch jsch = new JSch();
            jsch.setKnownHosts("known_hosts");
            Session session = jsch.getSession("cassftp","10.3.1.235",22);
            session.setPassword("CozCASKtor.1974");
            
            //session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            System.out.println(session.isConnected());
            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            
            channelSftp.connect();
            
            /*channelSftp.isConnected();
            System.out.println(channelSftp.pwd());
            Vector filelist = channelSftp.ls("/");
            for(int i=0; i<filelist.size();i++){
                System.out.println(filelist.get(i).toString());
            }*/
            channelSftp.cd("/publico/PDFTalones");
            
            try {
                channelSftp.ls("2020");
            } catch (SftpException e) {
                channelSftp.mkdir("2020");
            }
            
            System.out.println(channelSftp.pwd());
            
            /*FileInputStream fis;
            fis = new FileInputStream("21110135134.pdf");
            channelSftp.put(fis, "21110135134.pdf");
            
            Vector filelist = channelSftp.ls(channelSftp.pwd());
            for(int i=0; i<filelist.size();i++){
                System.out.println(filelist.get(i).toString());
            }*/
            
            channelSftp.exit();
            channelSftp.disconnect();
            
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
        
        
    }
}
