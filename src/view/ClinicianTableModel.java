package view;

import model.Clinician;
import util.I18n;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ClinicianTableModel extends AbstractTableModel {
    private final List<Clinician> clinicians;
    private final String[] columns = {
        "clinician.clinician_id",
        "clinician.first_name",
        "clinician.last_name",
        "clinician.title",
        "clinician.speciality",
        "clinician.gmc_number",
        "clinician.phone_number",
        "clinician.email",
        "clinician.workplace_id",
        "clinician.workplace_type",
        "clinician.employment_status",
        "clinician.start_date"
    };

    public ClinicianTableModel(List<Clinician> clinicians) {
        this.clinicians = clinicians;
    }

    public Clinician getClinicianAt(int row) {
        return clinicians.get(row);
    }

    @Override
    public int getRowCount() {
        return clinicians.size();
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
        Clinician clinician = clinicians.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return clinician.getClinicianId();
            case 1:
                return clinician.getFirstName();
            case 2:
                return clinician.getLastName();
            case 3:
                return clinician.getTitle();
            case 4:
                return clinician.getSpeciality();
            case 5:
                return clinician.getGmcNumber();
            case 6:
                return clinician.getPhoneNumber();
            case 7:
                return clinician.getEmail();
            case 8:
                return clinician.getWorkplaceId();
            case 9:
                return clinician.getWorkplaceType();
            case 10:
                return clinician.getEmploymentStatus();
            case 11:
                return clinician.getStartDate();
            default:
                return "";
        }
    }
}
