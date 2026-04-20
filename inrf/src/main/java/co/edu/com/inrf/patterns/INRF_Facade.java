package co.edu.com.inrf.patterns;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;

import co.edu.com.inrf.database.SistemaMemoria;
import co.edu.com.inrf.model.Cita;
import co.edu.com.inrf.model.Paciente;
import co.edu.com.inrf.model.RegistroClinico;

public class INRF_Facade {
    private SistemaMemoria db = SistemaMemoria.getInstancia();

    public void registrarPaciente(String id, String nombre) {
        if (!db.getPacientes().containsKey(id)) {
            db.getPacientes().put(id, new Paciente(id, nombre));
            System.out.println("-> ÉXITO: Paciente registrado. Historia clínica aperturada.");
        } else System.out.println("-> ERROR: El paciente ya existe.");
    }

    public void actualizarDatos(String id, String nuevoNombre) {
        Paciente p = db.getPacientes().get(id);
        if (p != null) { p.setNombre(nuevoNombre); System.out.println("-> ÉXITO: Datos actualizados."); }
        else System.out.println("-> ERROR: Paciente no encontrado.");
    }

    public void registrarDiagnostico(String id, String diagnostico, String plan) {
        Paciente p = db.getPacientes().get(id);
        if (p != null) {
            p.agregarRegistro(RegistroFactory.crearRegistro("DIAGNOSTICO", diagnostico));
            p.setPlanTratamiento(plan);
            System.out.println("-> ÉXITO: Diagnóstico registrado.");
        } else System.out.println("-> ERROR: Paciente no encontrado.");
    }

    public void registrarSesion(String id, String evolucion) {
        Paciente p = db.getPacientes().get(id);
        if (p != null) {
            p.agregarRegistro(RegistroFactory.crearRegistro("SESION", evolucion));
            System.out.println("-> ÉXITO: Evolución registrada.");
        } else System.out.println("-> ERROR: Paciente no encontrado.");
    }

    public void consultarEvolucion(String id) {
        Paciente p = db.getPacientes().get(id);
        if (p != null) {
            System.out.println("\n--- HISTORIAL: " + p.getNombre() + " ---");
            if (p.getHistoriaClinica().isEmpty()) System.out.println("Sin registros aún.");
            p.getHistoriaClinica().forEach(rc -> System.out.println("- " + rc.getDetalle()));
        } else System.out.println("-> ERROR: Paciente no encontrado.");
    }

    // RF-06: Generar Informe (Exportar a Archivo)
    public void generarInformeDocumento(String id) {
        Paciente p = db.getPacientes().get(id);
        Locale localeEs = new Locale("es", "ES");
        DateFormat formato = DateFormat.getDateInstance(DateFormat.FULL, localeEs);
        java.util.Date fecha = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        String fechaFormateada = formato.format(fecha);
        if (p != null) {
            String nombreArchivo = "Informe_Clinico_" + id + ".txt";
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(nombreArchivo), "UTF-8"))) {
                pw.println("======================================================");
                pw.println("      INSTITUTO NACIONAL DE REHABILITACIÓN (INRF)     ");
                pw.println("                INFORME CLÍNICO OFICIAL               ");
                pw.println("======================================================");
                pw.println("Fecha de Expedición: " + fechaFormateada);
                pw.println("Documento ID Paciente: " + p.getId());
                pw.println("Nombre del Paciente: " + p.getNombre());
                pw.println("------------------------------------------------------");
                pw.println("PLAN DE TRATAMIENTO ASIGNADO:");
                pw.println(p.getPlanTratamiento());
                pw.println("------------------------------------------------------");
                pw.println("REGISTRO HISTÓRICO DE DIAGNÓSTICOS Y SESIONES:");
                if (p.getHistoriaClinica().isEmpty()) {
                    pw.println("No hay registros clínicos en el sistema.");
                } else {
                    for (RegistroClinico rc : p.getHistoriaClinica()) {
                        pw.println(">> " + rc.getDetalle());
                    }
                }
                pw.println("======================================================");
                pw.println("Firma Digital Sistema INRF");
                
                System.out.println("-> ÉXITO: Informe generado y guardado como '" + nombreArchivo + "'.");
            } catch (IOException e) {
                System.out.println("-> ERROR al generar el documento: " + e.getMessage());
            }
        } else System.out.println("-> ERROR: Paciente no encontrado.");
    }

    public void programarCita(String idCita, String fecha, String idPaciente, String medico) {
        if (db.getPacientes().containsKey(idPaciente)) {
            db.getCitas().add(new Cita(idCita, fecha, idPaciente, medico));
            System.out.println("-> ÉXITO: Cita programada.");
        } else System.out.println("-> ERROR: Paciente no registrado.");
    }

    public void consultarCitas(String idPaciente) {
        System.out.println("\n--- AGENDA ---");
        boolean tiene = false;
        for (Cita c : db.getCitas()) {
            if (c.getIdPaciente().equals(idPaciente)) { System.out.println(c.toString()); tiene = true; }
        }
        if (!tiene) System.out.println("No hay citas.");
    }

    public void cancelarCita(String idCita) {
        for (Cita c : db.getCitas()) {
            if (c.getIdCita().equals(idCita) && c.isActiva()) {
                c.cancelar();
                Paciente p = db.getPacientes().get(c.getIdPaciente());
                System.out.println("-> ÉXITO: Cita cancelada.");
                if (p != null) p.notificar("Su cita " + idCita + " ha sido CANCELADA.");
                return;
            }
        }
        System.out.println("-> ERROR: Cita no encontrada o ya cancelada.");
    }

    public void consultarTratamientoCuidador(String id) {
        Paciente p = db.getPacientes().get(id);
        if (p != null) {
            System.out.println("\n--- VISTA CUIDADOR ---");
            System.out.println("Paciente: " + p.getNombre() + " | Estado/Plan: " + p.getPlanTratamiento());
        } else System.out.println("-> ERROR: Paciente no encontrado.");
    }
}
