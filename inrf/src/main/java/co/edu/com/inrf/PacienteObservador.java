package co.edu.com.inrf;

public class PacienteObservador implements Observador{
    private String nombre;

    public PacienteObservador(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void actualizar(String mensaje) {
        System.out.println("[NOTIFICACIÓN a " + nombre + "]: " + mensaje);
    }
}
