package com.pend.ui;

import com.pend.db.TransactionDAO;
import com.pend.model.Transaction;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private int userId;
    private JLabel totalSpentLabel;
    private JTextField totalIncomeField;
    private TransactionPanel transactionPanel;

    public DashboardFrame(int userId) {
        this.userId = userId;
        setTitle("$pend - Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalSpentLabel = new JLabel("Total Spent: $0.00");

        JLabel incomeLabel = new JLabel("Total Income:"); // <-- updated label
        totalIncomeField = new JTextField(10);
        JButton saveIncomeBtn = new JButton("Save");
        JButton chartBtn = new JButton("Show Graph");

        topPanel.add(totalSpentLabel);
        topPanel.add(incomeLabel);
        topPanel.add(totalIncomeField);
        topPanel.add(saveIncomeBtn);
        topPanel.add(chartBtn);

        add(topPanel, BorderLayout.NORTH);

        // Transaction Panel
        transactionPanel = new TransactionPanel(userId, this::updateSummary);
        add(transactionPanel, BorderLayout.CENTER);

        saveIncomeBtn.addActionListener(e -> {
            try {
                double totalIncome = Double.parseDouble(totalIncomeField.getText());
                JOptionPane.showMessageDialog(this, "Total income set: $" + String.format("%.2f", totalIncome));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for total income.");
            }
        });

        chartBtn.addActionListener(e -> ExpenseChartPanel.showChartFrame(userId));

        updateSummary();
    }

    private void updateSummary() {
        List<Transaction> list = TransactionDAO.getTransactionsForUser(userId);
        double totalSpent = 0;
        String[] expenseCategories = {"Groceries", "Entertainment", "Utilities", "Leisure", "Other"};

        for (Transaction t : list) {
            if (java.util.Arrays.asList(expenseCategories).contains(t.getCategory())) {
                totalSpent += t.getAmount();
            }
        }

        totalSpentLabel.setText("Total Spent: $" + String.format("%.2f", totalSpent));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DashboardFrame df = new DashboardFrame(1);
            df.setVisible(true);
        });
    }
}
