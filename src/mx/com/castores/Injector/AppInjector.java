/**
 *
 * (c) /201 Transportes Castores de Baja California
 *
 */
package mx.com.castores.Injector;

import castores.core.InjectorContainer;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * administra la creacion de objetos tipo Injector
 */
public class AppInjector {

    //private static ModuloTestB instance;
    private static Injector inject;

    private AppInjector() {
    }

    public static Injector getInjector() {
        if (inject == null) {
            synchronized (AppInjector.class) {
                if (inject == null) {
                    inject = Guice.createInjector(new PersistenciaModule());
                    InjectorContainer ic = InjectorContainer.getInstance(inject);
                }
            }
            return inject;
        } else {
            return inject;
        }
    }

    public static Injector getInjector(String ipbd, String nombreserver, String ipbd2, String nombreserver2) {
        /*
         * if (inject == null) {
         */
        synchronized (AppInjector.class) {
            /*
             * if (inject == null) {
             */
            try {
                PersistenciaModule objPersistenciaModule = new PersistenciaModule();
                objPersistenciaModule.setServer13(ipbd);
                objPersistenciaModule.setNombreserver(nombreserver);
                objPersistenciaModule.setServer2(ipbd2);
                objPersistenciaModule.setNombreserver2(nombreserver2);
                inject = Guice.createInjector(objPersistenciaModule);
                InjectorContainer ic = InjectorContainer.getInstance(inject);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            
            //}
        }
        return inject;
        /*
         * } else { return inject; }
         */
    }

    public static void setInjector(Injector inj) {
        inject = inj;
    }
}
