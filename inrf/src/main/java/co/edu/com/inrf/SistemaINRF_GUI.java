package co.edu.com.inrf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

// ==========================================
// INTERFAZ GRÁFICA PRINCIPAL (GUI)
// ==========================================
public class SistemaINRF_GUI extends JFrame {
    private static final String CSV_FILE = "usuarios.csv";
    private INRF_Facade api;
    private SistemaMemoria db;
    
    // Componentes UI
    private JPanel panelPrincipal;
    private CardLayout cardLayout;
    private JTextArea consolaSalida;
    private String rolActual = "";
    private String idLogueado = "";

    public SistemaINRF_GUI() {
        api = new INRF_Facade();
        db = SistemaMemoria.getInstancia();
        inicializarCSV();

        // Configuración de la Ventana
        setTitle("Sistema de Gestión INRF 2026");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Intentar usar el diseño nativo del Sistema Operativo
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
        catch (Exception e) {}

        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);
        
        // Crear las pantallas
        panelPrincipal.add(crearPanelLogin(), "LOGIN");
        panelPrincipal.add(crearPanelDashboard(), "DASHBOARD");

        add(panelPrincipal);
    }

    // --- AUTO-GENERAR CSV ---
    private void inicializarCSV() {
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

    private String autenticarPersonal(String usuario, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String linea; br.readLine();
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 3 && datos[1].equals(usuario) && datos[2].equals(password)) {
                    return datos[0];
                }
            }
        } catch (IOException e) {}
        return null;
    }

    // --- PANTALLA DE LOGIN ---
    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("INRF - Acceso al Sistema", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panel.add(titulo, gbc);

        // Opciones de Rol
        String[] roles = {"Personal Médico/Admin", "Paciente", "Cuidador"};
        JComboBox<String> comboRoles = new JComboBox<>(roles);
        gbc.gridy = 1; panel.add(comboRoles, gbc);

        // Campos dinámicos
        JLabel lblUser = new JLabel("Usuario / ID:");
        JTextField txtUser = new JTextField(15);
        JLabel lblPass = new JLabel("Contraseña:");
        JPasswordField txtPass = new JPasswordField(15);

        gbc.gridy = 2; gbc.gridwidth = 1; panel.add(lblUser, gbc);
        gbc.gridx = 1; panel.add(txtUser, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(lblPass, gbc);
        gbc.gridx = 1; panel.add(txtPass, gbc);

        // Lógica de mostrar/ocultar contraseña según el rol
        comboRoles.addActionListener(e -> {
            boolean esPersonal = comboRoles.getSelectedIndex() == 0;
            lblPass.setVisible(esPersonal);
            txtPass.setVisible(esPersonal);
            lblUser.setText(esPersonal ? "Usuario:" : "ID Cédula:");
        });

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.setBackground(new Color(0, 102, 204));
        btnIngresar.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; panel.add(btnIngresar, gbc);

        // Acción de Login
        btnIngresar.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());
            int tipoLog = comboRoles.getSelectedIndex();

            if (tipoLog == 0) { // Personal
                rolActual = autenticarPersonal(user, pass);
                if (rolActual != null) iniciarSesion();
                else JOptionPane.showMessageDialog(this, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
            } else { // Paciente o Cuidador
                if (db.getPacientes().containsKey(user)) {
                    rolActual = (tipoLog == 1) ? "PACIENTE" : "CUIDADOR";
                    idLogueado = user;
                    iniciarSesion();
                } else {
                    JOptionPane.showMessageDialog(this, "ID no registrado en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            txtPass.setText(""); // Limpiar
        });

        return panel;
    }

    // --- PANTALLA PRINCIPAL (DASHBOARD) ---
    private JPanel crearPanelDashboard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Cabecera
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel lblBienvenida = new JLabel("Bienvenido");
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 18));
        JButton btnSalir = new JButton("Cerrar Sesión");
        btnSalir.addActionListener(e -> cardLayout.show(panelPrincipal, "LOGIN"));
        topPanel.add(lblBienvenida, BorderLayout.CENTER);
        topPanel.add(btnSalir, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // Consola de Salida (Centro)
        consolaSalida = new JTextArea();
        consolaSalida.setEditable(false);
        consolaSalida.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(consolaSalida);
        scroll.setBorder(BorderFactory.createTitledBorder("Registro de Actividad del Sistema"));
        panel.add(scroll, BorderLayout.CENTER);

        // Redirigir System.out a la consola UI forzando UTF-8
        try {
            PrintStream printStream = new PrintStream(new TextAreaOutputStream(consolaSalida), true, "UTF-8");
            System.setOut(printStream);
        } catch (UnsupportedEncodingException e) {
            System.out.println("Error de codificación: " + e.getMessage());
        }

        return panel;
    }

    // --- CONSTRUCCIÓN DINÁMICA DEL MENÚ SEGÚN EL ROL ---
    private void iniciarSesion() {
        // Buscar el Dashboard y actualizarlo
        JPanel dashboard = (JPanel) panelPrincipal.getComponent(1);
        JLabel lblBienvenida = (JLabel) ((JPanel) dashboard.getComponent(0)).getComponent(0);
        lblBienvenida.setText("Panel de Control - " + rolActual);

        // Remover botones viejos si existen
        if (dashboard.getComponentCount() > 2) dashboard.remove(2);

        JPanel panelBotones = new JPanel(new GridLayout(0, 2, 10, 10));
        panelBotones.setBorder(BorderFactory.createTitledBorder("Acciones Disponibles"));

        // Añadir botones según los permisos (Mapeo de Funcionalidades)
        switch (rolActual) {
            case "AUXILIAR ADMINISTRATIVO":
                agregarBoton(panelBotones, "Registrar Paciente (RF-01)", e -> {
                    String id = JOptionPane.showInputDialog("ID Cédula:");
                    if (id != null && !id.trim().isEmpty()) {
                        String nom = JOptionPane.showInputDialog("Nombre Completo:");
                        api.registrarPaciente(id, nom);
                    }
                });
                agregarBoton(panelBotones, "Actualizar Datos (RF-09)", e -> {
                    String id = JOptionPane.showInputDialog("ID Cédula a actualizar:");
                    String nom = JOptionPane.showInputDialog("Nuevo Nombre:");
                    api.actualizarDatos(id, nom);
                });
                break;

            case "MEDICO FISIATRA":
                agregarBoton(panelBotones, "Registrar Diagnóstico (RF-02)", e -> {
                    String id = JOptionPane.showInputDialog("ID Paciente:");
                    String diag = JOptionPane.showInputDialog("Diagnóstico:");
                    String plan = JOptionPane.showInputDialog("Plan de Tratamiento:");
                    api.registrarDiagnostico(id, diag, plan);
                });
                agregarBoton(panelBotones, "Registrar Sesión (RF-04)", e -> {
                    String id = JOptionPane.showInputDialog("ID Paciente:");
                    String evo = JOptionPane.showInputDialog("Evolución/Detalle de sesión:");
                    api.registrarSesion(id, evo);
                });
                agregarBoton(panelBotones, "Consultar Evolución (RF-05)", e -> {
                    String id = JOptionPane.showInputDialog("ID Paciente:");
                    System.out.println("\n-----------------------------");
                    api.consultarEvolucion(id);
                });
                agregarBoton(panelBotones, "Generar Informe PDF/TXT (RF-06)", e -> {
                    String id = JOptionPane.showInputDialog("ID Paciente:");
                    api.generarInformeDocumento(id);
                    JOptionPane.showMessageDialog(this, "Informe generado en la carpeta del proyecto.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                });
                break;

            case "COORDINADOR CITAS":
                agregarBoton(panelBotones, "Programar Cita (RF-03)", e -> {
                    String idCita = JOptionPane.showInputDialog("Código Cita (Ej. C01):");
                    String id = JOptionPane.showInputDialog("ID Paciente:");
                    String fecha = JOptionPane.showInputDialog("Fecha (DD/MM/YY):");
                    String med = JOptionPane.showInputDialog("Médico Asignado:");
                    api.programarCita(idCita, fecha, id, med);
                });
                agregarBoton(panelBotones, "Consultar Citas (RF-07)", e -> {
                    String id = JOptionPane.showInputDialog("ID Paciente:");
                    System.out.println("\n-----------------------------");
                    api.consultarCitas(id);
                });
                agregarBoton(panelBotones, "Cancelar Cita (RF-10)", e -> {
                    String idCita = JOptionPane.showInputDialog("Código de Cita a Cancelar:");
                    api.cancelarCita(idCita);
                });
                break;

            case "PACIENTE":
                agregarBoton(panelBotones, "Consultar mi Evolución (RF-05)", e -> {
                    System.out.println("\n-----------------------------");
                    api.consultarEvolucion(idLogueado);
                });
                agregarBoton(panelBotones, "Generar mi Informe (RF-06)", e -> {
                    api.generarInformeDocumento(idLogueado);
                    JOptionPane.showMessageDialog(this, "Documento exportado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                });
                break;

            case "CUIDADOR":
                agregarBoton(panelBotones, "Consultar Tratamiento (RF-08)", e -> {
                    System.out.println("\n-----------------------------");
                    api.consultarTratamientoCuidador(idLogueado);
                });
                break;
        }

        dashboard.add(panelBotones, BorderLayout.SOUTH);
        consolaSalida.setText("Sesión iniciada como " + rolActual + ".\nListo para operar.\n");
        cardLayout.show(panelPrincipal, "DASHBOARD");
    }

    private void agregarBoton(JPanel panel, String texto, ActionListener accion) {
        JButton btn = new JButton(texto);
        btn.addActionListener(accion);
        panel.add(btn);
    }

    // --- MÉTODO MAIN DE ARRANQUE ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SistemaINRF_GUI app = new SistemaINRF_GUI();
            app.setVisible(true);
        });
    }
}
