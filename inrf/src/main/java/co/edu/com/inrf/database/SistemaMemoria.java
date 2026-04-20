package co.edu.com.inrf.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.edu.com.inrf.model.Cita;
import co.edu.com.inrf.model.Paciente;

public class SistemaMemoria {
    private static SistemaMemoria instancia;
    private HashMap<String, Paciente> pacientes;
    private List<Cita> citas;

    private SistemaMemoria() {
        pacientes = new HashMap<>();
        citas = new ArrayList<>();
    }
    public static SistemaMemoria getInstancia() {
        if (instancia == null) {
            instancia = new SistemaMemoria();
        }
        return instancia;
    }
    public HashMap<String, Paciente> getPacientes() {
        return pacientes;
    }
    public List<Cita> getCitas() {
        return citas;
    }
}
