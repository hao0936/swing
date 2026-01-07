package view;

import controller.AppController;
import model.Referral;
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
import java.util.Arrays;
import java.util.List;

public class ReferralPanel extends JPanel implements Localizable, SessionAware {
    private final AppController controller;
    private final ReferralTableModel tableModel;
    private final JTable table;
    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton();
    private final JButton deleteButton = new JButton();
    private final JButton exportButton = new JButton();
    private final JButton updateStatusButton = new JButton();
    private final List<Referral> viewReferrals = new ArrayList<>();
    private UserSession session;

    public ReferralPanel(AppController controller) {
        this.controller = controller;
        this.tableModel = new ReferralTableModel(viewReferrals);
        this.table = new JTable(tableModel);
        setLayout(new BorderLayout());

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(addButton);
        actions.add(editButton);
        actions.add(updateStatusButton);
        actions.add(deleteButton);
        actions.add(exportButton);
        add(actions, BorderLayout.SOUTH);

        addButton.addActionListener(event -> addReferral());
        editButton.addActionListener(event -> editReferral());
        updateStatusButton.addActionListener(event -> updateStatus());
        deleteButton.addActionListener(event -> deleteReferral());
        exportButton.addActionListener(event -> exportReferral());

        updateTexts();
    }

    @Override
    public void setSession(UserSession session) {
        this.session = session;
        refreshView();
        updateActions();
    }

    private void addReferral() {
        if (!isAdmin() && !isClinician()) {
            return;
        }
        String newId = controller.nextReferralId();
        String today = DateTimeService.today();
        String[] labels = referralLabels();
        String clinicianId = isClinician() ? session.getUserId() : "";
        String[] values = new String[]{
            newId, "", clinicianId, "", "", "", today, "Routine", "", "", "", "New", "", "", today, today
        };
        boolean[] editable = editableForId(labels.length, 0);
        if (isClinician()) {
            editable[2] = false;
        }
        String[] result = FormDialog.showDialog(this, I18n.t("dialog.addReferral"), labels, values, editable);
        if (result == null) {
            return;
        }
        Referral referral = new Referral(
            result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7],
            result[8], result[9], splitList(result[10]), result[11], result[12], result[13], result[14],
            result[15]
        );
        controller.addReferral(referral);
        refreshView();
    }

    private void editReferral() {
        if (!isAdmin() && !isClinician()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Referral referral = tableModel.getReferralAt(row);
        String[] labels = referralLabels();
        String[] values = new String[]{
            referral.getReferralId(),
            referral.getPatientId(),
            referral.getReferringClinicianId(),
            referral.getReferredToClinicianId(),
            referral.getReferringFacilityId(),
            referral.getReferredToFacilityId(),
            referral.getReferralDate(),
            referral.getUrgencyLevel(),
            referral.getReferralReason(),
            referral.getClinicalSummary(),
            referral.getInvestigationsAsString(),
            referral.getStatus(),
            referral.getAppointmentId(),
            referral.getNotes(),
            referral.getCreatedDate(),
            referral.getLastUpdated()
        };
        boolean[] editable = editableForId(labels.length, 0);
        if (isClinician()) {
            editable[2] = false;
        }
        String[] result = FormDialog.showDialog(this, I18n.t("dialog.editReferral"), labels, values, editable);
        if (result == null) {
            return;
        }
        referral.setPatientId(result[1]);
        referral.setReferringClinicianId(result[2]);
        referral.setReferredToClinicianId(result[3]);
        referral.setReferringFacilityId(result[4]);
        referral.setReferredToFacilityId(result[5]);
        referral.setReferralDate(result[6]);
        referral.setUrgencyLevel(result[7]);
        referral.setReferralReason(result[8]);
        referral.setClinicalSummary(result[9]);
        referral.setRequestedInvestigations(splitList(result[10]));
        referral.setStatus(result[11]);
        referral.setAppointmentId(result[12]);
        referral.setNotes(result[13]);
        referral.setCreatedDate(result[14]);
        referral.setLastUpdated(result[15]);
        controller.updateReferralStatus(referral, referral.getStatus());
        refreshView();
    }

    private void updateStatus() {
        if (!isAdmin() && !isClinician()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Referral referral = tableModel.getReferralAt(row);
        String[] labels = new String[]{I18n.t("referral.status")};
        String[] values = new String[]{referral.getStatus()};
        String[] result = FormDialog.showDialog(this, I18n.t("button.updateStatus"), labels, values, null);
        if (result == null) {
            return;
        }
        controller.updateReferralStatus(referral, result[0]);
        refreshView();
    }

    private void deleteReferral() {
        if (!isAdmin()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Referral referral = tableModel.getReferralAt(row);
        controller.deleteReferral(referral);
        refreshView();
    }

    private void exportReferral() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Referral referral = tableModel.getReferralAt(row);
        try {
            String path = controller.exportReferral(referral).toString();
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
        updateStatusButton.setText(I18n.t("button.updateStatus"));
        tableModel.fireTableStructureChanged();
        updateActions();
    }

    private void refreshView() {
        viewReferrals.clear();
        if (session == null) {
            tableModel.fireTableDataChanged();
            return;
        }
        if (session.getRole() == UserRole.ADMIN) {
            viewReferrals.addAll(controller.getReferrals());
        } else if (session.getRole() == UserRole.PATIENT) {
            for (Referral referral : controller.getReferrals()) {
                if (referral.getPatientId().equalsIgnoreCase(session.getUserId())) {
                    viewReferrals.add(referral);
                }
            }
        } else {
            for (Referral referral : controller.getReferrals()) {
                if (referral.getReferringClinicianId().equalsIgnoreCase(session.getUserId())
                    || referral.getReferredToClinicianId().equalsIgnoreCase(session.getUserId())) {
                    viewReferrals.add(referral);
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
        updateStatusButton.setVisible(admin || clinician);
        deleteButton.setVisible(admin);
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

    private String[] referralLabels() {
        return new String[]{
            I18n.t("referral.referral_id"),
            I18n.t("referral.patient_id"),
            I18n.t("referral.referring_clinician_id"),
            I18n.t("referral.referred_to_clinician_id"),
            I18n.t("referral.referring_facility_id"),
            I18n.t("referral.referred_to_facility_id"),
            I18n.t("referral.referral_date"),
            I18n.t("referral.urgency_level"),
            I18n.t("referral.referral_reason"),
            I18n.t("referral.clinical_summary"),
            I18n.t("referral.requested_investigations"),
            I18n.t("referral.status"),
            I18n.t("referral.appointment_id"),
            I18n.t("referral.notes"),
            I18n.t("referral.created_date"),
            I18n.t("referral.last_updated")
        };
    }

    private boolean[] editableForId(int length, int idIndex) {
        boolean[] editable = new boolean[length];
        for (int i = 0; i < length; i++) {
            editable[i] = i != idIndex;
        }
        return editable;
    }

    private List<String> splitList(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Arrays.asList(new String[0]);
        }
        return Arrays.asList(value.split("\\|"));
    }
}
