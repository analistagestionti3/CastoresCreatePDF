package mx.com.castores.Models;

public class TablaMercancias {
    private String totMerc;
    private String claveProd;
    private String descProd;
    private String cantidad;
    private String claveUnidad;
    private String descUnidad;
    private String valorMerc;
    private String peso;
    private String claveMP;
    private String MP;// SÍ o NO
    private String tipoEmbalaje;
    private String descEmbalaje;

    public TablaMercancias() {
    }

    public String getMP() {
        return MP;
    }

    public void setMP(String MP) {
        this.MP = MP;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getClaveMP() {
        return claveMP;
    }

    public void setClaveMP(String claveMP) {
        this.claveMP = claveMP;
    }

    public String getClaveProd() {
        return claveProd;
    }

    public void setClaveProd(String claveProd) {
        this.claveProd = claveProd;
    }

    public String getClaveUnidad() {
        return claveUnidad;
    }

    public void setClaveUnidad(String claveUnidad) {
        this.claveUnidad = claveUnidad;
    }

    public String getDescEmbalaje() {
        return descEmbalaje;
    }

    public void setDescEmbalaje(String descEmbalaje) {
        this.descEmbalaje = descEmbalaje;
    }

    public String getDescProd() {
        return descProd;
    }

    public void setDescProd(String descProd) {
        this.descProd = descProd;
    }

    public String getDescUnidad() {
        return descUnidad;
    }

    public void setDescUnidad(String descUnidad) {
        this.descUnidad = descUnidad;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getTipoEmbalaje() {
        return tipoEmbalaje;
    }

    public void setTipoEmbalaje(String tipoEmbalaje) {
        this.tipoEmbalaje = tipoEmbalaje;
    }

    public String getTotMerc() {
        return totMerc;
    }

    public void setTotMerc(String totMerc) {
        this.totMerc = totMerc;
    }

    public String getValorMerc() {
        return valorMerc;
    }

    public void setValorMerc(String valorMerc) {
        this.valorMerc = valorMerc;
    }

    @Override
    public String toString() {
        return "TablaMercancias{" + "totMerc=" + totMerc + ", claveProd=" + claveProd + ", descProd=" + descProd + ", cantidad=" + cantidad + ", claveUnidad=" + claveUnidad + ", descUnidad=" + descUnidad + ", valorMerc=" + valorMerc + ", peso=" + peso + ", claveMP=" + claveMP + ", MP=" + MP + ", tipoEmbalaje=" + tipoEmbalaje + ", descEmbalaje=" + descEmbalaje + '}';
    }
}
