package view;

import controller.AppController;
import model.Clinician;
import model.Patient;
import model.Staff;
import model.UserRole;
import model.UserSession;
import util.I18n;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public class LoginDialog extends JDialog {
    private final AppController controller;
    private final JComboBox<String> roleBox = new JComboBox<>();
    private final JComboBox<UserChoice> userBox = new JComboBox<>();
    private final JButton registerButton = new JButton();
    private final JButton loginButton = new JButton();
    private final JButton cancelButton = new JButton();

    private UserSession session;

    public LoginDialog(Frame owner, AppController controller) {
        super(owner, true);
        this.controller = controller;
        setTitle(I18n.t("dialog.login"));
        buildLayout();
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    public UserSession getSession() {
        return session;
    }

    private void buildLayout() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel roleLabel = new JLabel(I18n.t("label.role"));
        form.add(roleLabel, gbc);
        gbc.gridx = 1;
        roleBox.addItem(I18n.t("role.patient"));
        roleBox.addItem(I18n.t("role.clinician"));
        roleBox.addItem(I18n.t("role.admin"));
        form.add(roleBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel userLabel = new JLabel(I18n.t("label.user"));
        form.add(userLabel, gbc);
        gbc.gridx = 1;
        form.add(userBox, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(registerButton);
        buttons.add(loginButton);
        buttons.add(cancelButton);

        registerButton.addActionListener(event -> registerPatient());
        loginButton.addActionListener(event -> login());
        cancelButton.addActionListener(event -> cancel());

        roleBox.addActionListener(event -> updateUsers());

        registerButton.setText(I18n.t("button.register"));
        loginButton.setText(I18n.t("button.login"));
        cancelButton.setText(I18n.t("button.cancel"));

        updateUsers();

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void updateUsers() {
        userBox.removeAllItems();
        UserRole role = getSelectedRole();
        List<UserChoice> choices = new ArrayList<>();
        if (role == UserRole.PATIENT) {
            for (Patient patient : controller.getPatients()) {
                choices.add(new UserChoice(patient.getPatientId(), patient.getFullName()));
            }
        } else if (role == UserRole.CLINICIAN) {
            for (Clinician clinician : controller.getClinicians()) {
                choices.add(new UserChoice(clinician.getClinicianId(), clinician.getFullName()));
            }
        } else if (role == UserRole.ADMIN) {
            for (Staff staff : controller.getStaff()) {
                choices.add(new UserChoice(staff.getStaffId(), staff.getFullName()));
            }
        }
        for (UserChoice choice : choices) {
            userBox.addItem(choice);
        }
        boolean hasUsers = !choices.isEmpty();
        userBox.setEnabled(hasUsers);
        loginButton.setEnabled(hasUsers);
        registerButton.setVisible(role == UserRole.PATIENT);
        if (!hasUsers) {
            JOptionPane.showMessageDialog(this, I18n.t("message.noUsers"));
        }
    }

    private void registerPatient() {
        String newId = controller.nextPatientId();
        String[] labels = patientLabels();
        String[] values = new String[]{
            newId, "", "", "", "", "", "", "", "", "", "", "", "", ""
        };
        boolean[] editable = editableForId(labels.length, 0);
        String[] result = FormDialog.showDialog(this, I18n.t("dialog.addPatient"), labels, values, editable);
        if (result == null) {
            return;
        }
        Patient patient = new Patient(
            result[0], result[1], result[2], result[3], result[4], result[5], result[6],
            result[7], result[8], result[9], result[10], result[11], result[12], result[13]
        );
        controller.addPatient(patient);
        updateUsers();
        selectUserById(patient.getPatientId());
    }

    private void selectUserById(String id) {
        for (int i = 0; i < userBox.getItemCount(); i++) {
            UserChoice choice = userBox.getItemAt(i);
            if (choice != null && choice.getId().equalsIgnoreCase(id)) {
                userBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void login() {
        UserChoice choice = (UserChoice) userBox.getSelectedItem();
        if (choice == null) {
            JOptionPane.showMessageDialog(this, I18n.t("message.selectUser"));
            return;
        }
        UserRole role = getSelectedRole();
        session = new UserSession(role, choice.getId(), choice.getName());
        setVisible(false);
        dispose();
    }

    private void cancel() {
        session = null;
        setVisible(false);
        dispose();
    }

    private UserRole getSelectedRole() {
        int index = roleBox.getSelectedIndex();
        if (index == 1) {
            return UserRole.CLINICIAN;
        }
        if (index == 2) {
            return UserRole.ADMIN;
        }
        return UserRole.PATIENT;
    }

    private String[] patientLabels() {
        return new String[]{
            I18n.t("patient.patient_id"),
            I18n.t("patient.first_name"),
            I18n.t("patient.last_name"),
            I18n.t("patient.date_of_birth"),
            I18n.t("patient.nhs_number"),
            I18n.t("patient.gender"),
            I18n.t("patient.phone_number"),
            I18n.t("patient.email"),
            I18n.t("patient.address"),
            I18n.t("patient.postcode"),
            I18n.t("patient.emergency_contact_name"),
            I18n.t("patient.emergency_contact_phone"),
            I18n.t("patient.registration_date"),
            I18n.t("patient.gp_surgery_id")
        };
    }

    private boolean[] editableForId(int length, int idIndex) {
        boolean[] editable = new boolean[length];
        for (int i = 0; i < length; i++) {
            editable[i] = i != idIndex;
        }
        return editable;
    }

    private static class UserChoice {
        private final String id;
        private final String name;

        private UserChoice(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            if (name == null || name.trim().isEmpty()) {
                return id;
            }
            return id + " - " + name;
        }
    }
}
