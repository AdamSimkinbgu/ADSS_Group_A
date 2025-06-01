// cli/forms/InteractiveForm.java
package Suppliers.PresentationLayer;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Optional;

public abstract class InteractiveForm<T> {

    protected final View view; // your existing console abstraction

    protected InteractiveForm(View view) {
        this.view = view;
    }

    /** blocks until user cancels or we have a valid object */
    public Optional<T> fillBuild() {
        try {
            return Optional.of(build());
        } catch (Cancelled e) {
            view.showMessage("Cancelled.");
            return Optional.empty();
        }
    }

    public Optional<T> fillUpdate(T dto) {
        try {
            return Optional.of(update(dto));
        } catch (Cancelled e) {
            view.showMessage("Cancelled.");
            return Optional.empty();
        }
    }

    protected abstract T build() throws Cancelled;

    protected abstract T update(T dto) throws Cancelled;

    /* ---------- convenience helpers ---------- */

    protected String ask(String prompt) throws Cancelled {
        String line = view.readLine(prompt).trim();
        if (line.equalsIgnoreCase("cancel"))
            throw new Cancelled();
        return line;
    }

    protected String askNonEmpty(String prompt) throws Cancelled {
        String line = ask(prompt);
        while (line.isEmpty()) {
            view.showError("Input cannot be empty. Please try again.");
            line = ask(prompt);
        }
        return line;
    }

    protected static class Cancelled extends Exception {
    }

    protected int askInt(String prompt) throws Cancelled {
        String line = ask(prompt);
        while (!line.matches("\\d+")) {
            view.showError("Invalid input. Please enter a valid integer.");
            line = ask(prompt);
        }
        return Integer.parseInt(line);
    }

    protected BigDecimal askBigDecimal(String prompt) throws Cancelled {
        String line = ask(prompt);
        while (!line.matches("\\d+(\\.\\d+)?")) {
            view.showError("Invalid input. Please enter a valid decimal number.");
            line = ask(prompt);
        }
        return new BigDecimal(line);
    }

    protected boolean askBoolean(String prompt) throws Cancelled {
        String line = ask(prompt);
        while (!line.equalsIgnoreCase("yes") && !line.equalsIgnoreCase("no") ||
                !line.equalsIgnoreCase("true") && !line.equalsIgnoreCase("false") ||
                !line.equalsIgnoreCase("y") && !line.equalsIgnoreCase("n") ||
                !line.equalsIgnoreCase("t") && !line.equalsIgnoreCase("f")) {
            view.showError("Invalid input. Please enter 'yes' or 'no'.");
            line = ask(prompt);
        }
        return line.equalsIgnoreCase("yes");
    }

    protected LocalDate askDate(String prompt) throws Cancelled {
        String line = ask(prompt);
        while (!line.matches("\\d{4}-\\d{2}-\\d{2}")) {
            view.showError("Invalid input. Please enter a valid date in YYYY-MM-DD format.");
            line = ask(prompt);
        }
        return LocalDate.parse(line);
    }

    protected EnumSet<DayOfWeek> askDaysOfWeek(String prompt) throws Cancelled {
        EnumSet<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
        // monday is 1, sunday is 7, shift it so sunday is 1, monday is 2, ..., saturday
        // is 7
        // the user enters a day and we check if it's valid
        while (true) {
            String line = ask(
                    "Supply days (By words, e.g. MONDAY, TUESDAY, ... or numbers, e.g. 1 - MONDAY, 2 - TUESDAY, ..., 7 - SUNDAY): ");
            if (line.isEmpty()) {
                view.showError("Input cannot be empty. Please try again.");
                continue;
            }
            String[] parts = line.split(",");
            for (String part : parts) {
                part = part.trim().toUpperCase();
                try {
                    int dayNumber = Integer.parseInt(part);
                    if (dayNumber < 1 || dayNumber > 7) {
                        view.showError("Invalid day number: " + dayNumber + ". Please enter a number between 1 and 7.");
                        continue;
                    }
                    days.add(DayOfWeek.of((dayNumber % 7) + 1)); // shift to make Sunday the first day
                } catch (NumberFormatException e) {
                    try {
                        days.add(DayOfWeek.valueOf(part));
                    } catch (IllegalArgumentException ex) {
                        view.showError("Invalid day name: " + part + ". Please enter a valid day name.");
                    }
                }
            }
            if (!days.isEmpty()) {
                return days;
            } else {
                view.showError("No valid days entered. Please try again.");
            }
        }
    }
}
