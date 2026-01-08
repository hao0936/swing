package view;

import controller.AppController;
import model.Facility;
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
import java.util.Arrays;
import java.util.List;

public class FacilityPanel extends JPanel implements Localizable, SessionAware {
    private final AppController controller;
    private final FacilityTableModel tableModel;
    private final JTable table;
    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton();
    private final JButton deleteButton = new JButton();
    private UserSession session;

    public FacilityPanel(AppController controller) {
        this.controller = controller;
        this.tableModel = new FacilityTableModel(controller.getFacilities());
        this.table = new JTable(tableModel);
        setLayout(new BorderLayout());

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        add(actions, BorderLayout.SOUTH);

        addButton.addActionListener(event -> addFacility());
        editButton.addActionListener(event -> editFacility());
        deleteButton.addActionListener(event -> deleteFacility());

        updateTexts();
    }

    @Override
    public void setSession(UserSession session) {
        this.session = session;
    }

    private void addFacility() {
        if (!isAdmin()) {
            return;
        }
        String newId = controller.nextFacilityId();
        String[] labels = facilityLabels();
        String[] values = new String[]{
            newId, "", "", "", "", "", "", "", "", "", ""
        };
        boolean[] editable = editableForId(labels.length, 0);
        String[] result = FormDialog.showDialog(this, I18n.t("button.add"), labels, values, editable);
        if (result == null) {
            return;
        }
        Facility facility = new Facility(
            result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7],
            result[8], result[9], splitList(result[10])
        );
        controller.addFacility(facility);
        tableModel.fireTableDataChanged();
    }

    private void editFacility() {
        if (!isAdmin()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Facility facility = tableModel.getFacilityAt(row);
        String[] labels = facilityLabels();
        String[] values = new String[]{
            facility.getFacilityId(),
            facility.getFacilityName(),
            facility.getFacilityType(),
            facility.getAddress(),
            facility.getPostcode(),
            facility.getPhoneNumber(),
            facility.getEmail(),
            facility.getOpeningHours(),
            facility.getManagerName(),
            facility.getCapacity(),
            facility.getSpecialitiesAsString()
        };
        boolean[] editable = editableForId(labels.length, 0);
        String[] result = FormDialog.showDialog(this, I18n.t("button.edit"), labels, values, editable);
        if (result == null) {
            return;
        }
        facility.setFacilityName(result[1]);
        facility.setFacilityType(result[2]);
        facility.setAddress(result[3]);
        facility.setPostcode(result[4]);
        facility.setPhoneNumber(result[5]);
        facility.setEmail(result[6]);
        facility.setOpeningHours(result[7]);
        facility.setManagerName(result[8]);
        facility.setCapacity(result[9]);
        facility.setSpecialitiesOffered(splitList(result[10]));
        tableModel.fireTableRowsUpdated(row, row);
    }

    private void deleteFacility() {
        if (!isAdmin()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            showSelectRowMessage();
            return;
        }
        Facility facility = tableModel.getFacilityAt(row);
        if (!confirmDelete(facility.getFacilityId())) {
            return;
        }
        controller.deleteFacility(facility);
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

    private String[] facilityLabels() {
        return new String[]{
            I18n.t("facility.facility_id"),
            I18n.t("facility.facility_name"),
            I18n.t("facility.facility_type"),
            I18n.t("facility.address"),
            I18n.t("facility.postcode"),
            I18n.t("facility.phone_number"),
            I18n.t("facility.email"),
            I18n.t("facility.opening_hours"),
            I18n.t("facility.manager_name"),
            I18n.t("facility.capacity"),
            I18n.t("facility.specialities_offered")
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
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(value.split("\\|")));
    }

    private boolean isAdmin() {
        return session != null && session.getRole() == UserRole.ADMIN;
    }
}
