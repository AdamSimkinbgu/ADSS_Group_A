// cli/forms/InteractiveForm.java
package PresentationLayer.CLIs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Optional;

import DTOs.Enums.DayofWeek;
import PresentationLayer.View;

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

    protected EnumSet<DayofWeek> askDaysOfWeek(String prompt) throws Cancelled {
        String line = ask(prompt);
        EnumSet<DayofWeek> days = EnumSet.noneOf(DayofWeek.class);
        String[] dayStrings = line.split(" ");
        for (String dayString : dayStrings) {
            try {
                int dayNumber = Integer.parseInt(dayString);
                if (dayNumber < 1 || dayNumber > 7) {
                    view.showError("Invalid day number: " + dayString);
                } else {
                    days.add(DayofWeek.values()[dayNumber - 1]);
                }
            } catch (NumberFormatException e) {
                view.showError("Invalid input: " + dayString + ". Please enter numbers only.");
            }
        }
        return days;
    }
    
}