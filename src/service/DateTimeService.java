package service;

import java.time.LocalDate;

public final class DateTimeService {
    private DateTimeService() {
    }

    public static String today() {
        return LocalDate.now().toString();
    }
}
