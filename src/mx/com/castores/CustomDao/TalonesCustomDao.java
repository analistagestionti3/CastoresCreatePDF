package mx.com.castores.CustomDao;

import castores.core.Persistencia;
import castores.core.PreparedParams;
import castores.dao.talones.TalonesDao;
import castores.model.talones.Talones;
import com.castores.datautilsapi.db.DBUtils;
import com.castores.datautilsapi.log.LoggerUtils;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.com.castores.Models.TablaAutoTransporte;
import mx.com.castores.Models.TablaConexiones;
import mx.com.castores.Models.TablaMercancias;
import mx.com.castores.Models.TablaUbicaciones;
import mx.com.castores.Models.TablaUnidades;
import mx.com.castores.Utils.Tools;

public class TalonesCustomDao extends TalonesDao {
    Tools tools;
    
    @Inject
    public TalonesCustomDao(Persistencia persistencia, @Named("Server23") String server) {
        super(persistencia, server);
        tools = new Tools();
    }
    
    public List <TablaConexiones> getConexiones(String cla_talon){
        List <TablaConexiones> listConexiones = new ArrayList <>();
        
        TablaConexiones objConexionesOrigen;
        TablaConexiones objConexionesDestino;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        
        try {
            String consultaCadena = "SELECT cons_ccp FROM cfdinomina.complementocp_consecutivo WHERE cla_talon = '" + cla_talon + "' ORDER BY cons_ccp DESC LIMIT 1;";
            int consecutivo = 0;
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                consecutivo = rs.getInt(1);
            }
            if(consecutivo > 0){
                consultaCadena = "SELECT oo.calle ocalle, eo.nombre oestado, oo.cp ocp, " +
                                "        od.calle dcalle, ed.nombre destado, od.cp dcp " +
                                "FROM cfdinomina.complementocp c " +
                                "INNER JOIN personal.oficinas oo ON oo.idoficina = c.idoficinaorigen " +
                                "INNER JOIN camiones.ciudades co ON co.idciudad = oo.idciudad " +
                                "INNER JOIN camiones.estados eo ON eo.idestado = co.idestado " +
                                "INNER JOIN personal.oficinas od ON od.idoficina = c.idoficinadestino " +
                                "INNER JOIN camiones.ciudades cd ON cd.idciudad = od.idciudad " +
                                "INNER JOIN camiones.estados ed ON ed.idestado = cd.idestado " +
                                "WHERE c.idper_fac = '" + cla_talon + "' AND c.cons_ccp = "+consecutivo+";"; 
                ps = conn.prepareStatement(consultaCadena);
                rs = ps.executeQuery();
                
                while(rs.next()){
                    objConexionesOrigen = new TablaConexiones();
                    objConexionesDestino = new TablaConexiones();
                    
                    objConexionesOrigen.setTipo("Origen");
                    // 
                    objConexionesOrigen.setCalle((rs.getString(1) != null && rs.getString(1).length() > 0 ? rs.getString(1) : ""));
                    objConexionesOrigen.setEstado((rs.getString(2) != null && rs.getString(2).length() > 0 ? rs.getString(2) : ""));
                    objConexionesOrigen.setCp((rs.getString(3) != null && rs.getString(3).length() > 0 ? rs.getString(3) : ""));
                    objConexionesOrigen.setPais("México");
                    
                    objConexionesDestino.setTipo("Destino");
                    objConexionesDestino.setCalle((rs.getString(4) != null && rs.getString(4).length() > 0 ? rs.getString(4) : ""));
                    objConexionesDestino.setEstado((rs.getString(5) != null && rs.getString(5).length() > 0 ? rs.getString(5) : ""));
                    objConexionesDestino.setCp((rs.getString(6) != null && rs.getString(6).length() > 0 ? rs.getString(6) : ""));
                    objConexionesDestino.setPais("México");
                    
                    listConexiones.add(objConexionesOrigen);
                    listConexiones.add(objConexionesDestino);
                }
            }
        } catch (SQLException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, this.lastQuery, Thread.currentThread().getStackTrace());
            tools.logger(ex.getMessage());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }    
        return listConexiones;
    }
    
    
    public List <TablaUbicaciones> getUbicaciones(String cla_talon){
        List <TablaUbicaciones> listUbicaciones = new ArrayList <>();
        
        TablaUbicaciones objUbi;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        
        try {
            String consultaCadena = "SELECT cons_ccp FROM cfdinomina.complementocp_consecutivo WHERE cla_talon = '" + cla_talon + "' ORDER BY cons_ccp DESC LIMIT 1;";
            int consecutivo = 0;
            
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                consecutivo = rs.getInt(1);
            }
            if(consecutivo > 0){
                consultaCadena = "SELECT IF(dom.tipodomicilio = 1, 'ORIGEN', 'DESTINO') tipo, "+
                                        "dom.calle, cp.d_asenta colonia, cp.d_codigo, " +
                                        "cp.d_mnpio, cp.d_estado, dom.pais " +
                                "FROM cfdinomina.complementocp_domicilio dom " +
                                "LEFT JOIN camiones.codigospostalesmodulo cp " +
                                           "ON cp.c_mnpio = dom.municipio AND cp.c_estado = dom.estado " +
                                           "AND cp.id_asenta_cpcons = dom.colonia " +
                                           "AND cp.d_codigo = dom.codigopostal " +
                                "WHERE dom.idper_fac = '" + cla_talon + "' AND dom.cons_ccp = " + consecutivo + " " +
                                "ORDER BY dom.tipodomicilio, dom.cons_domicilio;";  
                ps = conn.prepareStatement(consultaCadena);
                rs = ps.executeQuery();
                while(rs.next()){
                    objUbi = new TablaUbicaciones();
                    
                    objUbi.setTipo((rs.getString(1) != null && rs.getString(1).length() > 0 ? rs.getString(1) : ""));
                    objUbi.setCalle((rs.getString(2) != null && rs.getString(2).length() > 0 ? rs.getString(2) : ""));
                    objUbi.setColonia((rs.getString(3) != null && rs.getString(3).length() > 0 ? rs.getString(3) : ""));
                    objUbi.setCp((rs.getString(4) != null && rs.getString(4).length() > 0 ? rs.getString(4) : ""));
                    objUbi.setMunicipio((rs.getString(5) != null && rs.getString(5).length() > 0 ? rs.getString(5) : ""));
                    objUbi.setLocalidad("");
                    objUbi.setEstado((rs.getString(6) != null && rs.getString(6).length() > 0 ? rs.getString(6) : ""));
                    objUbi.setPais((rs.getString(7) != null && rs.getString(7).length() > 0 ? rs.getString(7) : ""));
                    
                    listUbicaciones.add(objUbi);
                }
            }
        } catch (SQLException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, this.lastQuery, Thread.currentThread().getStackTrace());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }    
        return listUbicaciones;
    }
    
    public List <TablaMercancias> getMercancias(String cla_talon){
        List <TablaMercancias> listMercancias = new ArrayList <>();
        
        TablaMercancias objMerc;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        
        try {
            String consultaCadena = "SELECT cons_ccp FROM cfdinomina.complementocp_consecutivo WHERE cla_talon = '" + cla_talon + "' ORDER BY cons_ccp DESC LIMIT 1;";
            int consecutivo = 0;
            int totalMerc = 0;
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                consecutivo = rs.getInt(1);
            }
            if(consecutivo > 0){
                consultaCadena = "SELECT m.claveprodservcp, m.descripcion_claveprod, " +
                                "m.cantidad, m.claveunidadpeso, m.descripcion_embalaje, " +
                                "m.valormercancia, m.peso, m.cvematerialpeligroso, " +
                                "m.materialpeligroso, m.tipoembalaje, m.descripcion_unidad " +
                                "FROM cfdinomina.complementocp_mercancia m " +
                                "WHERE m.idper_fac = '" + cla_talon + "' AND m.cons_ccp = " + consecutivo + ";"; 
                ps = conn.prepareStatement(consultaCadena);
                rs = ps.executeQuery();
                if(rs.last()){
                    totalMerc = rs.getRow();
                    rs.beforeFirst();
                }
                while(rs.next()){
                    objMerc = new TablaMercancias();
                    
                    objMerc.setTotMerc(String.valueOf(totalMerc));
                    objMerc.setClaveProd((rs.getString(1) != null && rs.getString(1).length() > 0 ? rs.getString(1) : ""));
                    objMerc.setDescProd((rs.getString(2) != null && rs.getString(2).length() > 0 ? rs.getString(2) : ""));
                    objMerc.setCantidad((rs.getString(3) != null && rs.getString(3).length() > 0 ? rs.getString(3) : ""));
                    objMerc.setClaveUnidad((rs.getString(4) != null && rs.getString(4).length() > 0 ? rs.getString(4) : ""));
                    objMerc.setDescEmbalaje((rs.getString(5) != null && rs.getString(5).length() > 0 ? rs.getString(5) : ""));
                    objMerc.setValorMerc((rs.getString(6) != null && rs.getString(6).length() > 0 ? rs.getString(6) : ""));
                    objMerc.setPeso((rs.getString(7) != null && rs.getString(7).length() > 0 ? rs.getString(7) : ""));
                    objMerc.setClaveMP((rs.getString(8) != null && rs.getString(8).length() > 0 ? rs.getString(8) : ""));
                    objMerc.setMP((rs.getString(9) != null && rs.getString(9).length() > 0 ? rs.getString(9) : ""));
                    objMerc.setTipoEmbalaje((rs.getString(10) != null && rs.getString(10).length() > 0 ? rs.getString(10) : ""));
                    objMerc.setDescUnidad((rs.getString(11) != null && rs.getString(11).length() > 0 ? rs.getString(11) : ""));
                    
                    listMercancias.add(objMerc);
                }
            }
        } catch (SQLException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, this.lastQuery, Thread.currentThread().getStackTrace());
            
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }    
        return listMercancias;
    }
    
    public List <TablaUnidades> getUnidades(String cla_talon){
        List <TablaUnidades> listUnidades = new ArrayList <>();
        
        TablaUnidades objUnidades;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        
        try {
            String consultaCadena = "SELECT cons_ccp FROM cfdinomina.complementocp_consecutivo WHERE cla_talon = '" + cla_talon + "' ORDER BY cons_ccp DESC LIMIT 1;";
            int consecutivo = 0;
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                consecutivo = rs.getInt(1);
            }
            if(consecutivo > 0){
                consultaCadena = "SELECT i.configvehicular, i.placavm, i.aniomodelovm, " +
                                        "r.descripcion, r.subtiporem, r.placa, " +
                                        "o.rfcoperador, o.nombreoperador, o.numlicencia " +
                                 "FROM cfdinomina.complementocp_identificacionvehicular i " +
                                 "INNER JOIN cfdinomina.complementocp_remolque r ON r.idper_fac = i.idper_fac " +
                                        "AND r.cons_ccp = i.cons_ccp " +
                                 "INNER JOIN cfdinomina.complementocp_operador o ON o.idper_fac = i.idper_fac " +
                                        "AND o.cons_ccp = i.cons_ccp " +
                                 "WHERE i.idper_fac = '" + cla_talon + "' AND i.cons_ccp = " + consecutivo + ";"; 
                ps = conn.prepareStatement(consultaCadena);
                rs = ps.executeQuery();
                
                while(rs.next()){
                    objUnidades = new TablaUnidades();
                    
                    objUnidades.setAutotransporte(rs.getString(1));
                    objUnidades.setPlacaUnidad(rs.getString(2));
                    objUnidades.setAnioModelo(rs.getString(3));
                    objUnidades.setRemolque(rs.getString(4));
                    objUnidades.setTipoRemolque(rs.getString(5));
                    objUnidades.setPlacaRemolque(rs.getString(6));
                    objUnidades.setRfcOperador(rs.getString(7));
                    objUnidades.setNombreOperador(rs.getString(8));
                    objUnidades.setNoLic(rs.getString(9));
                    
                    listUnidades.add(objUnidades);
                }
            }
        } catch (SQLException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, this.lastQuery, Thread.currentThread().getStackTrace());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }    
        return listUnidades;
    }
    
    public List <TablaAutoTransporte> getAutotransporte(String cla_talon){
        List <TablaAutoTransporte> listAutoTransporte = new ArrayList <>();
        
        TablaAutoTransporte objAutoTransporte;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        
        try {
            String consultaCadena = "SELECT cons_ccp FROM cfdinomina.complementocp_consecutivo WHERE cla_talon = '" + cla_talon + "' ORDER BY cons_ccp DESC LIMIT 1;";
            int consecutivo = 0;
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                consecutivo = rs.getInt(1);
            }
            if(consecutivo > 0){
                consultaCadena = "SELECT '01' AS cveTransporte, c.permsct, " +
                                         "c.numpermisosct, c.nombreaseg, c.numpolizaseguro " +
                                 "FROM cfdinomina.complementocp c " +
                                 "WHERE c.idper_fac = '" + cla_talon + "' AND c.cons_ccp = " + consecutivo + ";"; 
                ps = conn.prepareStatement(consultaCadena);
                rs = ps.executeQuery();
                
                while(rs.next()){
                    objAutoTransporte = new TablaAutoTransporte();
                    
                    objAutoTransporte.setCveTransporte(rs.getString(1));
                    objAutoTransporte.setClavePermiso(rs.getString(2));
                    objAutoTransporte.setNoPermiso(rs.getString(3));
                    objAutoTransporte.setNombreAseguradora(rs.getString(4));
                    objAutoTransporte.setPolizaSeguro(rs.getString(5));
                    
                    listAutoTransporte.add(objAutoTransporte);
                }
            }
        } catch (SQLException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, this.lastQuery, Thread.currentThread().getStackTrace());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }    
        return listAutoTransporte;
    }
    
    public String getAbreviacionCiudad(int idCiudad){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        String respuesta = "";
        try {
            String consultaCadena = "select ciudades.nombre, estados.abreviacion " +
                                    "from camiones.ciudades, camiones.estados " +
                                    "where ciudades.idestado = estados.idestado " +
                                    "and ciudades.idciudad = "+ idCiudad +";";
            
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta = rs.getString(1) + ", " + rs.getString(2);
            }
        } catch (SQLException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, this.lastQuery, Thread.currentThread().getStackTrace());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }
        return respuesta;
    }
    
    public String getXML(String cla_talon){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        String respuesta = "";
        int consecutivo = 0;
        
        try {
            String consultaCadena = "SELECT cons_ccp FROM cfdinomina.complementocp_consecutivo WHERE cla_talon = '" + cla_talon + "' ORDER BY cons_ccp DESC LIMIT 1;";
            //String consultaCadena = "SELECT foliosat FROM talones.documentoscfdi WHERE iddocumento = '"+ cla_talon +"';";
            
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                consecutivo = rs.getInt(1);
            }
            consultaCadena = "SELECT xml FROM cfdinomina.complementocp_xml WHERE cla_talon = '"+ cla_talon +"' AND cons_ccp = "+consecutivo+";";
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta = rs.getString(1);
            }
            if(respuesta.equals("")){
                consultaCadena = "SELECT xml FROM talones.documentoscfdi WHERE iddocumento = '"+ cla_talon +"';";
                ps = conn.prepareStatement(consultaCadena);
                rs = ps.executeQuery();
                if (rs.next()) {
                    respuesta = rs.getString(1);
                }
            }
        } catch (SQLException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, this.lastQuery, Thread.currentThread().getStackTrace());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }
        return respuesta;
    }
    
    // SELECT CONCAT_WS(' ', p.`nombre`, p.`apepaterno`, p.`apematerno`) AS nombre FROM personal.`personal` p WHERE p.`idpersonal` = 38707;
    public String getNombrePersonal(int idPersonal){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        String respuesta = "";
        try {
            String consultaCadena = "SELECT CONCAT_WS(' ', nombre, apepaterno, apematerno) AS nombre "
                                  + "FROM personal.personal WHERE idpersonal = '"+idPersonal+"';";
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta = rs.getString(1);
            }
        } catch (SQLException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, this.lastQuery, Thread.currentThread().getStackTrace());
            
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }
        return respuesta;
    }
    
    public ArrayList<String> getPedimentoCartaPorte(String cla_talon){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        ArrayList<String> respuesta = new ArrayList<>();
        String transInter = "";
        String numPedimento = "";
        String tipo = "";
        
        try {
            String consultaCadena = "SELECT cons_ccp FROM cfdinomina.complementocp_consecutivo WHERE cla_talon = '" + cla_talon + "' ORDER BY cons_ccp DESC LIMIT 1;";
            int consecutivo = 0;
            
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                consecutivo = rs.getInt(1);
            }
            if(consecutivo > 0){
                consultaCadena = "SELECT ccp.transinternac, ci.tipo, " +
                                         "CONCAT_WS('  ', ci.anio, ci.aduana,ci.patente, ci.cantidad) AS pedimento " +
                                 "FROM cfdinomina.complementocp ccp " +
                                 "LEFT JOIN cfdinomina.complementocp_internacional ci " +
                                       " ON ccp.idper_fac =  ci.idper_fac AND ccp.cons_ccp = ci.cons_ccp " +
                                 "WHERE ccp.idper_fac = '"+cla_talon+"' AND ccp.cons_ccp = "+ consecutivo +";";
            }
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                transInter = (rs.getString(1) != null && rs.getString(1).length() > 0 ? rs.getString(1) : "");
                tipo = (rs.getString(2) != null && rs.getString(2).length() > 0 ? rs.getString(2) : "");
                numPedimento = (rs.getString(3) != null && rs.getString(3).length() > 0 ? rs.getString(3) : "");
            }
        } catch (SQLException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, this.lastQuery, Thread.currentThread().getStackTrace());
            
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }
        
        respuesta.add(transInter);
        respuesta.add(tipo);
        respuesta.add(numPedimento);
        return respuesta;
    }
    
    public String getFolioFiscal(String cla_talon){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        String respuesta = "";
        try {
            String consultaCadena = "SELECT foliosat FROM talones.documentoscfdi WHERE iddocumento = '"+ cla_talon +"';";
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta = rs.getString(1);
            }
        } catch (SQLException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, this.lastQuery, Thread.currentThread().getStackTrace());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }
        return respuesta;
    }
    
    public String getTelOficina(int idciudad){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        String idoficina = "";
        String respuesta = "";
        try {
            String consultaCadena = "SELECT idoficina FROM camiones.ciudadsucursal2009 WHERE idciudad = '"+ idciudad +"';";
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                idoficina = rs.getString(1);
            }
            if(idoficina.length()>0){
                consultaCadena = "SELECT concat_ws('-', o.prefijo, t.telefono) FROM personal.oficinas o " +
                                 "INNER JOIN telefonia.telsoficina t ON o.idoficina = t.idoficina " +
                                 "WHERE o.idoficina = " + idoficina + " limit 1";
                ps = conn.prepareStatement(consultaCadena);
                rs = ps.executeQuery();
                if (rs.next()) {
                    respuesta = rs.getString(1);
                }
            }
        } catch (SQLException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, this.lastQuery, Thread.currentThread().getStackTrace());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }
        return respuesta;
    }
    
    public ArrayList<String> getTablaContiene(String cla_talon, String tabla){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        ArrayList<String> respuesta = new ArrayList<>();
        String cantidad = "";
        String empaque = "";
        String queContiene = "";
        String pesoT = "";
        String mts3 = "";
        
        try {
            String consultaCadena = "select * from talones.co"+ tabla +" where cla_talon = '"+ cla_talon +"';";
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            while (rs.next()) {
                /*if(respuesta.length()>0){
                    respuesta = "\n"+ respuesta + rs.getString(2); 
                }else{*/
                    
                //}
                cantidad = cantidad + rs.getString(2)+"\n";
                empaque = empaque + rs.getString(4).substring(0, 9)+"\n";
                queContiene = queContiene + rs.getString(5)+"\n";
                pesoT = pesoT + rs.getString(6)+"\n";
                mts3 = mts3 + rs.getString(7)+"\n";
            }
        } catch (SQLException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, this.lastQuery, Thread.currentThread().getStackTrace());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }
        respuesta.add(cantidad);
        respuesta.add(empaque);
        respuesta.add(queContiene);
        respuesta.add(pesoT);
        respuesta.add(mts3);
        return respuesta;
    }
    
    public String getPedimento(String cla_talon, String tabla){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        String respuesta = "";
        try {
            String consultaCadena = "SELECT foliosat FROM talones.documentoscfdi WHERE iddocumento = '"+ cla_talon +"';";
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta = rs.getString(1);
            }
        } catch (SQLException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, this.lastQuery, Thread.currentThread().getStackTrace());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }
        return respuesta;
    }
    
    public String getClaveEncriptada(String cla_talon){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        String respuesta = "";
        try {
            String consultaCadena = "SELECT clave FROM talones.claves_talones WHERE cla_talon = '"+ cla_talon +"';";
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta = rs.getString(1);
            }
        } catch (SQLException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, this.lastQuery, Thread.currentThread().getStackTrace());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }
        return respuesta;
    }
    
    public String getEvidencias(String cla_talon, String tabla){
        Connection conex = persistencia.connect(server);

        String respuesta = "";
        try {
            String cadenaConsulta = " Select * from ev" +  tabla + " where cla_talon = '" + cla_talon + "'";

            PreparedStatement pStat = conex.prepareStatement(cadenaConsulta);
            ResultSet rs = pStat.executeQuery();

            if (rs.first()) {
                respuesta = rs.getString("idper_fac");
            } else {
                respuesta = "1";
            }

            DBUtils.close(pStat);
        } catch (SQLException ex) {
            respuesta = ex.toString();
            Logger.getLogger(TalonesCustomDao.class.getName()).log(Level.SEVERE, null, ex);
            
        } finally {
            DBUtils.close(conex);
        }
        return respuesta;
    }
    
    /*Admon Ocurre, Admon Fin, Admon EyE, Admon Traslado Cedis*/
    public ArrayList<String> getImportes(String cla_talon, String tabla){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        ArrayList<String> respuesta = new ArrayList<>();
        String admonOcurre = "0.00";
        String admonFin = "0.00";
        String eye = "0.00";
        String trasladoCedis = "0.00";
        try {
            String consultaCadena = "SELECT importe FROM talones.catalogo_concepto_importe"+tabla
                                  +" WHERE cla_talon = '"+ cla_talon +"' AND idconcepto = 50;";
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                admonOcurre = rs.getString(1);
            }
            consultaCadena = "SELECT importe FROM talones.catalogo_concepto_importe"+tabla
                                  +" WHERE cla_talon = '"+ cla_talon +"' AND idconcepto = 51;";
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                admonFin = rs.getString(1);
            }
            consultaCadena = "SELECT importe FROM talones.catalogo_concepto_importe"+tabla
                                  +" WHERE cla_talon = '"+ cla_talon +"' AND idconcepto = 53;";
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                eye = rs.getString(1);
            }
            consultaCadena = "SELECT importe FROM talones.catalogo_concepto_importe"+tabla
                                  +" WHERE cla_talon = '"+ cla_talon +"' AND idconcepto = 54;";
            ps = conn.prepareStatement(consultaCadena);
            rs = ps.executeQuery();
            if (rs.next()) {
                trasladoCedis = rs.getString(1);
            }
            
        } catch (SQLException ex) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, ex, this.lastQuery, Thread.currentThread().getStackTrace());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }
        respuesta.add(admonOcurre);
        respuesta.add(admonFin);
        respuesta.add(eye);
        respuesta.add(trasladoCedis);
        
        return respuesta;
    }
    
    /*La estructura de la tabla talones.talones es distinta de la 192.168.0.23 a la de las oficinas*/
    public Talones getTalon(String cla_talon){
        Connection conex = persistencia.connect(server);
        Talones talon = null;
        try {
            String cadenaConsulta = "SELECT * FROM talones.talones WHERE cla_talon = '" + cla_talon + "'";

            PreparedStatement pStat = conex.prepareStatement(cadenaConsulta);
            ResultSet rs = pStat.executeQuery();
            if (rs.first()) {
                talon = new Talones();
                talon.setCla_talon(rs.getString("cla_talon"));
                talon.setIdoficina(rs.getString("idoficina"));
                talon.setTabla(rs.getString("tabla"));
                talon.setStatus(rs.getInt("status"));
                talon.setRemision(rs.getString("remision"));
                talon.setSerie(rs.getString("serie"));
                talon.setManiobras(rs.getDouble("maniobras"));
            }

            DBUtils.close(pStat);
        } catch (SQLException ex) {
            Logger.getLogger(TalonesCustomDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(conex);
        }
        return talon;
    }
}
