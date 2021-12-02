package mx.com.castores.Models;

public class TablaUbicaciones {
    private String tipo;
    private String calle;
    private String colonia;
    private String cp;
    private String localidad;
    private String municipio;
    private String estado;
    private String pais;

    public TablaUbicaciones() {
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
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

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
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
        return "TablaUbicaciones{" + "tipo=" + tipo + ", calle=" + calle + ", colonia=" + colonia + ", cp=" + cp + ", localidad=" + localidad + ", municipio=" + municipio + ", estado=" + estado + ", pais=" + pais + '}';
    }
}
