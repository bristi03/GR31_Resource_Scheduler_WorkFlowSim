package org.workflowsim.examples;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ResultWindow {

    public static void showResultsInTable(Object[][] data, String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JFrame frame = new JFrame("WorkflowSim Scheduling Results");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);  // Center on screen
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Sample column names and data (you would replace this with your real results)
        String[] columns = {"Job ID", "Task ID", "VM ID", "Start Time", "Finish Time", "CPU Time"};
        Object[][] data = {
            {"Job_1", "Task_1", "VM_2", 100.0, 200.0, 100.0},
            {"Job_2", "Task_3", "VM_1", 150.0, 260.0, 110.0}
        };

        // Show in table
        showResultsInTable(data, columns);
    }
}