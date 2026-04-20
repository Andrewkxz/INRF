package co.edu.com.inrf.model;

public class SesionTerapia implements RegistroClinico{
    private String evolucion;

    public SesionTerapia(String evolucion){
        this.evolucion = evolucion;
    }

    @Override
    public String getDetalle() {
        return "Evolución de la sesión: " + evolucion;
    }
}
