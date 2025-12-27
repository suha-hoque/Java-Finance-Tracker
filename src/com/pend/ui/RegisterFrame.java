package com.pend.ui;

import com.pend.db.UserDAO;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    public RegisterFrame() {
        setTitle("$pend - Register");
        setSize(400, 260);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(7,1,4,4));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JPasswordField password2 = new JPasswordField();
        JButton create = new JButton("Create Account");

        form.add(new JLabel("Choose a username:"));
        form.add(username);
        form.add(new JLabel("Password:"));
        form.add(password);
        form.add(new JLabel("Confirm password:"));
        form.add(password2);
        form.add(create);

        add(form, BorderLayout.CENTER);

        create.addActionListener(e -> {
            String u = username.getText().trim();
            String p = new String(password.getPassword());
            String p2 = new String(password2.getPassword());
            if (u.isBlank() || p.isBlank()) {
                JOptionPane.showMessageDialog(this, "Username and password are required.");
                return;
            }
            if (!p.equals(p2)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match. Please try again.");
                return;
            }

            int id = UserDAO.register(u, p);
            if (id != -1) {
                JOptionPane.showMessageDialog(this, "Account created successfully. You may now log in.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Username may already exist.");
            }
        });
    }
}
