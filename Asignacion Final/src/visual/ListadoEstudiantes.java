package visual;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import logico.DatabaseConnection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ListadoEstudiantes extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTable table;
    private JButton btnEliminar;
    private JButton btnModificar;
    private JButton btnHorario;
    private String selectedStudentId; // Variable para almacenar el ID del estudiante seleccionado
    private JScrollPane scrollPane;
    private JButton btnInforme;

    public static void main(String[] args) {
        try {
            ListadoEstudiantes dialog = new ListadoEstudiantes();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ListadoEstudiantes() {
        setBounds(100, 100, 800, 600); // Ajusta el tamaño según sea necesario
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            JPanel panel = new JPanel();
            contentPanel.add(panel, BorderLayout.CENTER);
            panel.setLayout(new BorderLayout(0, 0));
            {
                scrollPane = new JScrollPane();
                panel.add(scrollPane);
                {
                    table = new JTable();
                    table.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int row = table.getSelectedRow();
                            if (row >= 0) {
                                selectedStudentId = table.getValueAt(row, 0).toString();
                                btnHorario.setEnabled(true);
                                btnModificar.setEnabled(true);
                        		btnInforme.setEnabled(true);

                            }
                        }
                    });
                    scrollPane.setViewportView(table);
                    // Configura el JTable con los datos obtenidos
                    table.setModel(getStudentData());
                }
            }
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                btnInforme = new JButton("Informe");
                btnInforme.addActionListener(new ActionListener() {
                	public void actionPerformed(ActionEvent e) {
                		PeriodosEstudiante periodos = new PeriodosEstudiante(selectedStudentId, false);
                		periodos.setModal(true);
                		periodos.setVisible(true);
                	}
                });
                btnInforme.setEnabled(false);
                buttonPane.add(btnInforme);
            }
            {
                btnHorario = new JButton("Horario");
                btnHorario.setEnabled(false);
                btnHorario.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // Usa el ID del estudiante seleccionado
                        System.out.println("ID del Estudiante para Horario: " + selectedStudentId);
                        // Llama a la función deseada con el ID del estudiante seleccionado
                        PeriodosEstudiante estudiante = new PeriodosEstudiante(selectedStudentId, true);
                        estudiante.setModal(true);
                        estudiante.setVisible(true);
                    }
                });
                buttonPane.add(btnHorario);
            }
            {
                btnModificar = new JButton("Editar");
                btnModificar.setEnabled(false);
                btnModificar.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // Usa el ID del estudiante seleccionado
                        System.out.println("ID del Estudiante para Editar: " + selectedStudentId);
                        try {
							InscribirGrupoEstudiante estudiante = new InscribirGrupoEstudiante();
							estudiante.setModal(true);
							estudiante.setVisible(true);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                        
                        
                        
                    }
                });
                btnModificar.setActionCommand("OK");
                buttonPane.add(btnModificar);
                getRootPane().setDefaultButton(btnModificar);
            }
            {
                JButton cancelButton = new JButton("Cancelar");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dispose(); // Solo cierra el diálogo, no la conexión
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    private DefaultTableModel getStudentData() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Estudiante");
        model.addColumn("Nombre");
        model.addColumn("Apellido");
        model.addColumn("ID Carrera");
        model.addColumn("ID Categoria Pago");
        model.addColumn("ID Nacionalidad");
        model.addColumn("Direccion");

        Connection connection = DatabaseConnection.getConnection();

        if (connection != null) {
            try (Statement stmt = connection.createStatement()) {
                String query = "SELECT [ID Estudiante], Nombre, Apellido, [ID Carrera], [ID Categoria Pago], [ID Nacionalidad], Direccion FROM Estudiante";
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    Object[] row = new Object[7];
                    row[0] = rs.getString("ID Estudiante");
                    row[1] = rs.getString("Nombre");
                    row[2] = rs.getString("Apellido");
                    row[3] = rs.getString("ID Carrera");
                    row[4] = rs.getString("ID Categoria Pago");
                    row[5] = rs.getString("ID Nacionalidad");
                    row[6] = rs.getString("Direccion");
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
}
