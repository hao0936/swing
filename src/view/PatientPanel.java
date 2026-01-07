package view;

import controller.AppController;
import model.Appointment;
import model.Patient;
import model.Prescription;
import model.Referral;
import model.UserRole;
import model.UserSession;
import util.I18n;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PatientPanel extends JPanel implements Localizable, SessionAware {
    private final AppController controller;
    private final PatientTableModel tableModel;
    private final JTable table;
    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton();
    private final JButton deleteButton = new JButton();
    private final List<Patient> viewPatients = new ArrayList<>();
    private UserSession session;

    public PatientPanel(AppController controller) {
        this.controller = controller;
        this.tableModel = new PatientTableModel(viewPatients);
        this.table = new JTable(tableModel);
        setLayout(new BorderLayout());

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        add(actions, BorderLayout.SOUTH);

        addButton.addActionListener(event -> addPatient());
        editButton.addActionListener(event -> editPatient());
        deleteButton.addActionListener(event -> deletePatient());

        updateTexts();
    }

    @Override
    public void setSession(UserSession session) {
        this.session = session;
        refreshView();
        updateActions();
    }

    private void addPatient() {
        if (!isAdmin()) {
            return;
        }
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
        refreshView();
    }

    private void editPatient() {
        if (!isAdmin() && !isPatient()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Patient patient = tableModel.getPatientAt(row);
        String[] labels = patientLabels();
        String[] values = new String[]{
            patient.getPatientId(),
            patient.getFirstName(),
            patient.getLastName(),
            patient.getDateOfBirth(),
            patient.getNhsNumber(),
            patient.getGender(),
            patient.getPhoneNumber(),
            patient.getEmail(),
            patient.getAddress(),
            patient.getPostcode(),
            patient.getEmergencyContactName(),
            patient.getEmergencyContactPhone(),
            patient.getRegistrationDate(),
            patient.getGpSurgeryId()
        };
        boolean[] editable = editableForId(labels.length, 0);
        String[] result = FormDialog.showDialog(this, I18n.t("dialog.editPatient"), labels, values, editable);
        if (result == null) {
            return;
        }
        patient.setFirstName(result[1]);
        patient.setLastName(result[2]);
        patient.setDateOfBirth(result[3]);
        patient.setNhsNumber(result[4]);
        patient.setGender(result[5]);
        patient.setPhoneNumber(result[6]);
        patient.setEmail(result[7]);
        patient.setAddress(result[8]);
        patient.setPostcode(result[9]);
        patient.setEmergencyContactName(result[10]);
        patient.setEmergencyContactPhone(result[11]);
        patient.setRegistrationDate(result[12]);
        patient.setGpSurgeryId(result[13]);
        refreshView();
    }

    private void deletePatient() {
        if (!isAdmin()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Patient patient = tableModel.getPatientAt(row);
        controller.deletePatient(patient);
        refreshView();
    }

    @Override
    public void updateTexts() {
        addButton.setText(I18n.t("button.add"));
        editButton.setText(I18n.t("button.edit"));
        deleteButton.setText(I18n.t("button.delete"));
        tableModel.fireTableStructureChanged();
        updateActions();
    }

    private void refreshView() {
        viewPatients.clear();
        if (session == null) {
            tableModel.fireTableDataChanged();
            return;
        }
        UserRole role = session.getRole();
        if (role == UserRole.ADMIN) {
            viewPatients.addAll(controller.getPatients());
        } else if (role == UserRole.PATIENT) {
            Patient patient = controller.findPatient(session.getUserId());
            if (patient != null) {
                viewPatients.add(patient);
            }
        } else {
            Set<String> patientIds = collectClinicianPatients(session.getUserId());
            for (Patient patient : controller.getPatients()) {
                if (patientIds.contains(patient.getPatientId())) {
                    viewPatients.add(patient);
                }
            }
        }
        tableModel.fireTableDataChanged();
    }

    private Set<String> collectClinicianPatients(String clinicianId) {
        Set<String> ids = new HashSet<>();
        for (Appointment appointment : controller.getAppointments()) {
            if (appointment.getClinicianId().equalsIgnoreCase(clinicianId)) {
                ids.add(appointment.getPatientId());
            }
        }
        for (Prescription prescription : controller.getPrescriptions()) {
            if (prescription.getClinicianId().equalsIgnoreCase(clinicianId)) {
                ids.add(prescription.getPatientId());
            }
        }
        for (Referral referral : controller.getReferrals()) {
            if (referral.getReferringClinicianId().equalsIgnoreCase(clinicianId)
                || referral.getReferredToClinicianId().equalsIgnoreCase(clinicianId)) {
                ids.add(referral.getPatientId());
            }
        }
        return ids;
    }

    private void updateActions() {
        if (session == null) {
            return;
        }
        boolean admin = isAdmin();
        boolean patient = isPatient();
        addButton.setVisible(admin);
        deleteButton.setVisible(admin);
        editButton.setVisible(admin || patient);
    }

    private boolean isAdmin() {
        return session != null && session.getRole() == UserRole.ADMIN;
    }

    private boolean isPatient() {
        return session != null && session.getRole() == UserRole.PATIENT;
    }

    private void showSelectRowMessage() {
        JOptionPane.showMessageDialog(this, I18n.t("message.selectRow"));
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
}
