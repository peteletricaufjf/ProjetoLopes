package app.views;

import app.processing.Main;
import processing.core.PApplet;
import processing.serial.Serial;

import javax.swing.*;
import java.awt.*;

public class ConfigForm {
    private JFrame frame = new JFrame("Software Principal");

    private JPanel mainPanel;
    private JButton restartButton;
    private JTextField baudTextField;
    private JButton startButton;
    private JComboBox<String> comPortComboBox;
    private JButton updateCOMButton;
    private JComboBox serialBaudComboBox;

    final Main main = new Main();

    public ConfigForm() {
        // Initialise the button listeners
        updateCOMButton.addActionListener(e -> {
            comPortComboBox.removeAllItems();
            for(String comPort : Serial.list()) {
                comPortComboBox.addItem(comPort);
            }
        });
        startButton.addActionListener(e -> {
            main.setBaud(Integer.parseInt(baudTextField.getText()));
            main.setSerialBaud(Integer.parseInt(serialBaudComboBox.getSelectedItem().toString()));
            main.setSerialPort(comPortComboBox.getSelectedItem().toString());
            PApplet.runSketch( new String[] { "" }, main);
        });
        restartButton.addActionListener(e -> SwingUtilities.invokeLater(main::finish));

        // Initialise default field values
        updateCOMButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        updateCOMButton.doClick();
        baudTextField.setText(String.valueOf(main.getBaud()));
        serialBaudComboBox.setSelectedItem("9600");
    }

    public void init() {
        frame.setContentPane(new ConfigForm().mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.setIconImage(new ImageIcon("src/assets/icon.png").getImage());
        frame.pack();
        frame.setVisible(true);
    }

}
