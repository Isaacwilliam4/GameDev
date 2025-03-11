package Enums;

public enum GameState {
    MENU,
    PLAYGAME,
    GAMEPAUSED,
    ENDGAME,
    CREDITS,
    HIGHSCORES;

    public GameState next() {
        GameState[] values = GameState.values();
        return values[(this.ordinal() + 1) % values.length];
    }

    public GameState previous() {
        GameState[] values = GameState.values();
        return values[(this.ordinal() - 1 + values.length) % values.length];
    }
}
