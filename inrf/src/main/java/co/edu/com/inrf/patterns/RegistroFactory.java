package co.edu.com.inrf.patterns;

import co.edu.com.inrf.model.Diagnostico;
import co.edu.com.inrf.model.RegistroClinico;
import co.edu.com.inrf.model.SesionTerapia;

public class RegistroFactory {
    public static RegistroClinico crearRegistro(String tipo, String contenido){
        if(tipo.equalsIgnoreCase("DIAGNOSTICO"))
            return new Diagnostico(contenido);
        else if(tipo.equalsIgnoreCase("SESION"))
            return new SesionTerapia(contenido);
        else
            throw new IllegalArgumentException("Tipo de registro no válido");
    }
}
