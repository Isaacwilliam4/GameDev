package Enums;

public enum PauseSelect {
    CONTINUE,
    QUIT;

    public PauseSelect next() {
        PauseSelect[] values = PauseSelect.values();
        return values[(this.ordinal() + 1) % values.length];
    }

    public PauseSelect previous() {
        PauseSelect[] values = PauseSelect.values();
        return values[(this.ordinal() - 1 + values.length) % values.length];
    }
}
