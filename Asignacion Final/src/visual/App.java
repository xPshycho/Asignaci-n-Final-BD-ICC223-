package visual;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

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
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32)); // Set font size and style
        GridBagConstraints gbc_title = new GridBagConstraints();
        gbc_title.gridx = 0;
        gbc_title.gridy = 0;
        gbc_title.gridwidth = 2;
        gbc_title.insets = new Insets(20, 0, 20, 0);
        panelCenter.add(titleLabel, gbc_title);

        // Create image labels as buttons
        JLabel button1 = new JLabel("Botón 1", JLabel.CENTER);
        JLabel button2 = new JLabel("Botón 2", JLabel.CENTER);

        // Set button size
        Dimension buttonSize = new Dimension(200, 100);
        button1.setPreferredSize(buttonSize);
        button2.setPreferredSize(buttonSize);

        // Load background image for buttons
        try {
            Image img = ImageIO.read(new File("/Asignacion Final/img/estudianteBoton.png"));
            button1.setIcon(new ImageIcon(img.getScaledInstance(button1.getWidth(), button1.getHeight(), Image.SCALE_SMOOTH)));
            button2.setIcon(new ImageIcon(img.getScaledInstance(button2.getWidth(), button2.getHeight(), Image.SCALE_SMOOTH)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Add mouse listener to labels to act as buttons
        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Botón 1 clicked");
            }
        });

        button2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Botón 2 clicked");
            }
        });

        // Increase space between buttons
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
