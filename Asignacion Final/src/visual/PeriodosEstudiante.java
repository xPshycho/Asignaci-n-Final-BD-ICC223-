package visual;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import logico.DatabaseConnection;

public class PeriodosEstudiante extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTable table;
    private String studentId; // Variable para almacenar el ID del estudiante
    private String selectedPeriodoId; // Variable para almacenar el ID del periodo seleccionado

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            // Ejemplo de uso con un ID ficticio
            PeriodosEstudiante dialog = new PeriodosEstudiante("10149910");
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public PeriodosEstudiante(String studentId) {
        this.studentId = studentId; // Almacena el ID del estudiante
        setBounds(100, 100, 658, 509);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            JPanel panel = new JPanel();
            contentPanel.add(panel, BorderLayout.CENTER);
            panel.setLayout(new BorderLayout(0, 0));
            {
                JScrollPane scrollPane = new JScrollPane();
                panel.add(scrollPane);
                {
                    table = new JTable();
                    table.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int selectedRow = table.getSelectedRow();
                            if (selectedRow >= 0) {
                                selectedPeriodoId = table.getValueAt(selectedRow, 0).toString();
                                System.out.println("Periodo seleccionado: " + selectedPeriodoId);
                            }
                        }
                    });
                    scrollPane.setViewportView(table);
                    // Configura el JTable con los datos obtenidos
                    table.setModel(getInscripcionData());
                }
            }
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (selectedPeriodoId != null && !selectedPeriodoId.isEmpty()) {
                            System.out.println("Llamando al procedimiento almacenado con ID estudiante: " + studentId + " y periodo: " + selectedPeriodoId);
                            callStoredProcedure(studentId, selectedPeriodoId);
                        } else {
                            JOptionPane.showMessageDialog(null, "Por favor seleccione un periodo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(e -> dispose());
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    private DefaultTableModel getInscripcionData() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Codigo Periodo");
        model.addColumn("ID Estudiante");
        model.addColumn("Fecha Inscripcion");

        Connection connection = DatabaseConnection.getConnection();

        if (connection != null) {
            try (java.sql.PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT [Codigo Periodo], [ID Estudiante], [Fecha Inscripcion] FROM Inscripcion WHERE [ID Estudiante] = ?")) {
                pstmt.setString(1, studentId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Object[] row = new Object[3];
                    row[0] = rs.getString("Codigo Periodo");
                    row[1] = rs.getString("ID Estudiante");
                    row[2] = rs.getDate("Fecha Inscripcion");
                    model.addRow(row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("La conexión a la base de datos no está disponible.");
        }

        return model;
    }

    private void callStoredProcedure(String studentId, String periodoId) {
        Connection connection = DatabaseConnection.getConnection();

        if (connection != null) {
            String storedProcedure = "{call dbo.ObtenerHorarioClases(?, ?)}";
            try (CallableStatement stmt = connection.prepareCall(storedProcedure)) {
                stmt.setString(1, studentId);
                stmt.setString(2, periodoId);

                // Ejecutar el procedimiento almacenado
                ResultSet rs = stmt.executeQuery();

                // Crear el modelo para la tabla
                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("Detalle");
                model.addColumn("Lunes");
                model.addColumn("Martes");
                model.addColumn("Miércoles");
                model.addColumn("Jueves");
                model.addColumn("Viernes");
                model.addColumn("Sábado");

                // Obtener los datos del ResultSet y agregarlos al modelo
                while (rs.next()) {
                    Object[] row = new Object[7];
                    row[0] = rs.getString("Detalle");
                    row[1] = rs.getString("Lunes");
                    row[2] = rs.getString("Martes");
                    row[3] = rs.getString("Miércoles");
                    row[4] = rs.getString("Jueves");
                    row[5] = rs.getString("Viernes");
                    row[6] = rs.getString("Sábado");
                    model.addRow(row);
                }

                // Mostrar los resultados en una nueva ventana
                JFrame resultFrame = new JFrame("Horario de Clases");
                JTable resultTable = new JTable(model);
                resultFrame.add(new JScrollPane(resultTable));
                resultFrame.setSize(800, 600);
                resultFrame.setLocationRelativeTo(null);
                resultFrame.setAlwaysOnTop(true);
                resultFrame.setVisible(true);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("La conexión a la base de datos no está disponible.");
        }
    }
}
