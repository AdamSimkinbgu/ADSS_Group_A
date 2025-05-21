// cli/forms/InteractiveForm.java
package PresentationLayer.CLIs;

import java.util.Optional;

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
        String v;
        do {
            v = ask(prompt);
        } while (v.isBlank());
        return v;
    }

    protected static class Cancelled extends Exception {
    }
}