import java.util.Date;

public class GameState {

    public GameState() {
        initialized = false;
    }

    public GameState(int score, int level) {
        this.name = "Default Player";
        this.score = score;
        this.level = level;
        this.timeStamp = new Date();

        this.initialized = true;
    }

    public String name;
    public int score;
    public int level;
    public Date timeStamp;
    public boolean initialized = false;
}
