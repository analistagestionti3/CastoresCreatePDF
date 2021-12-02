package mx.com.castores.Test;

import mx.com.castores.Data.TalonDataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;



public class TestJasperPDF {
    
    public static JasperPrint generarReporte(){
        try {
            JRDataSource dataSource = new TalonDataSource();
            
            JasperPrint reporteLleno = JasperFillManager.fillReport("Reportes/Talon.jasper", new HashMap(), dataSource);
            return reporteLleno;
        } catch (JRException ex) {
            Logger.getLogger(TestJasperPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void main(String[] args) throws JRException {
        
        JasperPrint reporteLeno = generarReporte();
        JasperExportManager.exportReportToPdfFile(reporteLeno,"Reporte.pdf");
        JasperViewer viewer = new JasperViewer(reporteLeno);
        viewer.setVisible(true);
    }
}
