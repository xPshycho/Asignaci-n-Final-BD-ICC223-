package visual;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import logico.DatabaseConnection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ListadoGrupos extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private JButton botonEditar;
	private String selectedCodigoPeriodo;
	private String selectedCodigoAsignatura;
	private String selectedNumeroGrupo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ListadoGrupos dialog = new ListadoGrupos();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ListadoGrupos() {
		setBounds(100, 100, 947, 724);
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
				panel.add(scrollPane, BorderLayout.CENTER);
				{
					table = new JTable();
					table.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							int selectedRow = table.getSelectedRow();
							if (selectedRow != -1) {
								selectedCodigoPeriodo = (String) table.getValueAt(selectedRow, 0);
								selectedCodigoAsignatura = (String) table.getValueAt(selectedRow, 1);
								selectedNumeroGrupo = (String) table.getValueAt(selectedRow, 2);
								botonEditar.setEnabled(true);
							}
						}
					});
					scrollPane.setViewportView(table);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				botonEditar = new JButton("Editar");
				botonEditar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						EditarGrupos dialog = new EditarGrupos(selectedCodigoPeriodo, selectedCodigoAsignatura, selectedNumeroGrupo);
						dialog.setModal(true);
						dialog.setVisible(true);
					}
				});
				botonEditar.setEnabled(false);
				botonEditar.setActionCommand("OK");
				buttonPane.add(botonEditar);
				getRootPane().setDefaultButton(botonEditar);
			}
			{
				JButton cancelButton = new JButton("Cancelar");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		loadData();
	}
	
	private void loadData() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Codigo Periodo");
		model.addColumn("Codigo Asignatura");
		model.addColumn("Numero Grupo");
		model.addColumn("Capacidad");
		model.addColumn("Horario");

		try {
			Connection con = DatabaseConnection.getConnection();
			Statement stmt = con.createStatement();
			String query = "SELECT [Codigo Periodo], [Codigo Asignatura], [Numero Grupo], Capacidad, Horario FROM Grupo";
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				Vector<String> row = new Vector<>();
				row.add(rs.getString("Codigo Periodo"));
				row.add(rs.getString("Codigo Asignatura"));
				row.add(rs.getString("Numero Grupo"));
				row.add(rs.getString("Capacidad"));
				row.add(rs.getString("Horario"));
				model.addRow(row);
			}

			table.setModel(model);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
