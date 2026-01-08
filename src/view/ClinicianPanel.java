package view;

import controller.AppController;
import model.Clinician;
import model.UserRole;
import model.UserSession;
import util.ClinicianFactory;
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

public class ClinicianPanel extends JPanel implements Localizable, SessionAware {
    private final AppController controller;
    private final ClinicianTableModel tableModel;
    private final JTable table;
    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton();
    private final JButton deleteButton = new JButton();
    private final List<Clinician> viewClinicians = new ArrayList<>();
    private UserSession session;

    public ClinicianPanel(AppController controller) {
        this.controller = controller;
        this.tableModel = new ClinicianTableModel(viewClinicians);
        this.table = new JTable(tableModel);
        setLayout(new BorderLayout());

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        add(actions, BorderLayout.SOUTH);

        addButton.addActionListener(event -> addClinician());
        editButton.addActionListener(event -> editClinician());
        deleteButton.addActionListener(event -> deleteClinician());

        updateTexts();
    }

    @Override
    public void setSession(UserSession session) {
        this.session = session;
        refreshView();
        updateActions();
    }

    private void addClinician() {
        if (!isAdmin()) {
            return;
        }
        String newId = controller.nextClinicianId();
        String[] labels = clinicianLabels();
        String[] values = new String[]{
            newId, "", "", "", "", "", "", "", "", "", "", ""
        };
        boolean[] editable = editableForId(labels.length, 0);
        String[] result = FormDialog.showDialog(this, I18n.t("dialog.addClinician"), labels, values, editable);
        if (result == null) {
            return;
        }
        Clinician clinician = ClinicianFactory.create(
            result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7],
            result[8], result[9], result[10], result[11]
        );
        controller.addClinician(clinician);
        refreshView();
    }

    private void editClinician() {
        if (!isAdmin()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Clinician clinician = tableModel.getClinicianAt(row);
        String[] labels = clinicianLabels();
        String[] values = new String[]{
            clinician.getClinicianId(),
            clinician.getFirstName(),
            clinician.getLastName(),
            clinician.getTitle(),
            clinician.getSpeciality(),
            clinician.getGmcNumber(),
            clinician.getPhoneNumber(),
            clinician.getEmail(),
            clinician.getWorkplaceId(),
            clinician.getWorkplaceType(),
            clinician.getEmploymentStatus(),
            clinician.getStartDate()
        };
        boolean[] editable = editableForId(labels.length, 0);
        String[] result = FormDialog.showDialog(this, I18n.t("dialog.editClinician"), labels, values, editable);
        if (result == null) {
            return;
        }
        clinician.setFirstName(result[1]);
        clinician.setLastName(result[2]);
        clinician.setTitle(result[3]);
        clinician.setSpeciality(result[4]);
        clinician.setGmcNumber(result[5]);
        clinician.setPhoneNumber(result[6]);
        clinician.setEmail(result[7]);
        clinician.setWorkplaceId(result[8]);
        clinician.setWorkplaceType(result[9]);
        clinician.setEmploymentStatus(result[10]);
        clinician.setStartDate(result[11]);
        refreshView();
    }

    private void deleteClinician() {
        if (!isAdmin()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Clinician clinician = tableModel.getClinicianAt(row);
        if (!confirmDelete(clinician.getClinicianId())) {
            return;
        }
        controller.deleteClinician(clinician);
        refreshView();
    }

    private boolean confirmDelete(String id) {
        int result = JOptionPane.showConfirmDialog(this,
            I18n.t("confirm.delete", id),
            I18n.t("title.confirm"),
            JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
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
        viewClinicians.clear();
        if (session == null) {
            tableModel.fireTableDataChanged();
            return;
        }
        if (session.getRole() == UserRole.ADMIN) {
            viewClinicians.addAll(controller.getClinicians());
        } else if (session.getRole() == UserRole.CLINICIAN) {
            for (Clinician clinician : controller.getClinicians()) {
                if (clinician.getClinicianId().equalsIgnoreCase(session.getUserId())) {
                    viewClinicians.add(clinician);
                    break;
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
        addButton.setVisible(admin);
        editButton.setVisible(admin);
        deleteButton.setVisible(admin);
    }

    private boolean isAdmin() {
        return session != null && session.getRole() == UserRole.ADMIN;
    }

    private void showSelectRowMessage() {
        JOptionPane.showMessageDialog(this, I18n.t("message.selectRow"));
    }

    private String[] clinicianLabels() {
        return new String[]{
            I18n.t("clinician.clinician_id"),
            I18n.t("clinician.first_name"),
            I18n.t("clinician.last_name"),
            I18n.t("clinician.title"),
            I18n.t("clinician.speciality"),
            I18n.t("clinician.gmc_number"),
            I18n.t("clinician.phone_number"),
            I18n.t("clinician.email"),
            I18n.t("clinician.workplace_id"),
            I18n.t("clinician.workplace_type"),
            I18n.t("clinician.employment_status"),
            I18n.t("clinician.start_date")
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
