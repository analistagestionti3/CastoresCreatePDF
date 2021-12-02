package mx.com.castores.CustomDao;

import castores.core.Persistencia;
import castores.core.PreparedParams;
import castores.dao.cfdinomina.Complementocp_parametrosDao;
import com.castores.datautilsapi.db.DBUtils;
import com.castores.datautilsapi.log.LoggerUtils;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author Windows
 */
public class GlobalCustomDao {

    protected Persistencia persistencia;
    protected String server;
    protected String server23;
    protected String server13;
    protected String lastQuery = "";

    @Inject
    public GlobalCustomDao(Persistencia persistencia, @Named("Server13") String server13, @Named("Server23") String server23) {
        this.persistencia = persistencia;
        this.server = server13;
        this.server23 = server23;
        this.server13 = server13;
    }

    public String getLastQuery() {
        return this.lastQuery;
    }

    public List<Map> getListaPersonalizada(String sql) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        ArrayList lstgeneral = new ArrayList();

        try {
            ps = conn.prepareStatement(sql);
            new PreparedParams(ps);
            rs = ps.executeQuery();

            while (rs.next()) {
                Map r = new HashMap();

                for (int i = 0; i < rs.getMetaData().getColumnCount(); ++i) {
                    r.put(rs.getMetaData().getColumnLabel(i + 1), rs.getString(i + 1));
                }

                lstgeneral.add(r);
            }
        } catch (Exception var12) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, var12, this.lastQuery, Thread.currentThread().getStackTrace());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }

        return lstgeneral;
    }

    public Map getobjetopersonalizado(String sql) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        HashMap objGeneral = new HashMap();

        try {
            ps = conn.prepareStatement(sql);
            new PreparedParams(ps);
            rs = ps.executeQuery();
            if (rs.next()) {
                for (int i = 0; i < rs.getMetaData().getColumnCount(); ++i) {
                    objGeneral.put(rs.getMetaData().getColumnLabel(i + 1), rs.getString(i + 1));
                }
            }
        } catch (Exception var11) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, var11, this.lastQuery, Thread.currentThread().getStackTrace());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }

        return objGeneral;
    }

    public String regresaCampo(String camposelect, String tablabd, String campocondicion, String valorcondicion) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);
        String resultado = "";

        try {
            ps = conn.prepareStatement("SELECT " + camposelect + " FROM " + tablabd + " WHERE " + campocondicion + " = " + valorcondicion);
            
            rs = ps.executeQuery();
            if (rs.next()) {
                resultado = rs.getString(1);
            }
        } catch (Exception var13) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, var13, this.lastQuery, Thread.currentThread().getStackTrace());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }

        return resultado;
    }

    public Date getCurrentDate() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);

        Timestamp o;
        try {
            Timestamp var5;
            try {
                ps = conn.prepareStatement("SELECT CURRENT_TIMESTAMP");
                rs = ps.executeQuery();
                if (rs.next()) {
                    o = rs.getTimestamp(1);
                    var5 = o;
                    return var5;
                }

                o = null;
            } catch (SQLException var9) {
                LoggerUtils.printLog(this.getClass(), Level.SEVERE, var9, "SELECT CURRENT_TIMESTAMP", Thread.currentThread().getStackTrace());
                var5 = null;
                return var5;
            }
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }

        return o;
    }

    public boolean ejecutaInstruccion(String sql) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);

        boolean var6;
        try {
            ps = conn.prepareStatement(sql);
            new PreparedParams(ps);
            ps.executeUpdate();
            var6 = true;
            return var6;
        } catch (Exception var10) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, var10, this.lastQuery, Thread.currentThread().getStackTrace());
            var6 = false;
        } finally {
            DBUtils.close(ps);
            DBUtils.close((ResultSet) rs);
            DBUtils.close(conn);
        }

        return var6;
    }

    public void cambiaPersistencia(String servidor) {
        if (servidor.equals("Server13")) {
            this.server = this.server13;
        }

        if (servidor.equals("Server23")) {
            this.server = this.server23;
        }

    }

    public Map ejecutaSP(String sql, String rec) {
        Map res = new HashMap();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = this.persistencia.connect(this.server);

        try {
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            sql = "select @" + rec;
            rs = ps.executeQuery(sql);
            if (rs.first()) {
                res.put(rec, rs.getString("@" + rec));
            }
        } catch (Exception var11) {
            System.out.println("Error al ejecutar el SP: " + var11.getMessage());
        } finally {
            DBUtils.close(ps);
            DBUtils.close(rs);
            DBUtils.close(conn);
        }

        return res;
    }

    public Map cargarTablaEnMemoria(String[] campos, String base_datos_tabla) {
        Map listaRegistros = new HashMap();
        String select_campos = "";
        Connection conex = null;
        Statement inst = null;
        ResultSet rs = null;

        try {
            for (int i = 0; i < campos.length; ++i) {
                select_campos = select_campos + campos[i];
                if (i != campos.length - 1) {
                    select_campos = select_campos + ",";
                }
            }

            String sql = " SELECT " + select_campos + " FROM " + base_datos_tabla;
            conex = this.persistencia.connect(this.server);
            inst = conex.createStatement();
            rs = inst.executeQuery(sql);

            while (rs.next()) {
                Map row = new HashMap();

                for (int i = 0; i < campos.length; ++i) {
                    row.put(campos[i], rs.getString(campos[i]));
                }

                listaRegistros.put(rs.getString(campos[0]), row);
            }

            System.out.println("query" + sql);
        } catch (Exception var14) {
            LoggerUtils.printLog(this.getClass(), Level.SEVERE, var14, this.lastQuery, Thread.currentThread().getStackTrace());
            listaRegistros = null;
        } finally {
            DBUtils.close(inst);
            DBUtils.close(rs);
            DBUtils.close(conex);
            return listaRegistros;

        }
    }
}
