package Suppliers.PresentationLayer;

public abstract class AbstractCommand implements CommandInterface {

    protected final View view;

    public AbstractCommand(View view) {
        this.view = view;
    }

    @Override
    public abstract void execute();

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
