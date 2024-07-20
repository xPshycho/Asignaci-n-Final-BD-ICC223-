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
        setBounds(100, 100, 800, 600); // Ajusta el tama�o seg�n sea necesario
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
                JScrollPane scrollPane = new JScrollPane();
                panel.add(scrollPane);
                {
                    table = new JTable();
                    table.addMouseListener(new MouseAdapter() {
                    	@Override
                    	public void mouseClicked(MouseEvent e) {
                    		
                    		btnHorario.setEnabled(true);
                    		btnModificar.setEnabled(true);
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
                JButton btnNewButton = new JButton("Insertar");
                buttonPane.add(btnNewButton);
            }
            {
            	btnHorario = new JButton("Horario");
            	btnHorario.setEnabled(false);
            	btnHorario.addActionListener(new ActionListener() {
            		public void actionPerformed(ActionEvent e) {
            		}
            	});
            	buttonPane.add(btnHorario);
            }
            {
                btnModificar = new JButton("Editar");
                btnModificar.setEnabled(false);
                btnModificar.setActionCommand("OK");
                buttonPane.add(btnModificar);
                getRootPane().setDefaultButton(btnModificar);
            }
            {
                JButton cancelButton = new JButton("Cancelar");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dispose(); // Solo cierra el di�logo, no la conexi�n
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
            System.out.println("La conexi�n a la base de datos no est� disponible.");
        }

        return model;
    }
}
