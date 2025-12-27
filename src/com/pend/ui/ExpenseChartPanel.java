package com.pend.ui;

import com.pend.db.TransactionDAO;
import com.pend.model.Transaction;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseChartPanel extends JPanel {

    private Map<String, Double> categoryData;
    private String title;

    public ExpenseChartPanel(Map<String, Double> categoryData, String title) {
        this.categoryData = categoryData;
        this.title = title;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (categoryData.isEmpty()) {
            g.setColor(Color.BLACK);
            g.drawString("No expenses to display", 50, 50);
            return;
        }

        int width = getWidth();
        int height = getHeight();
        int margin = 50;
        int chartHeight = height - 2 * margin;
        int chartWidth = width - 2 * margin;

        int barWidth = chartWidth / categoryData.size() - 10;

        double maxVal = Collections.max(categoryData.values());

        g.setColor(Color.BLACK);
        g.drawString(title, width / 2 - g.getFontMetrics().stringWidth(title) / 2, margin / 2);

        int i = 0;
        for (String cat : categoryData.keySet()) {
            double value = categoryData.get(cat);
            int barHeight = (int) ((value / maxVal) * chartHeight);

            int x = margin + i * (barWidth + 10);
            int y = height - margin - barHeight;

            g.setColor(new Color(100, 149, 237));
            g.fillRect(x, y, barWidth, barHeight);

            g.setColor(Color.BLACK);
            g.drawString(cat, x, height - margin + 15);
            g.drawString(String.format("%.2f", value), x, y - 5);
            i++;
        }

        g.drawLine(margin, height - margin, width - margin, height - margin); // x-axis
        g.drawLine(margin, height - margin, margin, margin); // y-axis
    }

    public static void showChartFrame(int userId) {
        String[] periods = {"Today", "Weekly", "Monthly", "Yearly"};
        String period = (String) JOptionPane.showInputDialog(
                null,
                "Select period for chart",
                "Chart Options",
                JOptionPane.PLAIN_MESSAGE,
                null,
                periods,
                periods[0]
        );
        if (period == null) return;

        List<Transaction> transactions = TransactionDAO.getTransactionsForUser(userId);
        String[] expenseCategories = {"Groceries", "Entertainment", "Utilities", "Leisure", "Other"};

        LocalDate now = LocalDate.now();
        Map<String, Double> categorySums = new LinkedHashMap<>();
        double totalAmount = 0.0;
        String selectedPeriodLabel = "";

        switch (period) {
            case "Today":
                selectedPeriodLabel = now.toString();
                for (String cat : expenseCategories) categorySums.put(cat, 0.0);
                for (Transaction t : transactions) {
                    if (!Arrays.asList(expenseCategories).contains(t.getCategory())) continue;
                    if (t.getDate().equals(now)) {
                        categorySums.put(t.getCategory(), categorySums.get(t.getCategory()) + t.getAmount());
                        totalAmount += t.getAmount();
                    }
                }
                break;

            case "Weekly":
                int currentWeek = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                int currentYear = now.getYear();
                selectedPeriodLabel = "Week " + currentWeek + " of " + currentYear;
                for (String cat : expenseCategories) categorySums.put(cat, 0.0);
                for (Transaction t : transactions) {
                    if (!Arrays.asList(expenseCategories).contains(t.getCategory())) continue;
                    int week = t.getDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                    int year = t.getDate().getYear();
                    if (week == currentWeek && year == currentYear) {
                        categorySums.put(t.getCategory(), categorySums.get(t.getCategory()) + t.getAmount());
                        totalAmount += t.getAmount();
                    }
                }
                break;

            case "Monthly":
                Set<String> monthsSet = transactions.stream()
                        .map(t -> t.getDate().getMonth() + " " + t.getDate().getYear())
                        .collect(Collectors.toSet());
                String[] months = monthsSet.toArray(new String[0]);
                String selectedMonth = (String) JOptionPane.showInputDialog(
                        null,
                        "Select month:",
                        "Month Selection",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        months,
                        months[0]
                );
                if (selectedMonth == null) return;
                selectedPeriodLabel = selectedMonth;
                for (String cat : expenseCategories) categorySums.put(cat, 0.0);
                for (Transaction t : transactions) {
                    if (!Arrays.asList(expenseCategories).contains(t.getCategory())) continue;
                    String monthStr = t.getDate().getMonth() + " " + t.getDate().getYear();
                    if (monthStr.equals(selectedMonth)) {
                        categorySums.put(t.getCategory(), categorySums.get(t.getCategory()) + t.getAmount());
                        totalAmount += t.getAmount();
                    }
                }
                break;

            case "Yearly":
                Set<String> yearsSet = transactions.stream()
                        .map(t -> String.valueOf(t.getDate().getYear()))
                        .collect(Collectors.toSet());
                String[] years = yearsSet.toArray(new String[0]);
                String selectedYear = (String) JOptionPane.showInputDialog(
                        null,
                        "Select year:",
                        "Year Selection",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        years,
                        years[0]
                );
                if (selectedYear == null) return;
                selectedPeriodLabel = selectedYear;
                for (String cat : expenseCategories) categorySums.put(cat, 0.0);
                for (Transaction t : transactions) {
                    if (!Arrays.asList(expenseCategories).contains(t.getCategory())) continue;
                    if (String.valueOf(t.getDate().getYear()).equals(selectedYear)) {
                        categorySums.put(t.getCategory(), categorySums.get(t.getCategory()) + t.getAmount());
                        totalAmount += t.getAmount();
                    }
                }
                break;
        }

        JFrame frame = new JFrame("Expenses Chart - " + period);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top panel with currently viewing label and total amount
        JPanel topPanel = new JPanel(new GridLayout(2,1));
        JLabel periodLabel = new JLabel("Currently Viewing: " + selectedPeriodLabel); // <-- updated text
        periodLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        periodLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel totalLabel = new JLabel("Total Amount: $" + String.format("%.2f", totalAmount));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(periodLabel);
        topPanel.add(totalLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new ExpenseChartPanel(categorySums, period + " Expenses by Category"), BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
    }
}
