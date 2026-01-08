package view;

import controller.AppController;
import model.AdminStaff;
import model.Staff;
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

public class StaffPanel extends JPanel implements Localizable, SessionAware {
    private final AppController controller;
    private final StaffTableModel tableModel;
    private final JTable table;
    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton();
    private final JButton deleteButton = new JButton();
    private UserSession session;

    public StaffPanel(AppController controller) {
        this.controller = controller;
        this.tableModel = new StaffTableModel(controller.getStaff());
        this.table = new JTable(tableModel);
        setLayout(new BorderLayout());

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        add(actions, BorderLayout.SOUTH);

        addButton.addActionListener(event -> addStaff());
        editButton.addActionListener(event -> editStaff());
        deleteButton.addActionListener(event -> deleteStaff());

        updateTexts();
    }

    @Override
    public void setSession(UserSession session) {
        this.session = session;
    }

    private void addStaff() {
        if (!isAdmin()) {
            return;
        }
        String newId = controller.nextStaffId();
        String[] labels = staffLabels();
        String[] values = new String[]{
            newId, "", "", "", "", "", "", "", "", "", "", ""
        };
        boolean[] editable = editableForId(labels.length, 0);
        String[] result = FormDialog.showDialog(this, I18n.t("button.add"), labels, values, editable);
        if (result == null) {
            return;
        }
        Staff member = new AdminStaff(
            result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7],
            result[8], result[9], result[10], result[11]
        );
        controller.addStaff(member);
        tableModel.fireTableDataChanged();
    }

    private void editStaff() {
        if (!isAdmin()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Staff member = tableModel.getStaffAt(row);
        String[] labels = staffLabels();
        String[] values = new String[]{
            member.getStaffId(),
            member.getFirstName(),
            member.getLastName(),
            member.getRole(),
            member.getDepartment(),
            member.getFacilityId(),
            member.getPhoneNumber(),
            member.getEmail(),
            member.getEmploymentStatus(),
            member.getStartDate(),
            member.getLineManager(),
            member.getAccessLevel()
        };
        boolean[] editable = editableForId(labels.length, 0);
        String[] result = FormDialog.showDialog(this, I18n.t("button.edit"), labels, values, editable);
        if (result == null) {
            return;
        }
        member.setFirstName(result[1]);
        member.setLastName(result[2]);
        member.setRole(result[3]);
        member.setDepartment(result[4]);
        member.setFacilityId(result[5]);
        member.setPhoneNumber(result[6]);
        member.setEmail(result[7]);
        member.setEmploymentStatus(result[8]);
        member.setStartDate(result[9]);
        member.setLineManager(result[10]);
        member.setAccessLevel(result[11]);
        tableModel.fireTableRowsUpdated(row, row);
    }

    private void deleteStaff() {
        if (!isAdmin()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Staff member = tableModel.getStaffAt(row);
        if (!confirmDelete(member.getStaffId())) {
            return;
        }
        controller.deleteStaff(member);
        tableModel.fireTableDataChanged();
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
    }

    private void showSelectRowMessage() {
        JOptionPane.showMessageDialog(this, I18n.t("message.selectRow"));
    }

    private String[] staffLabels() {
        return new String[]{
            I18n.t("staff.staff_id"),
            I18n.t("staff.first_name"),
            I18n.t("staff.last_name"),
            I18n.t("staff.role"),
            I18n.t("staff.department"),
            I18n.t("staff.facility_id"),
            I18n.t("staff.phone_number"),
            I18n.t("staff.email"),
            I18n.t("staff.employment_status"),
            I18n.t("staff.start_date"),
            I18n.t("staff.line_manager"),
            I18n.t("staff.access_level")
        };
    }

    private boolean[] editableForId(int length, int idIndex) {
        boolean[] editable = new boolean[length];
        for (int i = 0; i < length; i++) {
            editable[i] = i != idIndex;
        }
        return editable;
    }

    private boolean isAdmin() {
        return session != null && session.getRole() == UserRole.ADMIN;
    }
}
