package com.pend.ui;

import com.pend.db.TransactionDAO;
import com.pend.model.Transaction;
import com.pend.util.CSVUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class TransactionPanel extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private final int userId;
    private final Consumer<Void> onChanged;

    public TransactionPanel(int userId, Runnable onChangedRunnable) {
        this.userId = userId;
        this.onChanged = v -> onChangedRunnable.run();
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Expense-only categories
        String[] categories = {"Groceries", "Entertainment", "Utilities", "Leisure", "Other"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);

        String[] currencies = {"USD", "EUR", "GBP", "JPY", "Other"};
        JComboBox<String> currencyCombo = new JComboBox<>(currencies);

        JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
        JTextField amountField = new JTextField(8);
        JTextField descField = new JTextField(12);

        JButton addBtn = new JButton("Add");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton exportBtn = new JButton("Export CSV");
        JButton importBtn = new JButton("Import CSV");

        top.add(new JLabel("Date:")); top.add(dateField);
        top.add(new JLabel("Amount:")); top.add(amountField);
        top.add(new JLabel("Category:")); top.add(categoryCombo);
        top.add(new JLabel("Desc:")); top.add(descField);
        top.add(new JLabel("Curr:")); top.add(currencyCombo);
        top.add(addBtn);
        top.add(deleteBtn);
        top.add(exportBtn);
        top.add(importBtn);

        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"id","date","amount","category","description","currency"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // id not editable
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide id column visually
        add(new JScrollPane(table), BorderLayout.CENTER);

        reloadTable();

        // Add button
        addBtn.addActionListener(e -> {
            try {
                LocalDate d = LocalDate.parse(dateField.getText().trim());
                double amt = Double.parseDouble(amountField.getText().trim());
                String cat = categoryCombo.getSelectedItem().toString();
                String desc = descField.getText().trim();
                String cur = currencyCombo.getSelectedItem().toString();

                Transaction t = new Transaction(d, amt, cat, desc, cur);
                TransactionDAO.addTransaction(userId, t);

                reloadTable();
                onChanged.accept(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please check date format and amount.");
            }
        });

        // Delete button (multi-row)
        deleteBtn.addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(this, "Please select at least one transaction to delete.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Delete selected transaction(s)?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int modelRow = table.convertRowIndexToModel(selectedRows[i]);
                int id = (int) model.getValueAt(modelRow, 0);
                TransactionDAO.deleteTransaction(id);
            }

            reloadTable();
            onChanged.accept(null);
        });

        // Export CSV
        exportBtn.addActionListener(e -> {
            List<Transaction> list = TransactionDAO.getTransactionsForUser(userId);
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                CSVUtil.exportToCSV(list, fc.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Exported successfully.");
            }
        });

        // Import CSV
        importBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                List<Transaction> list = CSVUtil.importFromCSV(fc.getSelectedFile());
                for (Transaction t : list) TransactionDAO.addTransaction(userId, t);
                reloadTable();
                onChanged.accept(null);
                JOptionPane.showMessageDialog(this, "Imported " + list.size() + " transactions.");
            }
        });

        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Table editing listener
        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                try {
                    int id = (int) model.getValueAt(row, 0);
                    LocalDate d = LocalDate.parse((String) model.getValueAt(row, 1));
                    double amt = Double.parseDouble(model.getValueAt(row, 2).toString());
                    String cat = model.getValueAt(row, 3).toString();
                    String desc = model.getValueAt(row, 4).toString();
                    String cur = model.getValueAt(row, 5).toString();

                    Transaction t = new Transaction(id, d, amt, cat, desc, cur);
                    TransactionDAO.updateTransaction(t);
                    onChanged.accept(null);
                } catch (Exception ignored) {}
            }
        });
    }

    public void reloadTable() {
        model.setRowCount(0);
        List<Transaction> list = TransactionDAO.getTransactionsForUser(userId);
        for (Transaction t : list) {
            model.addRow(new Object[]{
                    t.getId(),
                    t.getDate().toString(),
                    String.format("%.2f", t.getAmount()),
                    t.getCategory(),
                    t.getDescription(),
                    t.getCurrency()
            });
        }
    }
}
