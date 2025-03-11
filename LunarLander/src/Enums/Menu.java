package Enums;

public enum Menu {
    PLAYGAME,
    HIGHSCORES,
    CREDITS,
    QUIT,
    NONE;

    public Menu next() {
        Menu[] values = Menu.values();
        return values[(this.ordinal() + 1) % values.length];
    }

    public Menu previous() {
        Menu[] values = Menu.values();
        return values[(this.ordinal() - 1 + values.length) % values.length];
    }
}
