package mx.com.castores.Models;

public class TablaUnidades {
    private String autotransporte;
    private String placaUnidad;
    private String anioModelo;
    private String remolque;
    private String tipoRemolque;
    private String placaRemolque;
    private String rfcOperador;
    private String nombreOperador;
    private String noLic;

    public TablaUnidades() {
    }

    public String getAnioModelo() {
        return anioModelo;
    }

    public void setAnioModelo(String anioModelo) {
        this.anioModelo = anioModelo;
    }

    public String getAutotransporte() {
        return autotransporte;
    }

    public void setAutotransporte(String autotransporte) {
        this.autotransporte = autotransporte;
    }

    public String getNoLic() {
        return noLic;
    }

    public void setNoLic(String noLic) {
        this.noLic = noLic;
    }

    public String getNombreOperador() {
        return nombreOperador;
    }

    public void setNombreOperador(String nombreOperador) {
        this.nombreOperador = nombreOperador;
    }

    public String getPlacaRemolque() {
        return placaRemolque;
    }

    public void setPlacaRemolque(String placaRemolque) {
        this.placaRemolque = placaRemolque;
    }

    public String getPlacaUnidad() {
        return placaUnidad;
    }

    public void setPlacaUnidad(String placaUnidad) {
        this.placaUnidad = placaUnidad;
    }

    public String getRemolque() {
        return remolque;
    }

    public void setRemolque(String remolque) {
        this.remolque = remolque;
    }

    public String getRfcOperador() {
        return rfcOperador;
    }

    public void setRfcOperador(String rfcOperador) {
        this.rfcOperador = rfcOperador;
    }

    public String getTipoRemolque() {
        return tipoRemolque;
    }

    public void setTipoRemolque(String tipoRemolque) {
        this.tipoRemolque = tipoRemolque;
    }

    @Override
    public String toString() {
        return "TablaVehicular{" + "autotransporte=" + autotransporte + ", placaUnidad=" + placaUnidad + ", anioModelo=" + anioModelo + ", remolque=" + remolque + ", tipoRemolque=" + tipoRemolque + ", placaRemolque=" + placaRemolque + ", rfcOperador=" + rfcOperador + ", nombreOperador=" + nombreOperador + ", noLic=" + noLic + '}';
    }
}
