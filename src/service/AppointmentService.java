package service;

import model.Appointment;

import java.util.List;

public class AppointmentService {
    private final List<Appointment> appointments;

    public AppointmentService(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public boolean createAppointment(Appointment appointment) {
        if (hasConflict(appointment)) {
            return false;
        }
        appointments.add(appointment);
        return true;
    }

    public void updateAppointment(Appointment appointment) {
        appointment.setLastModified(DateTimeService.today());
    }

    public void cancelAppointment(Appointment appointment) {
        appointment.setStatus("Cancelled");
        appointment.setLastModified(DateTimeService.today());
    }

    private boolean hasConflict(Appointment candidate) {
        for (Appointment existing : appointments) {
            if (!"Cancelled".equalsIgnoreCase(existing.getStatus())
                && existing.getClinicianId().equalsIgnoreCase(candidate.getClinicianId())
                && existing.getAppointmentDate().equalsIgnoreCase(candidate.getAppointmentDate())
                && existing.getAppointmentTime().equalsIgnoreCase(candidate.getAppointmentTime())) {
                return true;
            }
        }
        return false;
    }
}
