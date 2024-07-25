package visual;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
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

    // Listas para almacenar los cambios pendientes
    private List<String[]> gruposParaAgregar = new ArrayList<>();
    private List<String[]> gruposParaEliminar = new ArrayList<>();

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
        setModal(true);
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
                    fillGrupoTable(); // Asegúrate de que esto se llame después de llenar los grupos inscritos
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

        btnAadir = new JButton("Añadir");
        btnAadir.setBounds(661, 396, 153, 48);
        contentPanel.add(btnAadir);

        JButton btnNewButton = new JButton("Finalizar");
        btnNewButton.setBounds(919, 720, 115, 29);
        contentPanel.add(btnNewButton);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		dispose();
        	}
        });
        btnCancelar.setBounds(793, 720, 115, 29);
        contentPanel.add(btnCancelar);

        // Añadir ActionListener al ComboBox
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillGrupoTable(); // Llama a fillGrupoTable cuando el seleccionado cambia
            }
        });

        // Añadir ActionListener a btnAadir
        btnAadir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table_Grupos.getSelectedRow();
                if (selectedRow >= 0) {
                    String codigoPeriodo = table_Grupos.getValueAt(selectedRow, 0).toString();
                    String codigoAsignatura = table_Grupos.getValueAt(selectedRow, 1).toString();
                    String numeroGrupo = table_Grupos.getValueAt(selectedRow, 2).toString();
                    agregarGrupo(codigoPeriodo, codigoAsignatura, numeroGrupo);
                }
            }
        });

        // Añadir ActionListener a btnEliminar
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table_GruposInscritos.getSelectedRow();
                if (selectedRow >= 0) {
                    String codigoPeriodo = table_GruposInscritos.getValueAt(selectedRow, 0).toString();
                    String codigoAsignatura = table_GruposInscritos.getValueAt(selectedRow, 1).toString();
                    String numeroGrupo = table_GruposInscritos.getValueAt(selectedRow, 2).toString();
                    eliminarGrupo(codigoPeriodo, codigoAsignatura, numeroGrupo);
                }
            }
        });

        // Añadir ActionListener a btnNewButton (Finalizar)
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finalizarCambios();
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
        String queryGrupos = "SELECT * FROM Grupo WHERE Capacidad > 0";
        List<String[]> gruposInscritos = new ArrayList<>();

        // Si hay un estudiante seleccionado, recupera los grupos ya inscritos
        if (selectedStudentId != null) {
            String queryInscritos = "SELECT [Codigo Periodo], [Codigo Asignatura], [Numero Grupo] FROM [Grupo Inscrito] WHERE [ID Estudiante] = ?";
            try (PreparedStatement stmtInscritos = conn.prepareStatement(queryInscritos)) {
                stmtInscritos.setString(1, selectedStudentId);
                try (ResultSet rsInscritos = stmtInscritos.executeQuery()) {
                    while (rsInscritos.next()) {
                        gruposInscritos.add(new String[]{
                            rsInscritos.getString("Codigo Periodo"),
                            rsInscritos.getString("Codigo Asignatura"),
                            rsInscritos.getString("Numero Grupo")
                        });
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Crear una consulta para los grupos disponibles
        if (!ciclo.equals("Todos los periodos")) {
            queryGrupos += " AND [Codigo Periodo] = ?";
        }

        try (PreparedStatement stmtGrupos = conn.prepareStatement(queryGrupos)) {
            if (!ciclo.equals("Todos los periodos")) {
                stmtGrupos.setString(1, ciclo);
            }
            try (ResultSet rsGrupos = stmtGrupos.executeQuery()) {
                DefaultTableModel model = new DefaultTableModel(new Object[]{"Codigo Periodo", "Codigo Asignatura", "Numero Grupo", "Capacidad"}, 0);
                while (rsGrupos.next()) {
                    String codigoPeriodo = rsGrupos.getString("Codigo Periodo");
                    String codigoAsignatura = rsGrupos.getString("Codigo Asignatura");
                    String numeroGrupo = rsGrupos.getString("Numero Grupo");

                    // Verificar si el grupo ya está inscrito
                    boolean yaInscrito = gruposInscritos.stream().anyMatch(grupo ->
                        grupo[0].equals(codigoPeriodo) &&
                        grupo[1].equals(codigoAsignatura) &&
                        grupo[2].equals(numeroGrupo)
                    );

                    if (!yaInscrito) {
                        model.addRow(new Object[]{
                            codigoPeriodo,
                            codigoAsignatura,
                            numeroGrupo,
                            rsGrupos.getInt("Capacidad")
                        });
                    }
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

    private void agregarGrupo(String codigoPeriodo, String codigoAsignatura, String numeroGrupo) {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea agregar este grupo?", "Confirmar adición", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Agregar a la lista de grupos para agregar
            gruposParaAgregar.add(new String[]{codigoPeriodo, codigoAsignatura, numeroGrupo});
            // Remover de la lista de grupos para eliminar, si está presente
            gruposParaEliminar.removeIf(grupo -> grupo[0].equals(codigoPeriodo) && grupo[1].equals(codigoAsignatura) && grupo[2].equals(numeroGrupo));

            // Actualizar la tabla de grupos inscritos
            DefaultTableModel modelInscritos = (DefaultTableModel) table_GruposInscritos.getModel();
            modelInscritos.addRow(new Object[]{codigoPeriodo, codigoAsignatura, numeroGrupo});

            // Actualizar la tabla de grupos disponibles
            DefaultTableModel modelGrupos = (DefaultTableModel) table_Grupos.getModel();
            for (int i = 0; i < modelGrupos.getRowCount(); i++) {
                if (modelGrupos.getValueAt(i, 0).equals(codigoPeriodo) &&
                    modelGrupos.getValueAt(i, 1).equals(codigoAsignatura) &&
                    modelGrupos.getValueAt(i, 2).equals(numeroGrupo)) {
                    modelGrupos.removeRow(i);
                    break;
                }
            }
        }
    }

    private void eliminarGrupo(String codigoPeriodo, String codigoAsignatura, String numeroGrupo) {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este grupo?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Agregar a la lista de grupos para eliminar
            gruposParaEliminar.add(new String[]{codigoPeriodo, codigoAsignatura, numeroGrupo});
            // Remover de la lista de grupos para agregar, si está presente
            gruposParaAgregar.removeIf(grupo -> grupo[0].equals(codigoPeriodo) && grupo[1].equals(codigoAsignatura) && grupo[2].equals(numeroGrupo));

            // Actualizar la tabla de grupos disponibles
            DefaultTableModel modelGrupos = (DefaultTableModel) table_Grupos.getModel();
            modelGrupos.addRow(new Object[]{codigoPeriodo, codigoAsignatura, numeroGrupo});

            // Actualizar la tabla de grupos inscritos
            DefaultTableModel modelInscritos = (DefaultTableModel) table_GruposInscritos.getModel();
            for (int i = 0; i < modelInscritos.getRowCount(); i++) {
                if (modelInscritos.getValueAt(i, 0).equals(codigoPeriodo) &&
                    modelInscritos.getValueAt(i, 1).equals(codigoAsignatura) &&
                    modelInscritos.getValueAt(i, 2).equals(numeroGrupo)) {
                    modelInscritos.removeRow(i);
                    break;
                }
            }
        }
    }

    private void finalizarCambios() {
        try {
            // Insertar nuevos grupos inscritos
            String insertQuery = "INSERT INTO [Grupo Inscrito] ([ID Estudiante], [Codigo Periodo], [Codigo Asignatura], [Numero Grupo]) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                for (String[] grupo : gruposParaAgregar) {
                    insertStmt.setString(1, selectedStudentId);
                    insertStmt.setString(2, grupo[0]);
                    insertStmt.setString(3, grupo[1]);
                    insertStmt.setString(4, grupo[2]);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

            // Eliminar grupos inscritos
            String deleteQuery = "DELETE FROM [Grupo Inscrito] WHERE [ID Estudiante] = ? AND [Codigo Periodo] = ? AND [Codigo Asignatura] = ? AND [Numero Grupo] = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                for (String[] grupo : gruposParaEliminar) {
                    deleteStmt.setString(1, selectedStudentId);
                    deleteStmt.setString(2, grupo[0]);
                    deleteStmt.setString(3, grupo[1]);
                    deleteStmt.setString(4, grupo[2]);
                    deleteStmt.addBatch();
                }
                deleteStmt.executeBatch();
            }

            // Limpiar listas temporales
            gruposParaAgregar.clear();
            gruposParaEliminar.clear();

            // Actualizar las tablas
            fillGrupoTable();
            fillGrupoInscritoTable(selectedStudentId);

            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(this, "Cambios finalizados exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al finalizar los cambios.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        dispose();
    }
}
