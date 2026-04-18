package co.edu.com.inrf;

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
