package visual;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import logico.DatabaseConnection;

public class EditarGrupos extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JComboBox<String> comboHoraInicio;
    private JComboBox<String> comboHoraFin;
    private JCheckBox chckbxLunes;
    private JCheckBox chckbxMartes;
    private JCheckBox chckbxMiercoles;
    private JCheckBox chckbxJueves;
    private JCheckBox chckbxViernes;
    private JCheckBox chckbxSabado;
    private String codigoPeriodo;
    private String codigoAsignatura;
    private String numeroGrupo;
    private ListadoGrupos listadoGrupos = new ListadoGrupos();


    /**
     * Launch the application.
     */

    /**
     * Create the dialog.
     */
    public EditarGrupos(String codigoPeriodo, String codigoAsignatura, String numeroGrupo) {
        this.codigoPeriodo = codigoPeriodo;
        this.codigoAsignatura = codigoAsignatura;
        this.numeroGrupo = numeroGrupo;
        
        setBounds(100, 100, 450, 300);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new FlowLayout());

        // Horas de inicio y fin
        comboHoraInicio = new JComboBox<>();
        comboHoraFin = new JComboBox<>();
        for (int i = 7; i <= 22; i++) {
            String hora = String.format("%02d:00", i);
            comboHoraInicio.addItem(hora);
            comboHoraFin.addItem(hora);
        }

        // Días de la semana
        chckbxLunes = new JCheckBox("Lunes");
        chckbxMartes = new JCheckBox("Martes");
        chckbxMiercoles = new JCheckBox("Miércoles");
        chckbxJueves = new JCheckBox("Jueves");
        chckbxViernes = new JCheckBox("Viernes");
        chckbxSabado = new JCheckBox("Sábado");

        // Agregar componentes al panel
        contentPanel.add(comboHoraInicio);
        contentPanel.add(comboHoraFin);
        contentPanel.add(chckbxLunes);
        contentPanel.add(chckbxMartes);
        contentPanel.add(chckbxMiercoles);
        contentPanel.add(chckbxJueves);
        contentPanel.add(chckbxViernes);
        contentPanel.add(chckbxSabado);

        // Cargar datos del horario si existen
        cargarDatosHorario();

        // Panel de botones
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        {
            JButton okButton = new JButton("OK");
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!hayDiaSeleccionado()) {
                        JOptionPane.showMessageDialog(null, "Debe seleccionar al menos un día.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int confirm = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que quieres actualizar el horario?", "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        // Recoger la información seleccionada
                        String horaInicio = (String) comboHoraInicio.getSelectedItem();
                        String horaFin = (String) comboHoraFin.getSelectedItem();
                        StringBuilder dias = new StringBuilder();
                        if (chckbxLunes.isSelected()) dias.append("LU-");
                        if (chckbxMartes.isSelected()) dias.append("MA-");
                        if (chckbxMiercoles.isSelected()) dias.append("MI-");
                        if (chckbxJueves.isSelected()) dias.append("JU-");
                        if (chckbxViernes.isSelected()) dias.append("VI-");
                        if (chckbxSabado.isSelected()) dias.append("SA-");
                        
                        // Eliminar el último guion
                        if (dias.length() > 0) dias.setLength(dias.length() - 1);

                        // Imprimir la selección
                        System.out.println("Hora de Inicio: " + horaInicio);
                        System.out.println("Hora de Fin: " + horaFin);
                        System.out.println("Días: " + dias.toString());

                        // Lógica para actualizar el horario en la base de datos
                        actualizarHorarioGrupo(codigoPeriodo, codigoAsignatura, 
                        		numeroGrupo, dias.toString(), horaInicio, horaFin);
                        dispose();
                        listadoGrupos.loadData();
                        
                    }
                }
            });
            okButton.setActionCommand("OK");
            buttonPane.add(okButton);
            getRootPane().setDefaultButton(okButton);
        }
        {
            JButton deleteButton = new JButton("Eliminar Horario");
            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int confirm = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que quieres eliminar el horario?", "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        // Limpiar selecciones
                        comboHoraInicio.setSelectedIndex(-1);
                        comboHoraFin.setSelectedIndex(-1);
                        chckbxLunes.setSelected(false);
                        chckbxMartes.setSelected(false);
                        chckbxMiercoles.setSelected(false);
                        chckbxJueves.setSelected(false);
                        chckbxViernes.setSelected(false);
                        chckbxSabado.setSelected(false);

                        System.out.println("Horario eliminado.");

                        // Lógica para eliminar el horario en la base de datos
                        eliminarHorarioGrupo(codigoPeriodo, codigoAsignatura, numeroGrupo);
                        dispose();
                        listadoGrupos.loadData();

                    }
                }
            });
            deleteButton.setActionCommand("Eliminar");
            buttonPane.add(deleteButton);
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

    private void cargarDatosHorario() {
        Connection con = DatabaseConnection.getConnection();
        try {
            String query = "SELECT [Numero Dia], Hora_Inicio, Hora_Fin FROM Horario_Grupo WHERE [Codigo Periodo] = ? AND [Codigo Asignatura] = ? AND [Numero Grupo] = ?";
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, codigoPeriodo);
                stmt.setString(2, codigoAsignatura);
                stmt.setString(3, numeroGrupo);
                try (ResultSet rs = stmt.executeQuery()) {
                    Map<Integer, String> diaMap = new HashMap<>();
                    while (rs.next()) {
                        int numeroDia = rs.getInt("Numero Dia");
                        String horaInicio = rs.getString("Hora_Inicio").substring(0, 5);
                        String horaFin = rs.getString("Hora_Fin").substring(0, 5);
                        diaMap.put(numeroDia, horaInicio + "-" + horaFin);
                    }

                    // Llenar los campos de hora y días
                    if (!diaMap.isEmpty()) {
                        String[] firstHorario = diaMap.values().iterator().next().split("-");
                        comboHoraInicio.setSelectedItem(firstHorario[0]);
                        comboHoraFin.setSelectedItem(firstHorario[1]);

                        for (Integer dia : diaMap.keySet()) {
                            switch (dia) {
                                case 1:
                                    chckbxLunes.setSelected(true);
                                    break;
                                case 2:
                                    chckbxMartes.setSelected(true);
                                    break;
                                case 3:
                                    chckbxMiercoles.setSelected(true);
                                    break;
                                case 4:
                                    chckbxJueves.setSelected(true);
                                    break;
                                case 5:
                                    chckbxViernes.setSelected(true);
                                    break;
                                case 6:
                                    chckbxSabado.setSelected(true);
                                    break;
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void actualizarHorarioGrupo(String codigoPeriodo, String codigoAsignatura, String numeroGrupo, String dias, String horaInicio, String horaFin) {
        Connection con = DatabaseConnection.getConnection();
        try {
            // Eliminar registros existentes
            String deleteQuery = "DELETE FROM Horario_Grupo WHERE [Codigo Periodo] = ? AND [Codigo Asignatura] = ? AND [Numero Grupo] = ?";
            try (PreparedStatement deleteStmt = con.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, codigoPeriodo);
                deleteStmt.setString(2, codigoAsignatura);
                deleteStmt.setString(3, numeroGrupo);
                deleteStmt.executeUpdate();
            }

            // Insertar nuevos registros
            String insertQuery = "INSERT INTO Horario_Grupo ([Codigo Periodo], [Codigo Asignatura], [Numero Grupo], [Numero Dia], Hora_Inicio, Hora_Fin) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {
                String[] diasArray = dias.split("-");
                for (String dia : diasArray) {
                    int numeroDia = obtenerNumeroDia(con, dia);
                    insertStmt.setString(1, codigoPeriodo);
                    insertStmt.setString(2, codigoAsignatura);
                    insertStmt.setString(3, numeroGrupo);
                    insertStmt.setInt(4, numeroDia);
                    insertStmt.setString(5, horaInicio + ":00");
                    insertStmt.setString(6, horaFin + ":00");
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void eliminarHorarioGrupo(String codigoPeriodo, String codigoAsignatura, String numeroGrupo) {
        Connection con = DatabaseConnection.getConnection();
        try {
            String deleteQuery = "DELETE FROM Horario_Grupo WHERE [Codigo Periodo] = ? AND [Codigo Asignatura] = ? AND [Numero Grupo] = ?";
            try (PreparedStatement deleteStmt = con.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, codigoPeriodo);
                deleteStmt.setString(2, codigoAsignatura);
                deleteStmt.setString(3, numeroGrupo);
                deleteStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int obtenerNumeroDia(Connection con, String nombreCorto) {
        int numeroDia = -1;
        String query = "SELECT [Numero Dia] FROM [Dia_Semana] WHERE [Nombre Corto] = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, nombreCorto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    numeroDia = rs.getInt("Numero Dia");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numeroDia;
    }

    private boolean hayDiaSeleccionado() {
        return chckbxLunes.isSelected() || chckbxMartes.isSelected() || chckbxMiercoles.isSelected() || chckbxJueves.isSelected() || chckbxViernes.isSelected() || chckbxSabado.isSelected();
    }
}
