package view;

import model.Staff;
import util.I18n;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class StaffTableModel extends AbstractTableModel {
    private final List<Staff> staff;
    private final String[] columns = {
        "staff.staff_id",
        "staff.first_name",
        "staff.last_name",
        "staff.role",
        "staff.department",
        "staff.facility_id",
        "staff.phone_number",
        "staff.email",
        "staff.employment_status",
        "staff.start_date",
        "staff.line_manager",
        "staff.access_level"
    };

    public StaffTableModel(List<Staff> staff) {
        this.staff = staff;
    }

    public Staff getStaffAt(int row) {
        return staff.get(row);
    }

    @Override
    public int getRowCount() {
        return staff.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return I18n.t(columns[column]);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Staff member = staff.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return member.getStaffId();
            case 1:
                return member.getFirstName();
            case 2:
                return member.getLastName();
            case 3:
                return member.getRole();
            case 4:
                return member.getDepartment();
            case 5:
                return member.getFacilityId();
            case 6:
                return member.getPhoneNumber();
            case 7:
                return member.getEmail();
            case 8:
                return member.getEmploymentStatus();
            case 9:
                return member.getStartDate();
            case 10:
                return member.getLineManager();
            case 11:
                return member.getAccessLevel();
            default:
                return "";
        }
    }
}
