package view;

import model.Patient;
import util.I18n;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class PatientTableModel extends AbstractTableModel {
    private final List<Patient> patients;
    private final String[] columns = {
        "patient.patient_id",
        "patient.first_name",
        "patient.last_name",
        "patient.date_of_birth",
        "patient.nhs_number",
        "patient.gender",
        "patient.phone_number",
        "patient.email",
        "patient.address",
        "patient.postcode",
        "patient.emergency_contact_name",
        "patient.emergency_contact_phone",
        "patient.registration_date",
        "patient.gp_surgery_id"
    };

    public PatientTableModel(List<Patient> patients) {
        this.patients = patients;
    }

    public Patient getPatientAt(int row) {
        return patients.get(row);
    }

    @Override
    public int getRowCount() {
        return patients.size();
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
        Patient patient = patients.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return patient.getPatientId();
            case 1:
                return patient.getFirstName();
            case 2:
                return patient.getLastName();
            case 3:
                return patient.getDateOfBirth();
            case 4:
                return patient.getNhsNumber();
            case 5:
                return patient.getGender();
            case 6:
                return patient.getPhoneNumber();
            case 7:
                return patient.getEmail();
            case 8:
                return patient.getAddress();
            case 9:
                return patient.getPostcode();
            case 10:
                return patient.getEmergencyContactName();
            case 11:
                return patient.getEmergencyContactPhone();
            case 12:
                return patient.getRegistrationDate();
            case 13:
                return patient.getGpSurgeryId();
            default:
                return "";
        }
    }
}
