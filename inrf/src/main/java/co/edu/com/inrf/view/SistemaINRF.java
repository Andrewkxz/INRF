package co.edu.com.inrf.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import co.edu.com.inrf.database.SistemaMemoria;
import co.edu.com.inrf.patterns.INRF_Facade;

// ==========================================
// 6. CONTROLADOR DE LOGIN Y MENÚS
// ==========================================
public class SistemaINRF {
    private static final String CSV_FILE = "usuarios.csv";

    // Auto-genera el archivo CSV para evitar errores de ejecución inicial
    private static void inicializarCSV() {
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("TIPO_USUARIO,NOMBRE,PASSWORD");
                pw.println("AUXILIAR ADMINISTRATIVO,auxiliar1,1234");
                pw.println("MEDICO FISIATRA,fisiatra1,1234");
                pw.println("COORDINADOR CITAS,coordinador1,1234");
            } catch (IOException e) { System.out.println("Error creando CSV."); }
        }
    }

    private static String autenticarPersonal(String usuario, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String linea;
            br.readLine(); // Saltar encabezado
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 3) {
                    if (datos[1].equals(usuario) && datos[2].equals(password)) {
                        return datos[0]; // Retorna el ROL
                    }
                }
            }
        } catch (IOException e) { System.out.println("Error leyendo CSV."); }
        return null; // Falla la autenticación
    }

    public static void main(String[] args) {
        inicializarCSV();
        Scanner sc = new Scanner(System.in);
        INRF_Facade api = new INRF_Facade();
        SistemaMemoria db = SistemaMemoria.getInstancia();

        while (true) {
            System.out.println("\n=== LOGIN SISTEMA INRF ===");
            System.out.println("1. Ingresar como Personal (Auxiliar Administrativo / Médico Fisiatra / Coordinador Citas)");
            System.out.println("2. Ingresar como Paciente");
            System.out.println("3. Ingresar como Cuidador");
            System.out.println("4. Salir del Sistema");
            System.out.print("Seleccione: ");
            String op = sc.nextLine();

            String rolActual = null;
            String idLogueado = null; // Para uso en menús de Paciente/Cuidador

            if (op.equals("1")) {
                System.out.print("Usuario: "); String u = sc.nextLine();
                System.out.print("Contraseña: "); String p = sc.nextLine();
                rolActual = autenticarPersonal(u, p);
                if (rolActual == null) { System.out.println("-> Credenciales incorrectas."); continue; }
                System.out.println("-> Bienvenido, acceso concedido como: " + rolActual);
            } 
            else if (op.equals("2") || op.equals("3")) {
                System.out.print("Ingrese ID del Paciente: "); 
                idLogueado = sc.nextLine();
                if (!db.getPacientes().containsKey(idLogueado)) {
                    System.out.println("-> ERROR: Paciente no registrado en el sistema. Acceso denegado.");
                    continue;
                }
                rolActual = op.equals("2") ? "PACIENTE" : "CUIDADOR";
                System.out.println("-> Acceso concedido al historial del paciente.");
            }
            else if (op.equals("4")) { break; }
            else { continue; }

            // BUCLE DE SESIÓN (Dependiendo del Rol)
            boolean sesionActiva = true;
            while (sesionActiva) {
                System.out.println("\n--- MENÚ " + rolActual + " ---");
                String id, idCita; // Variables auxiliares

                switch (rolActual) {
                    case "AUXILIAR ADMINISTRATIVO":
                        System.out.println("1. Registrar Paciente (RF-01)");
                        System.out.println("2. Actualizar Datos (RF-09)");
                        System.out.println("3. Cerrar Sesión");
                        System.out.print("Opción: ");
                        String opAux = sc.nextLine();
                        if (opAux.equals("1")) {
                            System.out.print("ID Cédula: "); id = sc.nextLine();
                            System.out.print("Nombre: "); String nom = sc.nextLine();
                            api.registrarPaciente(id, nom);
                        } else if (opAux.equals("2")) {
                            System.out.print("ID Paciente: "); id = sc.nextLine();
                            System.out.print("Nuevo Nombre: "); String nom = sc.nextLine();
                            api.actualizarDatos(id, nom);
                        } else if (opAux.equals("3")) sesionActiva = false;
                        break;

                    case "MEDICO FISIATRA":
                        System.out.println("1. Registrar Diagnóstico (RF-02)");
                        System.out.println("2. Registrar Sesión (RF-04)");
                        System.out.println("3. Generar Informe Clínico (RF-06)");
                        System.out.println("4. Consultar Evolución (RF-05)");
                        System.out.println("5. Cerrar Sesión");
                        System.out.print("Opción: ");
                        String opMed = sc.nextLine();
                        if (opMed.equals("1")) {
                            System.out.print("ID Paciente: "); id = sc.nextLine();
                            System.out.print("Diagnóstico: "); String diag = sc.nextLine();
                            System.out.print("Plan: "); String plan = sc.nextLine();
                            api.registrarDiagnostico(id, diag, plan);
                        } else if (opMed.equals("2")) {
                            System.out.print("ID Paciente: "); id = sc.nextLine();
                            System.out.print("Evolución de sesión: "); String evo = sc.nextLine();
                            api.registrarSesion(id, evo);
                        } else if (opMed.equals("3")) {
                            System.out.print("ID Paciente: "); id = sc.nextLine();
                            api.generarInformeDocumento(id);
                        } else if (opMed.equals("4")) {
                            System.out.print("ID Paciente: "); id = sc.nextLine();
                            api.consultarEvolucion(id);
                        } else if (opMed.equals("5")) sesionActiva = false;
                        break;

                    case "COORDINADOR CITAS":
                        System.out.println("1. Programar Cita (RF-03)");
                        System.out.println("2. Consultar Citas (RF-07)");
                        System.out.println("3. Cancelar Cita (RF-10)");
                        System.out.println("4. Cerrar Sesión");
                        System.out.print("Opción: ");
                        String opCita = sc.nextLine();
                        if (opCita.equals("1")) {
                            System.out.print("Cod Cita: "); idCita = sc.nextLine();
                            System.out.print("ID Paciente: "); id = sc.nextLine();
                            System.out.print("Fecha: "); String f = sc.nextLine();
                            System.out.print("Médico: "); String m = sc.nextLine();
                            api.programarCita(idCita, f, id, m);
                        } else if (opCita.equals("2")) {
                            System.out.print("ID Paciente: "); id = sc.nextLine();
                            api.consultarCitas(id);
                        } else if (opCita.equals("3")) {
                            System.out.print("Cod Cita: "); idCita = sc.nextLine();
                            api.cancelarCita(idCita);
                        } else if (opCita.equals("4")) sesionActiva = false;
                        break;

                    case "PACIENTE":
                        System.out.println("1. Consultar mi Evolución (RF-05)");
                        System.out.println("2. Generar mi Informe Clínico (RF-06)");
                        System.out.println("3. Cerrar Sesión");
                        System.out.print("Opción: ");
                        String opPac = sc.nextLine();
                        if (opPac.equals("1")) api.consultarEvolucion(idLogueado);
                        else if (opPac.equals("2")) api.generarInformeDocumento(idLogueado);
                        else if (opPac.equals("3")) sesionActiva = false;
                        break;

                    case "CUIDADOR":
                        System.out.println("1. Consultar Tratamiento del Paciente (RF-08)");
                        System.out.println("2. Cerrar Sesión");
                        System.out.print("Opción: ");
                        String opCui = sc.nextLine();
                        if (opCui.equals("1")) api.consultarTratamientoCuidador(idLogueado);
                        else if (opCui.equals("2")) sesionActiva = false;
                        break;
                }
            }
        }
        sc.close();
        System.out.println("Sistema Finalizado.");
    }
}