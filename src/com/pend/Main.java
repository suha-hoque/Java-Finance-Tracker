package com.pend;

import com.pend.db.Database;
import com.pend.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Initialize DB (creates file and tables if needed)
        Database.initialize();

        // Use system look and feel if available
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            LoginFrame lf = new LoginFrame();
            lf.setVisible(true);
        });
    }
}
