package co.edu.com.inrf.model;

public class Informe implements DocumentoClinico{
    private String evolucion;

    public Informe(String evolucion){
        this.evolucion = evolucion;
    }

    @Override
    public String getContenido() {
        return "Informe de evolución: " + evolucion;
    }
}
