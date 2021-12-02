package mx.com.castores.Models;

public class TablaAutoTransporte {
    private String cveTransporte;
    private String clavePermiso;
    private String noPermiso;
    private String nombreAseguradora;
    private String polizaSeguro;

    public TablaAutoTransporte() {
    }

    public String getClavePermiso() {
        return clavePermiso;
    }

    public void setClavePermiso(String clavePermiso) {
        this.clavePermiso = clavePermiso;
    }

    public String getCveTransporte() {
        return cveTransporte;
    }

    public void setCveTransporte(String cveTransporte) {
        this.cveTransporte = cveTransporte;
    }

    public String getNoPermiso() {
        return noPermiso;
    }

    public void setNoPermiso(String noPermiso) {
        this.noPermiso = noPermiso;
    }

    public String getNombreAseguradora() {
        return nombreAseguradora;
    }

    public void setNombreAseguradora(String nombreAseguradora) {
        this.nombreAseguradora = nombreAseguradora;
    }

    public String getPolizaSeguro() {
        return polizaSeguro;
    }

    public void setPolizaSeguro(String polizaSeguro) {
        this.polizaSeguro = polizaSeguro;
    }

    @Override
    public String toString() {
        return "TablaAutoTransporte{" + "cveTransporte=" + cveTransporte + ", clavePermiso=" + clavePermiso + ", noPermiso=" + noPermiso + ", nombreAseguradora=" + nombreAseguradora + ", polizaSeguro=" + polizaSeguro + '}';
    }
}
