package mx.com.castores.Models;

public class TablaConexiones {
    private String tipo;
    private String calle;
    private String estado;
    private String pais;
    private String cp;

    public TablaConexiones() {
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "TablaConexiones{" + "tipo=" + tipo + ", calle=" + calle + ", estado=" + estado + ", pais=" + pais + ", cp=" + cp + '}';
    }   
}
