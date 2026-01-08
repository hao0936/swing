package view;

import controller.AppController;
import model.Appointment;
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
import java.util.ArrayList;
import java.util.List;

public class AppointmentPanel extends JPanel implements Localizable, SessionAware {
    private final AppController controller;
    private final AppointmentTableModel tableModel;
    private final JTable table;
    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton();
    private final JButton cancelButton = new JButton();
    private final JButton deleteButton = new JButton();
    private final List<Appointment> viewAppointments = new ArrayList<>();
    private UserSession session;

    public AppointmentPanel(AppController controller) {
        this.controller = controller;
        this.tableModel = new AppointmentTableModel(viewAppointments);
        this.table = new JTable(tableModel);
        setLayout(new BorderLayout());

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(addButton);
        actions.add(editButton);
        actions.add(cancelButton);
        actions.add(deleteButton);
        add(actions, BorderLayout.SOUTH);

        addButton.addActionListener(event -> addAppointment());
        editButton.addActionListener(event -> editAppointment());
        cancelButton.addActionListener(event -> cancelAppointment());
        deleteButton.addActionListener(event -> deleteAppointment());

        updateTexts();
    }

    @Override
    public void setSession(UserSession session) {
        this.session = session;
        refreshView();
        updateActions();
    }

    private void addAppointment() {
        if (!isAdmin() && !isPatient()) {
            return;
        }
        String newId = controller.nextAppointmentId();
        String today = DateTimeService.today();
        String[] labels = appointmentLabels();
        String patientId = isPatient() ? session.getUserId() : "";
        String[] values = new String[]{
            newId, patientId, "", "", "", "", "", "", "Scheduled", "", "", today, today
        };
        boolean[] editable = editableForId(labels.length, 0);
        if (isPatient()) {
            editable[1] = false;
        }
        String[] result = FormDialog.showDialog(this, I18n.t("dialog.addAppointment"), labels, values, editable);
        if (result == null) {
            return;
        }
        Appointment appointment = new Appointment(
            result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7],
            result[8], result[9], result[10], result[11], result[12]
        );
        if (!controller.addAppointment(appointment)) {
            JOptionPane.showMessageDialog(this, I18n.t("message.conflict"));
            return;
        }
        refreshView();
    }

    private void editAppointment() {
        if (!isAdmin() && !isPatient() && !isClinician()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Appointment appointment = tableModel.getAppointmentAt(row);
        String[] labels = appointmentLabels();
        String[] values = new String[]{
            appointment.getAppointmentId(),
            appointment.getPatientId(),
            appointment.getClinicianId(),
            appointment.getFacilityId(),
            appointment.getAppointmentDate(),
            appointment.getAppointmentTime(),
            appointment.getDurationMinutes(),
            appointment.getAppointmentType(),
            appointment.getStatus(),
            appointment.getReasonForVisit(),
            appointment.getNotes(),
            appointment.getCreatedDate(),
            appointment.getLastModified()
        };
        boolean[] editable = editableForId(labels.length, 0);
        if (isPatient()) {
            editable[1] = false;
        }
        String[] result = FormDialog.showDialog(this, I18n.t("dialog.editAppointment"), labels, values, editable);
        if (result == null) {
            return;
        }
        appointment.setPatientId(result[1]);
        appointment.setClinicianId(result[2]);
        appointment.setFacilityId(result[3]);
        appointment.setAppointmentDate(result[4]);
        appointment.setAppointmentTime(result[5]);
        appointment.setDurationMinutes(result[6]);
        appointment.setAppointmentType(result[7]);
        appointment.setStatus(result[8]);
        appointment.setReasonForVisit(result[9]);
        appointment.setNotes(result[10]);
        appointment.setCreatedDate(result[11]);
        appointment.setLastModified(result[12]);
        controller.updateAppointment(appointment);
        refreshView();
    }

    private void deleteAppointment() {
        if (!isAdmin()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Appointment appointment = tableModel.getAppointmentAt(row);
        if (!confirmDelete(appointment.getAppointmentId())) {
            return;
        }
        controller.getAppointments().remove(appointment);
        refreshView();
    }

    private void cancelAppointment() {
        if (!isAdmin() && !isPatient() && !isClinician()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Appointment appointment = tableModel.getAppointmentAt(row);
        if (!confirmCancel(appointment.getAppointmentId())) {
            return;
        }
        controller.cancelAppointment(appointment);
        refreshView();
    }

    private boolean confirmDelete(String id) {
        int result = JOptionPane.showConfirmDialog(this,
            I18n.t("confirm.delete", id),
            I18n.t("title.confirm"),
            JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    private boolean confirmCancel(String appointmentId) {
        int result = JOptionPane.showConfirmDialog(this,
            I18n.t("confirm.cancelAppointment", appointmentId),
            I18n.t("title.confirm"),
            JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    @Override
    public void updateTexts() {
        addButton.setText(I18n.t("button.add"));
        editButton.setText(I18n.t("button.edit"));
        cancelButton.setText(I18n.t("button.cancel"));
        deleteButton.setText(I18n.t("button.delete"));
        tableModel.fireTableStructureChanged();
        updateActions();
    }

    private void refreshView() {
        viewAppointments.clear();
        if (session == null) {
            tableModel.fireTableDataChanged();
            return;
        }
        if (session.getRole() == UserRole.ADMIN) {
            viewAppointments.addAll(controller.getAppointments());
        } else if (session.getRole() == UserRole.PATIENT) {
            for (Appointment appointment : controller.getAppointments()) {
                if (appointment.getPatientId().equalsIgnoreCase(session.getUserId())) {
                    viewAppointments.add(appointment);
                }
            }
        } else {
            for (Appointment appointment : controller.getAppointments()) {
                if (appointment.getClinicianId().equalsIgnoreCase(session.getUserId())) {
                    viewAppointments.add(appointment);
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
        boolean patient = isPatient();
        boolean clinician = isClinician();
        addButton.setVisible(admin || patient);
        editButton.setVisible(admin || patient || clinician);
        cancelButton.setVisible(admin || patient || clinician);
        deleteButton.setVisible(admin);
    }

    private boolean isAdmin() {
        return session != null && session.getRole() == UserRole.ADMIN;
    }

    private boolean isPatient() {
        return session != null && session.getRole() == UserRole.PATIENT;
    }

    private boolean isClinician() {
        return session != null && session.getRole() == UserRole.CLINICIAN;
    }

    private void showSelectRowMessage() {
        JOptionPane.showMessageDialog(this, I18n.t("message.selectRow"));
    }

    private String[] appointmentLabels() {
        return new String[]{
            I18n.t("appointment.appointment_id"),
            I18n.t("appointment.patient_id"),
            I18n.t("appointment.clinician_id"),
            I18n.t("appointment.facility_id"),
            I18n.t("appointment.appointment_date"),
            I18n.t("appointment.appointment_time"),
            I18n.t("appointment.duration_minutes"),
            I18n.t("appointment.appointment_type"),
            I18n.t("appointment.status"),
            I18n.t("appointment.reason_for_visit"),
            I18n.t("appointment.notes"),
            I18n.t("appointment.created_date"),
            I18n.t("appointment.last_modified")
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
