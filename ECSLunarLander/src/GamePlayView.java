import edu.usu.graphics.Graphics2D;

import static org.lwjgl.glfw.GLFW.*;

public class GamePlayView extends GameStateView {

    private KeyboardInput inputKeyboard;
    private GameStateEnum nextGameState = GameStateEnum.GamePlay;
    private GameModel gameModel;

    @Override
    public void initialize(Graphics2D graphics) {
        super.initialize(graphics);

        inputKeyboard = new KeyboardInput(graphics.getWindow());
        // When ESC is pressed, set the appropriate new game state
        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> {
            nextGameState = GameStateEnum.MainMenu;
        });
    }

    @Override
    public void initializeSession() {
        gameModel = new GameModel();
        gameModel.initialize(graphics);
        nextGameState = GameStateEnum.GamePlay;
    }

    @Override
    public GameStateEnum processInput(double elapsedTime) {
        // Updating the keyboard can change the nextGameState
        inputKeyboard.update(elapsedTime);
        return nextGameState;
    }

    @Override
    public void update(double elapsedTime) {
        gameModel.update(elapsedTime);
    }

    @Override
    public void render(double elapsedTime) {
        // Nothing to do because the render now occurs in the update of the game model
    }
}
