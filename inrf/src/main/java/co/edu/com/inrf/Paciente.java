package co.edu.com.inrf;

import java.util.ArrayList;
import java.util.List;

public class Paciente {
    private String id;
    private String nombre;
    private String planTratamiento;
    public List<RegistroClinico> historiaClinica;
    private List<Observador> observadores; 
    
    public Paciente(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.historiaClinica = new ArrayList<>();
        this.observadores = new ArrayList<>();
        this.observadores.add(new PacienteObservador(nombre));
    }
    public String getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getPlanTratamiento() {
        return planTratamiento != null ? planTratamiento : "No asignado";
    }
    public void setPlanTratamiento(String planTratamiento) {
        this.planTratamiento = planTratamiento;
        notificar("Plan de tratamiento actualizado: " + planTratamiento);
    }
    public void agregarRegistro(RegistroClinico registro) {
        historiaClinica.add(registro);
        notificar("Nuevo registro clínico agregado: " + registro.getDetalle());
    }
    public List<RegistroClinico> getHistoriaClinica() {
        return historiaClinica;
    }
    public void notificar(String mensaje){
        for(Observador obs : observadores){
            obs.actualizar(mensaje);
        }
    }
}
