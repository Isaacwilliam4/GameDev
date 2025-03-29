package Models;

import java.util.List;

public class HighScores {
    private List<Integer> highScores;

    public HighScores() {
    }

    public void addToHighscores(Integer score){
        this.highScores.add(score);
    }

    public List<Integer> getHighScores() {
        return highScores;
    }

    public void setHighScores(List<Integer> highScores) {
        this.highScores = highScores;
    }
}
