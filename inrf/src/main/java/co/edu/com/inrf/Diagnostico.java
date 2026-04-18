package co.edu.com.inrf;

public class Diagnostico implements RegistroClinico{
    private String descripcion;

    public Diagnostico(String descripcion){
        this.descripcion = descripcion;
    }

    @Override
    public String getDetalle() {
        return "Diagnóstico inicial: " + descripcion;
    }
}
