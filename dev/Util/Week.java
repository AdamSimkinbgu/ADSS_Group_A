package Util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

public record Week(int year, int week) implements Comparable<Week> {
    private static final WeekFields SUNDAY_START_WEEK = WeekFields.of(DayOfWeek.SUNDAY, 1);

    public static Week from(LocalDate date) {
        int week = date.get(SUNDAY_START_WEEK.weekOfWeekBasedYear());
        int year = date.get(SUNDAY_START_WEEK.weekBasedYear());
        return new Week(year, week);
    }

    public static LocalDate getNextSunday(LocalDate fromDate) {
        int daysUntilSunday = (DayOfWeek.SUNDAY.getValue() - fromDate.getDayOfWeek().getValue() + 7) % 7;
        if (daysUntilSunday == 0) daysUntilSunday = 7;
        return fromDate.plusDays(daysUntilSunday);
    }

    @Override
    public int compareTo(Week other) {
        if (this.year != other.year) {
            return Integer.compare(this.year, other.year);
        }
        return Integer.compare(this.week, other.week);
    }
}
