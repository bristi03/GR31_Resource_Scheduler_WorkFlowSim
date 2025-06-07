package org.workflowsim.examples;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WorkFlowEntry extends JFrame {

    private JTextField vmField;
    private JTextField daxField;
    private JComboBox<String> modeDropdown;

    public WorkFlowEntry() {
        setTitle("WorkflowSim Scheduler Entry");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Create form elements
        JLabel vmLabel = new JLabel("Number of VMs:");
        vmField = new JTextField();

        JLabel daxLabel = new JLabel("DAX File Name:");
        daxField = new JTextField();

        JLabel modeLabel = new JLabel("Mode:");
        String[] modes = {"Static", "Dynamic"};
        modeDropdown = new JComboBox<>(modes);

        JButton submitButton = new JButton("Submit");

        // Layout setup
        setLayout(new GridLayout(4, 2, 10, 10));
        add(vmLabel);
        add(vmField);
        add(daxLabel);
        add(daxField);
        add(modeLabel);
        add(modeDropdown);
        add(new JLabel()); // Empty cell
        add(submitButton);

        // Submit button action
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String vmText = vmField.getText().trim();
                String daxText = daxField.getText().trim();
                String mode = (String) modeDropdown.getSelectedItem();

                // Basic input validation
                if (vmText.isEmpty() || daxText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                    return;
                }

                try {
                    int numVMs = Integer.parseInt(vmText);
                    // You can now pass these to WorkflowSim launcher
                    WorkFlowSimMain.configureBasic(numVMs, daxText, mode);
                    new WorkFlowSimMain();
                    JOptionPane.showMessageDialog(null, "Starting WorkflowSim...\nVMs: " + numVMs + "\nDAX File: " + daxText + "\nMode: " + mode);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Number of VMs must be an integer.");
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new WorkFlowEntry();
    }
}
