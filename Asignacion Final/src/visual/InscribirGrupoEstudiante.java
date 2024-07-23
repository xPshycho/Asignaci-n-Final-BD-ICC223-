package visual;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JComboBox;

import logico.DatabaseConnection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class InscribirGrupoEstudiante extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTable tableEstudiante;
    private JTable table_Grupos;
    private JTable table_GruposInscritos;
    private Connection conn;
    private String selectedStudentId;
    private JComboBox<String> comboBox;
    private JTextArea textAreaHorario;
    private JButton btnAadir;
    private JButton btnEliminar;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            InscribirGrupoEstudiante dialog = new InscribirGrupoEstudiante();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public InscribirGrupoEstudiante() throws SQLException {
        conn = DatabaseConnection.getConnection();

        setBounds(100, 100, 1096, 821);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        
        JPanel panel = new JPanel();
        panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        panel.setBounds(15, 13, 465, 367);
        contentPanel.add(panel);
        panel.setLayout(new BorderLayout(0, 0));
        
        JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tableEstudiante = new JTable();
        tableEstudiante.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableEstudiante.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedStudentId = tableEstudiante.getValueAt(selectedRow, 0).toString();
                    fillGrupoInscritoTable(selectedStudentId);
                }
            }
        });
        scrollPane.setViewportView(tableEstudiante);
        
        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        panel_1.setBounds(495, 16, 489, 364);
        contentPanel.add(panel_1);
        panel_1.setLayout(new BorderLayout(0, 0));
        
        JScrollPane scrollPane_1 = new JScrollPane();
        panel_1.add(scrollPane_1, BorderLayout.CENTER);
        
        table_Grupos = new JTable();
        table_Grupos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table_Grupos.getSelectedRow();
                if (selectedRow >= 0) {
                    String codigoPeriodo = table_Grupos.getValueAt(selectedRow, 0).toString();
                    String codigoAsignatura = table_Grupos.getValueAt(selectedRow, 1).toString();
                    String numeroGrupo = table_Grupos.getValueAt(selectedRow, 2).toString();
                    fillHorario(codigoPeriodo, codigoAsignatura, numeroGrupo);
                }
            }
        });
        scrollPane_1.setViewportView(table_Grupos);
        
        JPanel panel_2 = new JPanel();
        panel_2.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panel_2.setBounds(25, 460, 1034, 237);
        contentPanel.add(panel_2);
        panel_2.setLayout(new BorderLayout(0, 0));
        
        JScrollPane scrollPane_2 = new JScrollPane();
        panel_2.add(scrollPane_2, BorderLayout.CENTER);
        
        table_GruposInscritos = new JTable();
        scrollPane_2.setViewportView(table_GruposInscritos);

        // Crear e inicializar el ComboBox
        comboBox = new JComboBox<>();
        comboBox.setBounds(940, 399, 94, 35);
        contentPanel.add(comboBox);
        
        JPanel panel_3 = new JPanel();
        panel_3.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panel_3.setBounds(384, 714, 296, 35);
        contentPanel.add(panel_3);
        panel_3.setLayout(new BorderLayout(0, 0));
        
        // Crear JTextArea para mostrar el horario
        textAreaHorario = new JTextArea();
        textAreaHorario.setEditable(false); // Solo lectura
        JScrollPane scrollPane_3 = new JScrollPane(textAreaHorario);
        panel_3.add(scrollPane_3, BorderLayout.CENTER);
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(319, 396, 153, 48);
        contentPanel.add(btnEliminar);
        
        btnAadir = new JButton("A\u00F1adir");
        btnAadir.setBounds(661, 396, 153, 48);
        contentPanel.add(btnAadir);
        
        // Añadir ActionListener al ComboBox
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillGrupoTable(); // Llama a fillGrupoTable cuando el seleccionado cambia
            }
        });

        // Llamar métodos para llenar las tablas y el ComboBox
        fillEstudianteTable();
        fillComboBoxWithPeriodos(); // Primero llenar el ComboBox
        fillGrupoTable(); // Luego llenar la tabla de grupos
        fillGrupoInscritoTable(null); // Llenar inicialmente sin filtrar por estudiante

        // Añadir un listener para cerrar la conexión cuando el diálogo se cierre
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fillEstudianteTable() {
        String query = "SELECT [ID Estudiante], [Nombre] + ' ' + [Apellido] AS NombreCompleto, [ID Carrera] FROM Estudiante";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(new Object[]{"ID Estudiante", "Nombre Completo", "ID Carrera"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("ID Estudiante"),
                    rs.getString("NombreCompleto"),
                    rs.getString("ID Carrera")
                });
            }
            tableEstudiante.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillGrupoTable() {
        String ciclo = (String) comboBox.getSelectedItem();
        String query;
        if (ciclo.equals("Todos los periodos")) {
            query = "SELECT * FROM Grupo WHERE Capacidad > 0";
        } else {
            query = "SELECT * FROM Grupo WHERE Capacidad > 0 AND [Codigo Periodo] = ?";
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            if (!ciclo.equals("Todos los periodos")) {
                stmt.setString(1, ciclo);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                DefaultTableModel model = new DefaultTableModel(new Object[]{"Codigo Periodo", "Codigo Asignatura", "Numero Grupo", "Capacidad"}, 0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("Codigo Periodo"),
                        rs.getString("Codigo Asignatura"),
                        rs.getString("Numero Grupo"),
                        rs.getInt("Capacidad")
                    });
                }
                table_Grupos.setModel(model);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillGrupoInscritoTable(String studentId) {
        if (studentId == null) {
            DefaultTableModel model = new DefaultTableModel(new Object[]{"Codigo Periodo", "Codigo Asignatura", "Numero Grupo"}, 0);
            table_GruposInscritos.setModel(model);
            return;
        }

        String query = "SELECT [Codigo Periodo], [Codigo Asignatura], [Numero Grupo] FROM [Grupo Inscrito] WHERE [ID Estudiante] = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                DefaultTableModel model = new DefaultTableModel(new Object[]{"Codigo Periodo", "Codigo Asignatura", "Numero Grupo"}, 0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("Codigo Periodo"),
                        rs.getString("Codigo Asignatura"),
                        rs.getString("Numero Grupo")
                    });
                }
                table_GruposInscritos.setModel(model);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillComboBoxWithPeriodos() {
        String query = "SELECT DISTINCT [Codigo Periodo] FROM Grupo";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            comboBox.addItem("Todos los periodos");
            while (rs.next()) {
                comboBox.addItem(rs.getString("Codigo Periodo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillHorario(String codigoPeriodo, String codigoAsignatura, String numeroGrupo) {
        // Consulta SQL para obtener el horario basado en los tres identificadores
        String query = "SELECT [Horario] FROM Grupo WHERE [Codigo Periodo] = ? AND [Codigo Asignatura] = ? AND [Numero Grupo] = ?";

        // Depuración: Imprimir los valores utilizados para la búsqueda
        System.out.println("Buscando horario para Codigo Periodo: " + codigoPeriodo + ", Codigo Asignatura: " + codigoAsignatura + ", Numero Grupo: " + numeroGrupo);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, codigoPeriodo);
            stmt.setString(2, codigoAsignatura);
            stmt.setString(3, numeroGrupo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String horario = rs.getString("Horario");
                    if (horario != null && !horario.isEmpty()) {
                        textAreaHorario.setText(horario); // Muestra el horario en el JTextArea
                    } else {
                        textAreaHorario.setText("No hay información de horario disponible.");
                    }
                } else {
                    textAreaHorario.setText("No hay información de horario disponible.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            textAreaHorario.setText("Error al recuperar la información del horario.");
        }
    }
}
