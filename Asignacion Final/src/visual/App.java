package visual;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class App extends JFrame {

    private JPanel contentPane;
    private Dimension dim;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    App frame = new App();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public App() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        dim = super.getToolkit().getScreenSize();
        super.setSize(dim.width, dim.height - 100);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        // Create a panel with GridBagLayout
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new GridBagLayout());
        contentPane.add(panelCenter, BorderLayout.CENTER);

        // Create title label
        JLabel titleLabel = new JLabel("Sistema Académico");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48)); // Set font size and style
        GridBagConstraints gbc_title = new GridBagConstraints();
        gbc_title.gridx = 0;
        gbc_title.gridy = 0;
        gbc_title.gridwidth = 2;
        gbc_title.insets = new Insets(20, 0, 20, 0);
        panelCenter.add(titleLabel, gbc_title);

        // Create buttons
        JButton button1 = new JButton("Estudiantes");
        button1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		ListadoEstudiantes listaEs = new ListadoEstudiantes();
        		listaEs.setModal(true);
        		listaEs.setVisible(true);
        	}
        });
        JButton button2 = new JButton("Grupos");
        button2.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		ListadoGrupos listaGrupos = new ListadoGrupos();
        		listaGrupos.setModal(true);
        		listaGrupos.setVisible(true);
        	}
        });

        // Set button size
        Dimension buttonSize = new Dimension(200, 100);
        button1.setPreferredSize(buttonSize);
        button2.setPreferredSize(buttonSize);

        // Add buttons to panel
        GridBagConstraints gbc_button1 = new GridBagConstraints();
        gbc_button1.gridx = 0;
        gbc_button1.gridy = 1;
        gbc_button1.insets = new Insets(10, 10, 10, 50);
        panelCenter.add(button1, gbc_button1);

        GridBagConstraints gbc_button2 = new GridBagConstraints();
        gbc_button2.gridx = 1;
        gbc_button2.gridy = 1;
        gbc_button2.insets = new Insets(10, 50, 10, 10);
        panelCenter.add(button2, gbc_button2);
    }
}
