package Enums;

public enum Level {
    LEVEL_1,
    LEVEL_2;

    public Level next() {
        Level[] values = Level.values();
        return values[(this.ordinal() + 1) % values.length];
    }
}
