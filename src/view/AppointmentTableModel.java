package view;

import model.Appointment;
import util.I18n;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class AppointmentTableModel extends AbstractTableModel {
    private final List<Appointment> appointments;
    private final String[] columns = {
        "appointment.appointment_id",
        "appointment.patient_id",
        "appointment.clinician_id",
        "appointment.facility_id",
        "appointment.appointment_date",
        "appointment.appointment_time",
        "appointment.duration_minutes",
        "appointment.appointment_type",
        "appointment.status",
        "appointment.reason_for_visit",
        "appointment.notes",
        "appointment.created_date",
        "appointment.last_modified"
    };

    public AppointmentTableModel(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public Appointment getAppointmentAt(int row) {
        return appointments.get(row);
    }

    @Override
    public int getRowCount() {
        return appointments.size();
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
        Appointment appointment = appointments.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return appointment.getAppointmentId();
            case 1:
                return appointment.getPatientId();
            case 2:
                return appointment.getClinicianId();
            case 3:
                return appointment.getFacilityId();
            case 4:
                return appointment.getAppointmentDate();
            case 5:
                return appointment.getAppointmentTime();
            case 6:
                return appointment.getDurationMinutes();
            case 7:
                return appointment.getAppointmentType();
            case 8:
                return appointment.getStatus();
            case 9:
                return appointment.getReasonForVisit();
            case 10:
                return appointment.getNotes();
            case 11:
                return appointment.getCreatedDate();
            case 12:
                return appointment.getLastModified();
            default:
                return "";
        }
    }
}
