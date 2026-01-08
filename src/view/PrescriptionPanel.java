package view;

import controller.AppController;
import model.Prescription;
import model.UserRole;
import model.UserSession;
import service.DateTimeService;
import util.I18n;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionPanel extends JPanel implements Localizable, SessionAware {
    private final AppController controller;
    private final PrescriptionTableModel tableModel;
    private final JTable table;
    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton();
    private final JButton deleteButton = new JButton();
    private final JButton exportButton = new JButton();
    private final List<Prescription> viewPrescriptions = new ArrayList<>();
    private UserSession session;

    public PrescriptionPanel(AppController controller) {
        this.controller = controller;
        this.tableModel = new PrescriptionTableModel(viewPrescriptions);
        this.table = new JTable(tableModel);
        setLayout(new BorderLayout());

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        actions.add(exportButton);
        add(actions, BorderLayout.SOUTH);

        addButton.addActionListener(event -> addPrescription());
        editButton.addActionListener(event -> editPrescription());
        deleteButton.addActionListener(event -> deletePrescription());
        exportButton.addActionListener(event -> exportPrescription());

        updateTexts();
    }

    @Override
    public void setSession(UserSession session) {
        this.session = session;
        refreshView();
        updateActions();
    }

    private void addPrescription() {
        if (!isAdmin() && !isClinician()) {
            return;
        }
        String newId = controller.nextPrescriptionId();
        String today = DateTimeService.today();
        String[] labels = prescriptionLabels();
        String clinicianId = isClinician() ? session.getUserId() : "";
        String[] values = new String[]{
            newId, "", clinicianId, "", today, "", "", "", "", "", "", "", "Issued", today, ""
        };
        boolean[] editable = editableForId(labels.length, 0);
        if (isClinician()) {
            editable[2] = false;
        }
        String[] result = FormDialog.showDialog(this, I18n.t("dialog.addPrescription"), labels, values, editable);
        if (result == null) {
            return;
        }
        Prescription prescription = new Prescription(
            result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7],
            result[8], result[9], result[10], result[11], result[12], result[13], result[14]
        );
        controller.addPrescription(prescription);
        refreshView();
    }

    private void editPrescription() {
        if (!isAdmin() && !isClinician()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Prescription prescription = tableModel.getPrescriptionAt(row);
        String[] labels = prescriptionLabels();
        String[] values = new String[]{
            prescription.getPrescriptionId(),
            prescription.getPatientId(),
            prescription.getClinicianId(),
            prescription.getAppointmentId(),
            prescription.getPrescriptionDate(),
            prescription.getMedicationName(),
            prescription.getDosage(),
            prescription.getFrequency(),
            prescription.getDurationDays(),
            prescription.getQuantity(),
            prescription.getInstructions(),
            prescription.getPharmacyName(),
            prescription.getStatus(),
            prescription.getIssueDate(),
            prescription.getCollectionDate()
        };
        boolean[] editable = editableForId(labels.length, 0);
        if (isClinician()) {
            editable[2] = false;
        }
        String[] result = FormDialog.showDialog(this, I18n.t("dialog.editPrescription"), labels, values, editable);
        if (result == null) {
            return;
        }
        prescription.setPatientId(result[1]);
        prescription.setClinicianId(result[2]);
        prescription.setAppointmentId(result[3]);
        prescription.setPrescriptionDate(result[4]);
        prescription.setMedicationName(result[5]);
        prescription.setDosage(result[6]);
        prescription.setFrequency(result[7]);
        prescription.setDurationDays(result[8]);
        prescription.setQuantity(result[9]);
        prescription.setInstructions(result[10]);
        prescription.setPharmacyName(result[11]);
        prescription.setStatus(result[12]);
        prescription.setIssueDate(result[13]);
        prescription.setCollectionDate(result[14]);
        refreshView();
    }

    private void deletePrescription() {
        if (!isAdmin() && !isClinician()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Prescription prescription = tableModel.getPrescriptionAt(row);
        if (!confirmDelete(prescription.getPrescriptionId())) {
            return;
        }
        controller.deletePrescription(prescription);
        refreshView();
    }

    private boolean confirmDelete(String id) {
        int result = JOptionPane.showConfirmDialog(this,
            I18n.t("confirm.delete", id),
            I18n.t("title.confirm"),
            JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    private void exportPrescription() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Prescription prescription = tableModel.getPrescriptionAt(row);
        try {
            String path = controller.exportPrescription(prescription).toString();
            JOptionPane.showMessageDialog(this, I18n.t("message.exportSuccess", path));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, I18n.t("message.exportFailed"));
        }
    }

    @Override
    public void updateTexts() {
        addButton.setText(I18n.t("button.add"));
        editButton.setText(I18n.t("button.edit"));
        deleteButton.setText(I18n.t("button.delete"));
        exportButton.setText(I18n.t("button.export"));
        tableModel.fireTableStructureChanged();
        updateActions();
    }

    private void refreshView() {
        viewPrescriptions.clear();
        if (session == null) {
            tableModel.fireTableDataChanged();
            return;
        }
        if (session.getRole() == UserRole.ADMIN) {
            viewPrescriptions.addAll(controller.getPrescriptions());
        } else if (session.getRole() == UserRole.PATIENT) {
            for (Prescription prescription : controller.getPrescriptions()) {
                if (prescription.getPatientId().equalsIgnoreCase(session.getUserId())) {
                    viewPrescriptions.add(prescription);
                }
            }
        } else {
            for (Prescription prescription : controller.getPrescriptions()) {
                if (prescription.getClinicianId().equalsIgnoreCase(session.getUserId())) {
                    viewPrescriptions.add(prescription);
                }
            }
        }
        tableModel.fireTableDataChanged();
    }

    private void updateActions() {
        if (session == null) {
            return;
        }
        boolean admin = isAdmin();
        boolean clinician = isClinician();
        addButton.setVisible(admin || clinician);
        editButton.setVisible(admin || clinician);
        deleteButton.setVisible(admin || clinician);
        exportButton.setVisible(true);
    }

    private boolean isAdmin() {
        return session != null && session.getRole() == UserRole.ADMIN;
    }

    private boolean isClinician() {
        return session != null && session.getRole() == UserRole.CLINICIAN;
    }

    private void showSelectRowMessage() {
        JOptionPane.showMessageDialog(this, I18n.t("message.selectRow"));
    }

    private String[] prescriptionLabels() {
        return new String[]{
            I18n.t("prescription.prescription_id"),
            I18n.t("prescription.patient_id"),
            I18n.t("prescription.clinician_id"),
            I18n.t("prescription.appointment_id"),
            I18n.t("prescription.prescription_date"),
            I18n.t("prescription.medication_name"),
            I18n.t("prescription.dosage"),
            I18n.t("prescription.frequency"),
            I18n.t("prescription.duration_days"),
            I18n.t("prescription.quantity"),
            I18n.t("prescription.instructions"),
            I18n.t("prescription.pharmacy_name"),
            I18n.t("prescription.status"),
            I18n.t("prescription.issue_date"),
            I18n.t("prescription.collection_date")
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
