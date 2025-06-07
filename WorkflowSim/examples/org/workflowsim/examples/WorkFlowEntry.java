package org.workflowsim.examples;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WorkFlowEntry extends JFrame {

    static String GLOBAL_FONT="Cambria";
    private JTextField vmField;
    private JTextField daxField;
    private JComboBox<String> modeDropdown;

    public WorkFlowEntry() {
        setTitle("WorkflowSim Scheduler Entry");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // ===== Header =====
        JLabel header = new JLabel("WorkflowSim Scheduler", SwingConstants.CENTER);
        header.setFont(new Font(GLOBAL_FONT, Font.BOLD, 36));
        mainPanel.add(header, BorderLayout.NORTH);

        // ===== Instructions =====
        JTextArea info = new JTextArea(
                "Instructions:\n" +
                "1. Enter the number of virtual machines (VMs).\n" +
                "2. Enter the DAX file name (make sure it exists).\n" +
                "3. Select the scheduling mode (Static or Dynamic).\n" +
                "4. Click Submit to start the simulation.\n"
        );

        JTextArea instructions = new JTextArea(
                "Instructions:\n" +
                "1. Enter the number of virtual machines (VMs).\n" +
                "2. Enter the DAX file name (make sure it exists).\n" +
                "3. Select the scheduling mode (Static or Dynamic).\n" +
                "4. Click Submit to start the simulation.\n"
        );
        instructions.setFont(new Font(GLOBAL_FONT, Font.PLAIN, 16));
        instructions.setEditable(false);
        instructions.setBackground(null);
        instructions.setBorder(null);
        mainPanel.add(instructions, BorderLayout.WEST);

        // ===== Form =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Number of VMs:"), gbc);
        gbc.gridx = 1;
        vmField = new JTextField(20);
        formPanel.add(vmField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("DAX File Name:"), gbc);
        gbc.gridx = 1;
        daxField = new JTextField(20);
        formPanel.add(daxField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Mode:"), gbc);
        gbc.gridx = 1;
        String[] modes = {"Static", "Dynamic"};
        modeDropdown = new JComboBox<>(modes);
        formPanel.add(modeDropdown, gbc);

        // ===== Submit Button =====
        gbc.gridx = 1; gbc.gridy++;
        JButton submitButton = new JButton("Start Simulation");
        formPanel.add(submitButton, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // ===== Submit Action =====
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String vmText = vmField.getText().trim();
                String daxText = daxField.getText().trim();
                String mode = (String) modeDropdown.getSelectedItem();

                if (vmText.isEmpty() || daxText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                    return;
                }

                try {
                    int numVMs = Integer.parseInt(vmText);
                    String[] args = {String.valueOf(numVMs), daxText, mode};
                    JOptionPane.showMessageDialog(null, "Starting WorkflowSim...\nVMs: " + numVMs + "\nDAX File: " + daxText + "\nMode: " + mode);
                    WorkFlowSimMain.main(args);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Number of VMs must be an integer.");
                }
            }
        });

        // ===== Final Setup =====
        setContentPane(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WorkFlowEntry::new);
    }
}
