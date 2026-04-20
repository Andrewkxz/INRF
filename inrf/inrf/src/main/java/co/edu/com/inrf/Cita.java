package co.edu.com.inrf;

public class Cita {
    private String idCita;
    private String fecha;
    private String idPaciente;
    private String medico;
    private boolean activa;

    public Cita(String idCita, String fecha, String idPaciente, String medico) {
        this.idCita = idCita;
        this.fecha = fecha;
        this.idPaciente = idPaciente;
        this.medico = medico;
        this.activa = true;
    }

    public String getIdCita() {
        return idCita;
    }

    public String getIdPaciente() {
        return idPaciente;
    }

    public boolean isActiva() {
        return activa;
    }

    public void cancelar() {
        this.activa = false;
        System.out.println("La cita " + idCita + " ha sido cancelada.");
    }

    @Override
    public String toString() {
        return "Cita [" + idCita + "] - Fecha: " + fecha + " - Médico: " + medico + " - Estado: " + (activa ? "Activa" : "Cancelada");
    }
}
