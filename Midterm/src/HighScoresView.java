import Models.HighScores;
import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class HighScoresView extends GameStateView {

    private KeyboardInput inputKeyboard;
    private GameStateEnum nextGameState = GameStateEnum.HighScores;
    private Serializer serializer = new Serializer();
    private HighScores highScores = new HighScores();
    private Font font = new Font("resources/fonts/gunplay3d.otf", 48, false);

    @Override
    public void initialize(Graphics2D graphics) {
        super.initialize(graphics);
        serializer.loadHighScores(this.highScores);


        inputKeyboard = new KeyboardInput(graphics.getWindow());
        // When ESC is pressed, set the appropriate new game state
        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> {
            nextGameState = GameStateEnum.MainMenu;
        });
    }

    @Override
    public void initializeSession() {
        nextGameState = GameStateEnum.HighScores;
        serializer.loadHighScores(this.highScores);
    }

    @Override
    public GameStateEnum processInput(double elapsedTime) {
        // Updating the keyboard can change the nextGameState
        inputKeyboard.update(elapsedTime);
        return nextGameState;
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void render(double elapsedTime) {
        StringBuilder highscores = new StringBuilder();
        highscores.append("High Scores \n");
        int maxscores = 0;
        for (Integer s: this.highScores.getHighScores()){
            if (maxscores < 5){
                highscores.append(s).append("\n");
                maxscores++;
            }
        }
        drawTextWithLeftTopAndColor(highscores.toString(), -0.2f, -0.4f, 0.075f);
    }

    private void drawTextWithLeftTopAndColor(String text, float left, float top, float textHeight){
        String[] stringArr = text.split("\n");
        int idx = 0;
        for (String str: stringArr){
            float newTop = top + (idx * textHeight);
            graphics.drawTextByHeight(font, str, left, newTop, textHeight, Color.WHITE);
            idx++;
        }
    }
}
