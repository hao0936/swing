package view;

import model.Facility;
import util.I18n;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class FacilityTableModel extends AbstractTableModel {
    private final List<Facility> facilities;
    private final String[] columns = {
        "facility.facility_id",
        "facility.facility_name",
        "facility.facility_type",
        "facility.address",
        "facility.postcode",
        "facility.phone_number",
        "facility.email",
        "facility.opening_hours",
        "facility.manager_name",
        "facility.capacity",
        "facility.specialities_offered"
    };

    public FacilityTableModel(List<Facility> facilities) {
        this.facilities = facilities;
    }

    public Facility getFacilityAt(int row) {
        return facilities.get(row);
    }

    @Override
    public int getRowCount() {
        return facilities.size();
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
        Facility facility = facilities.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return facility.getFacilityId();
            case 1:
                return facility.getFacilityName();
            case 2:
                return facility.getFacilityType();
            case 3:
                return facility.getAddress();
            case 4:
                return facility.getPostcode();
            case 5:
                return facility.getPhoneNumber();
            case 6:
                return facility.getEmail();
            case 7:
                return facility.getOpeningHours();
            case 8:
                return facility.getManagerName();
            case 9:
                return facility.getCapacity();
            case 10:
                return facility.getSpecialitiesAsString();
            default:
                return "";
        }
    }
}
