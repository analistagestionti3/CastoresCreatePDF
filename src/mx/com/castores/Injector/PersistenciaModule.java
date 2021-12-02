/**
 *
 * (c) /201 Transportes Castores de Baja California
 *
 */
package mx.com.castores.Injector;

import castores.core.Persistencia;
import castores.core.PersistenciaLocal;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;
import org.ini4j.Ini;


/**
 * Establece las bases para la injeccion de codigo
 */
public class PersistenciaModule implements Module {

    private String server13;
    private String nombreserver;
    private String server2;
    private String nombreserver2;

    @Override
    public void configure(Binder binder) {
        /** Ligar interface con la implementacion **/
        binder.bind(Persistencia.class).to(PersistenciaLocal.class);

        /** propiedades **/
        // valores por default
        //String server13 = "192.168.0.13:3306";
        if(server13.equals("")){
            server13 = "172.16.100.72";
        }
        server13 = server13 + ":3306";
        
        if(nombreserver.equals("")){
            nombreserver = "Server13";
        }
        
        String usuario13 = "usuarioWin";
        String pass13 = "windows";
        String datos13 = server13 + "&" + usuario13 + "&" + pass13;
        
        try{
            datos13 = server13 + "&" + usuario13 + "&" + pass13;
            binder.bindConstant().annotatedWith(Names.named(nombreserver)).to(datos13);
            
            if(server2 != null && !server2.isEmpty()){
                String datos2 = server2 + "&" + usuario13 + "&" + pass13;
                binder.bindConstant().annotatedWith(Names.named(nombreserver2)).to(datos2);
            }
        }
        catch(Exception ex){
            // si ocurre un error al cargar el archivo properties pone por default estos valores
            System.out.println(ex.toString());
        }
        
    }
    
    public String getServer13() {
        return server13;
    }

    public void setServer13(String server13) {
        this.server13 = server13;
    }
    
    public String getNombreserver() {
        return nombreserver;
    }

    public void setNombreserver(String nombreserver) {
        this.nombreserver = nombreserver;
    }
    
    public String getNombreserver2() {
        return nombreserver2;
    }

    public void setNombreserver2(String nombreserver2) {
        this.nombreserver2 = nombreserver2;
    }

    public String getServer2() {
        return server2;
    }

    public void setServer2(String server2) {
        this.server2 = server2;
    }

}

