package com.pend.ui;

import com.pend.db.UserDAO;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("$pend - Login");
        setSize(380, 220);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5,1,5,5));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        form.add(new JLabel("Username:"));
        form.add(username);
        form.add(new JLabel("Password:"));
        form.add(password);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        JPanel row = new JPanel(new BorderLayout());
        row.add(registerBtn, BorderLayout.WEST);
        row.add(loginBtn, BorderLayout.EAST);

        add(form, BorderLayout.CENTER);
        add(row, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> {
            String u = username.getText().trim();
            String p = new String(password.getPassword());
            if (u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password.");
                return;
            }
            Integer id = UserDAO.authenticate(u, p);
            if (id != null) {
                DashboardFrame df = new DashboardFrame(id);
                df.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Check your username and password.");
            }
        });

        registerBtn.addActionListener(e -> {
            RegisterFrame rf = new RegisterFrame();
            rf.setVisible(true);
        });
    }
}
